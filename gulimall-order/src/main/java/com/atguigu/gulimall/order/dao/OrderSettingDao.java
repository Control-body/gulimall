package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author ${author}
 * @email 55333@qq.com
 * @date 2022-07-05 20:34:26
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
