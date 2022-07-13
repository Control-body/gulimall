package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;

import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author Control
 * @email 1741642120@qq.com
 * @date 2022-07-05 17:39:36
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

