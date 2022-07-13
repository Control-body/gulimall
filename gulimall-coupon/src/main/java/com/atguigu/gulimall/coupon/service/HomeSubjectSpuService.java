package com.atguigu.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.coupon.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * 专题商品
 *
 * @author ${author}
 * @email 55333@qq.com
 * @date 2022-07-05 20:11:54
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

