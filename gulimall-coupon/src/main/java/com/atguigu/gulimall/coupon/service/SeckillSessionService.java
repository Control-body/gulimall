package com.atguigu.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.coupon.entity.SeckillSessionEntity;

import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author ${author}
 * @email 55333@qq.com
 * @date 2022-07-05 20:11:54
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

