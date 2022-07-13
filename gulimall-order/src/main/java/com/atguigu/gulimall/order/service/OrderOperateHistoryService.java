package com.atguigu.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.order.entity.OrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author ${author}
 * @email 55333@qq.com
 * @date 2022-07-05 20:34:26
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

