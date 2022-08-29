package com.atguigu.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GulimallSearchApplicationTests {
    @Autowired
    private RestHighLevelClient client;
    @Data
    class User{
        private String name;
        private String gender;
        private Integer age;
    }


    /**
     * Auto-generated: 2022-07-29 23:26:22
     *
     * @author json.cn (i@json.cn)
     * @website http://www.json.cn/java2pojo/
     */
    @Data
    @ToString
    static class JsonRootBean {

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }
    @Test
    void contextLoads() {
        System.out.println(client);

    }

    @Test
    public  void indexTest() throws IOException {
        IndexRequest request = new IndexRequest("users");
        request.id("1");
        User user = new User();
        user.setAge(18);
        user.setName("liubin");
        user.setGender("nan");
        String jsonString = JSON.toJSONString(user);
        request.source(jsonString,XContentType.JSON);
//       执行操作
        IndexResponse index = client.index(request, GulimallElasticSearchConfig.COMMON_OPTIONS);

    }

    /**
     * 测试复杂检索功能
     * @throws IOException
     */
    @Test
    public  void searchTest() throws IOException{
//        创建检索请求
        SearchRequest searchRequest = new SearchRequest();
//        指定索引
        searchRequest.indices("bank");
//        指定条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        构造检索条件
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));


//      聚合的工具类
//        1.按照年龄聚合
        TermsAggregationBuilder ageAvg = AggregationBuilders.terms("ageAvg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAvg);
//        2.按照年龄进行平均聚合
        AvgAggregationBuilder field = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(field);
        System.out.println("检索条件----------"+searchSourceBuilder.toString()+"---------");

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

//        结果分析 封装对象
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit item : hits1) {
            String sourceAsString = item.getSourceAsString();
            System.out.println(sourceAsString);
            JsonRootBean jsonObject = JSON.parseObject(sourceAsString,JsonRootBean.class);
            System.out.println(
                    jsonObject.toString()
            );

        }
//        分析结果数据的 封装
        // 3.2 获取检索到的分析信息
        Aggregations aggregations = searchResponse.getAggregations();
        Terms agg21 = aggregations.get("ageAvg");
        for (Terms.Bucket bucket : agg21.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println(keyAsString);
        }


    }

}
