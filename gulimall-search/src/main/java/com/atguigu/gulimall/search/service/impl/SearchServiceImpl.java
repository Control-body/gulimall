package com.atguigu.gulimall.search.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.EsConstant;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import com.atguigu.gulimall.search.feign.ProductFeignService;
import com.atguigu.gulimall.search.service.SearchService;
import com.atguigu.gulimall.search.vo.*;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.ParsedAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/5 19:16
 *
 * @author Control.
 * @since JDK 1.8
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    ProductFeignService productAttrValueService;
    @Autowired
    private RestHighLevelClient client;
    @Override
    public SearchResult search(SearchParam Param) {
        SearchResult searchResult = null;
//       准备检索请求
        SearchRequest searchRequest = buildSearchRequest(Param);

        try {
//            执行检索请求
            SearchResponse search = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

//            分析相应数据 封装成我们需要的格式
            searchResult=buildSearchResult(search,Param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    /**
     * 根据返回结果 封装数据
     * @param search
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse search,SearchParam Param) {

        SearchResult searchResult = new SearchResult();
//        1.返回查询到的商品
        SearchHits hits = search.getHits();
        SearchHit[] hits1 = hits.getHits();

        List<SkuEsModel> skuEsModels = new ArrayList<>();
        if (hits1 != null && hits1.length > 0) {
            for (SearchHit documentFields : hits1) {
                String sourceAsString = documentFields.getSourceAsString();
                SkuEsModel skuEsModel = new SkuEsModel();
                skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if (!StringUtils.isEmpty(Param.getKeyword())) {
                    HighlightField skuTitle = documentFields.getHighlightFields().get("skuTitle");
                    String string = skuTitle.getFragments()[0].string();
                    skuEsModel.setSkuTitle(string);
                }

                skuEsModels.add(skuEsModel);
            }
        }
        searchResult.setProducts(skuEsModels);
//        2.封装 过滤参数
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        Aggregations aggregations = search.getAggregations();
        ParsedLongTerms catalog_agg = aggregations.get("catalog_agg");
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            String keyAsString = bucket.getKeyAsString();
//            分类ID
            catalogVo.setCatalogId(Long.parseLong(keyAsString));
//            分类的名字
            Aggregations aggregations1 = bucket.getAggregations();
            ParsedStringTerms brand_name_agg = aggregations1.get("catalog_name_agg");
            String name = brand_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(name);
            catalogVos.add(catalogVo);
        }
        searchResult.setCatalogs(catalogVos);

//       品牌聚合
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = search.getAggregations().get("brand_agg");
        List<? extends Terms.Bucket> buckets1 = brand_agg.getBuckets();
        for (Terms.Bucket bucket : buckets1) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
//           品牌的ID
            long brand_Id = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brand_Id);
//           品牌的名字
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            String brand_name = brand_name_agg.getBuckets().get(0).getKeyAsString();
//           品牌的图片
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            String brand_img = brand_img_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brand_img);
            brandVo.setBrandName(brand_name);
            brandVos.add(brandVo);
        }
        searchResult.setBrands(brandVos);
//          属性信息
        ArrayList<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = search.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            // 2.1 得到属性的id
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
            // 2.2 得到属性的名字
            String attr_name = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attr_name);
            // 2.3 得到属性的所有值
            List<String> attr_value = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(
                            item -> item.getKeyAsString())
                    .collect(Collectors.toList()
                    );
            attrVo.setAttrValue(attr_value);
            attrVos.add(attrVo);
        }
        searchResult.setAttrs(attrVos);


//  当前页  参数中的页码
        searchResult.setPageNum(Param.getPageNum());
//        分页信息
        long total = hits.getTotalHits().value;
        searchResult.setTotal(total);
//       总页码
        int totalPages = (int) total % EsConstant.PRODUCT_PASIZE == 0 ?
                (int) total / EsConstant.PRODUCT_PASIZE : ((int) total / EsConstant.PRODUCT_PASIZE + 1);
        searchResult.setTotalPages(totalPages);
//        增加页数
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        searchResult.setPageNavs(pageNavs);
//        增加面包屑导航
        List<SearchResult.NavVo> navVo = new ArrayList<>();
        if (Param.getAttrs() != null && Param.getAttrs().size() > 0) {
            navVo = Param.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo1 = new SearchResult.NavVo();
                String[] s = attr.split("_");
//            需要远程调用商品 服务获取 姓名
                navVo1.setNavValue(s[1]);
                R attrsInfo = productAttrValueService.getAttrsInfo(Long.parseLong(s[0]));
//                将面包屑中有的属性值去掉
                searchResult.getAttrIds().add(Long.parseLong(s[0]));
                if (attrsInfo.getCode() == 0) {
                    AttrResponseVo attr1 = attrsInfo.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    navVo1.setName(attr1.getAttrName());
                } else {
                    navVo1.setName(s[0]);
                }
//                设置 替换后的地址
//                String encode = null;
//                try {
//                    encode = URLEncoder.encode(attr, "UTF-8");
//                    encode = encode.replace("+", "%20");// 浏览器对空格的差异化处理
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                String replace=replaceQueryString(Param, attr + "", "attrs");
//                String replace = Param.get_queryString().replace("&attrs=" + encode, "");
                navVo1.setLink("http://search.gulimall.com/list.html?" + replace);
//             当取消这个歌面包屑之后

                return navVo1;
            }).collect(Collectors.toList());
        }
        searchResult.setNavs(navVo);
//        return searchResult;
//    }
        // 品牌、分类
        if(Param.getBrandId() != null && Param.getBrandId().size() > 0){
            List<SearchResult.NavVo> navs = searchResult.getNavs();
            SearchResult.NavVo navVo1 = new SearchResult.NavVo();
            navVo1.setName("品牌");
            // TODO 远程查询所有品牌
            R r = productAttrValueService.brandInfo(Param.getBrandId());
            if(r.getCode() == 0){
                List<BrandVOLB> brands = r.getData("brands", new TypeReference<List<BrandVOLB>>() {
                });
                StringBuffer buffer = new StringBuffer();
                // 替换所有品牌ID
                String replace = "";
                for (BrandVOLB brandVo : brands) {
                    buffer.append(brandVo.getName() + ";");
                    replace = replaceQueryString(Param, brandVo.getBrandId() + "", "brandId");
                }
                navVo1.setNavValue(buffer.toString());
                navVo1.setLink("http://search.gulimall.com/list.html?" + replace);
            }
            navs.add(navVo1);
        }
        return searchResult;
    }
    /**
     * 替换字符
     * key ：需要替换的key
     */
    private String replaceQueryString(SearchParam Param, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value,"UTF-8");
            // 浏览器对空格的编码和java的不一样
            encode = encode.replace("+","%20");
            encode = encode.replace("%28", "(").replace("%29",")");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String queryString = Param.get_queryString();
        String[] split = queryString.split("=");

        if(key.equals(split[0])){
            return Param.get_queryString().replace(key + "=" + encode, "");
        }else {
            return Param.get_queryString().replace("&" + key + "=" + encode, "");
        }
    }
    /**
     * 准备检索请求
     * @return
     * @param param
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
//       构建 查询 dsl 语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
// 1. 模糊匹配 过滤(按照属性、分类、品牌、价格区间、库存) 先构建一个布尔Query

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

// 1.1 must
        if(!StringUtils.isEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
// 1.2 filter
        if(param.getCatalog3Id()!=null){
            boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
        }
        if(param.getBrandId()!=null && param.getBrandId().size()>0){
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }
//        商品属性的查询
        if(param.getAttrs()!=null && param.getAttrs().size()>0){
            for (String attr : param.getAttrs()) {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                String attrId=s[0];
                String[] attrValue = s[1].split(":");
                boolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                boolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue",attrValue));
//                每一个都生成的一个nest查询方式
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", boolQueryBuilder, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }
//        是否有库存
        if(param.getHasStock()!=null){
            boolQuery.filter(QueryBuilders.termQuery("hasStock",param.getHasStock()==1));
        }

//      价格区间
        if(!StringUtils.isEmpty(param.getSkuPrice())){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = param.getSkuPrice().split("_");
            if(s.length== 2){
                rangeQuery.gte(s[0]).lte(s[1]);
            }else
            if(s.length== 1){
                if(param.getSkuPrice().startsWith("_")){
                    rangeQuery.lte(s[0]);
                }
                if(param.getSkuPrice().endsWith("_")){
                    rangeQuery.gte(s[0]);
                }
            }
            boolQuery.filter(rangeQuery);
        }
        sourceBuilder.query(boolQuery);
// 2.排序
      if(!StringUtils.isEmpty(param.getSort())){
          String sort = param.getSort();
          String[] s = sort.split("_");
          SortOrder sortOrder = s[1].equalsIgnoreCase("asc")?SortOrder.ASC:SortOrder.DESC;
          sourceBuilder.sort(s[0],sortOrder);
      }

        // 3.分页 pageSize ： 5
        sourceBuilder.size(EsConstant.PRODUCT_PASIZE);
        sourceBuilder.from((param.getPageNum()-1)*EsConstant.PRODUCT_PASIZE);

        // 4.高亮
        if(!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        // 聚合分析
        // TODO 1.品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
//         1.1 品牌的子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        sourceBuilder.aggregation(brand_agg);
//    TODO 分类聚合
        TermsAggregationBuilder catalogId_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        catalogId_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName"));
        sourceBuilder.aggregation(catalogId_agg);

//  TODO 属性聚合
        NestedAggregationBuilder nested = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));

        nested.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(nested);


        String s = sourceBuilder.toString();
        System.out.println(" 构建的DSL---------"+s);

        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);



        return searchRequest;

    }
}
