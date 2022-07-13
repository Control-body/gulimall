package com.atguigu.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.OrderItemEntity;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author ${author}
 * @email 55333@qq.com
 * @date 2022-07-05 20:34:26
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

