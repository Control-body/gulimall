package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponSpuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 * 
 * @author ${author}
 * @email 55333@qq.com
 * @date 2022-07-05 20:11:54
 */
@Mapper
public interface CouponSpuRelationDao extends BaseMapper<CouponSpuRelationEntity> {
	
}
