package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuReductionTO;
import com.atguigu.common.to.SpuBoundTO;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.to.es.SkuHasStockVo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WereFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.*;
import com.google.errorprone.annotations.Var;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
   SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
//    调用远程的接口
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    WereFeignService wereFeignService;
    @Autowired
    SearchFeignService searchFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
//        1. 保存sou的基本信息
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);
//        2. 保存spu描述的图片
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);
//        3.保存spu的图片集
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);
//        4.保存spu的规格参数
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            Long attrId = attr.getAttrId();
            productAttrValueEntity.setAttrId(attrId);
            AttrEntity byId = attrService.getById(attrId);
            productAttrValueEntity.setAttrName(byId.getAttrName());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveproductAttr(collect);
//        5.保存 积分 信息
        Bounds bounds = vo.getBounds();
        SpuBoundTO spuBoundTO = new SpuBoundTO();
        BeanUtils.copyProperties(bounds,spuBoundTO);
        spuBoundTO.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTO);
        if(r.getCode() != 0){
            log.error("远程保存积分信息失败");
        }
//        6。保存spu对应的sku信息
//         6.1 spu的基本星系
        List<Skus> skus = vo.getSkus();
        skus.forEach(item ->{
            String defaultImg="";
            for (Images image: item.getImages()){
                if(image.getDefaultImg()==1){
                    defaultImg=image.getImgUrl();
                }
            }
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(item,skuInfoEntity);
            skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfoEntity.setSaleCount(0L);
            skuInfoEntity.setSpuId(spuInfoEntity.getId());
            skuInfoEntity.setSkuDefaultImg(defaultImg);
            skuInfoService.saveSkuInfo(skuInfoEntity);
//            获取自增组件
            Long skuId = skuInfoEntity.getSkuId();
            List<SkuImagesEntity> collect1 = item.getImages().stream().map(img -> {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setSkuId(skuId);
                skuImagesEntity.setImgUrl(img.getImgUrl());
                skuImagesEntity.setDefaultImg(img.getDefaultImg());
                return skuImagesEntity;
            }).filter(entity -> {
                return !StringUtils.isEmpty(entity.getImgUrl());
            }).collect(Collectors.toList());
//            todo 没有图片的路径无需进行保存
            skuImagesService.saveBatch(collect1);
//            保存sku的销售属性
            List<Attr> attr = item.getAttr();
            List<SkuSaleAttrValueEntity> collect2 = attr.stream().map(a -> {
                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                skuSaleAttrValueEntity.setSkuId(skuId);
                return skuSaleAttrValueEntity;
            }).collect(Collectors.toList());
            skuSaleAttrValueService.saveBatch(collect2);
//          6.4 sku的优惠券信息 需要调用远程服务
            SkuReductionTO skuReductionTO = new SkuReductionTO();
            BeanUtils.copyProperties(item,skuReductionTO);
            skuReductionTO.setSkuId(skuId);
//            只要有满减信息就发送保存
            if(skuReductionTO.getFullCount()>0||skuReductionTO.getFullPrice().compareTo(new BigDecimal("0"))==1){
                R r1 = couponFeignService.saveSkuReduction(skuReductionTO);
                if(r1.getCode() != 0){
                    log.error("远程保存优惠券信息失败");
                }
            }
        });

    }

    /**
     * 根据key等信息 进行查询
     * @param params
     * @return
     */
    /**
     * spu管理模糊查询
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        // 根据 spu管理带来的条件进行叠加模糊查询
//         使用lamda表达式的方式 是为了形成 status=1 and （id= 1 or spu_name like “xx”）
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w -> w.eq("id", key).or().like("spu_name", key));
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 保存进es 修改状态
     * @param spuId
     */
    @Override
    public void up(Long spuId) {
//       组装需保存进 es的类型
//        查到spu对饮的sku
        List<SkuInfoEntity> spuInfoEntities=skuInfoService.getSkuBySpuId(spuId);

 //            todo 所有的 可以被检索的规格属性
//            查询所有的规格属性
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseListforspu(spuId);
        List<Long> collect = productAttrValueEntities.stream().map((item) -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        List<Long> searchAttrIds= attrService.selectSearchAttrs(collect);
//       联转换成set 看是否 在集合中
        Set<Long> idSet=new HashSet<Long>(searchAttrIds);

        List<SkuEsModel.Attrs> endpspuAttr = productAttrValueEntities.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item->{
            SkuEsModel.Attrs Attr = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, Attr);
            return Attr;
        }).collect(Collectors.toList());
//            todo 发送远程服务 查看是否 有库存
        // 每件skuId是否有库存
        Map<Long, Boolean> stockMap = null;
        List<Long> spuIdList = spuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        try{
            R skuHasStock = wereFeignService.getSkuHasStock(spuIdList);
//            构造器是受保护的 匿名内部类
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {};
            List<SkuHasStockVo> data = skuHasStock.getData(typeReference);
            stockMap = data.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
            log.warn("服务调用成功" + data);
        }catch (Exception e){
            log.error("库存服务调用失败: 原因{}", e);
        }
        Map<Long, Boolean> finalStockMap = stockMap;//防止lambda中改变

        List<SkuEsModel> endEsmodel = spuInfoEntities.stream().map((item) -> {
            SkuEsModel skuEsMode = new SkuEsModel();
            BeanUtils.copyProperties(item, skuEsMode);
            skuEsMode.setSkuPrice(item.getPrice());
            skuEsMode.setSkuImg(item.getSkuDefaultImg());
//            todo 发送远程服务 查看是否 有库存
//            是否有库存
            // 4 设置库存，只查是否有库存，不查有多少

            if (finalStockMap == null) {
                skuEsMode.setHasStock(true);
            } else {
                skuEsMode.setHasStock(finalStockMap.get(item.getSkuId()));
            }
//            todo 2.热度评分 默认为0
            skuEsMode.setHotScore(0L);
//            todu 品牌的信息
            BrandEntity bandId = brandService.getById(item.getBrandId());
            skuEsMode.setBrandImg(bandId.getLogo());
            skuEsMode.setBrandName(bandId.getName());

            CategoryEntity categoryId = categoryService.getById(item.getCatalogId());
            skuEsMode.setCatalogName(categoryId.getName());
//            设置检索属性
            skuEsMode.setAttrs(endpspuAttr);

            return skuEsMode;
        }).collect(Collectors.toList());
//        todo 发送给 第三方的服务


        R r = searchFeignService.productStatusUp(endEsmodel);
        Integer code = r.getCode();
        if(code==0){
//            远程调用成功
//            TODO 修改spu的 上架状态
            baseMapper.updateStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else{
//            远程失败
//            TODO 接口幂等等（接口调用失败是否重新调用）

        }
    }


    //保存sou的基本信息
    private void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

}