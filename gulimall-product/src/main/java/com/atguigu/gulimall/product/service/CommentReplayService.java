package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CommentReplayEntity;

import java.util.Map;

/**
 * 商品评价回复关系
 *
 * @author Control
 * @email 1741642120@qq.com
 * @date 2022-07-05 17:39:36
 */
public interface CommentReplayService extends IService<CommentReplayEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

