package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author Control
 * @email 1741642120@qq.com
 * @date 2022-07-05 17:39:36
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

