package com.atguigu.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.coupon.entity.CouponEntity;

import java.util.Map;

/**
 * 优惠券信息
 *
 * @author ${author}
 * @email 55333@qq.com
 * @date 2022-07-05 20:11:54
 */
public interface CouponService extends IService<CouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

