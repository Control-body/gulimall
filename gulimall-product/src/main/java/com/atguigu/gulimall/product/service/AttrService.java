package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author Control
 * @email 1741642120@qq.com
 * @date 2022-07-05 17:39:36
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

