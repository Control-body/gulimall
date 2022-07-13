package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SpuCommentEntity;

import java.util.Map;

/**
 * 商品评价
 *
 * @author Control
 * @email 1741642120@qq.com
 * @date 2022-07-05 17:39:36
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

