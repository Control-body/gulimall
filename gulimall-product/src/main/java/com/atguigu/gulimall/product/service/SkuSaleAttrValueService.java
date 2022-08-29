package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.ItemSaleAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author Control
 * @email 1741642120@qq.com
 * @date 2022-07-05 17:39:36
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    List<ItemSaleAttrVo> getSalesAttrsBySpuId(Long spuId) ;

    PageUtils queryPage(Map<String, Object> params);

    List<String> getSkuSaleAttrValues(Long skuId);
}

