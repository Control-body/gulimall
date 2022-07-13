package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderOperateHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单操作历史记录
 * 
 * @author ${author}
 * @email 55333@qq.com
 * @date 2022-07-05 20:34:26
 */
@Mapper
public interface OrderOperateHistoryDao extends BaseMapper<OrderOperateHistoryEntity> {
	
}
