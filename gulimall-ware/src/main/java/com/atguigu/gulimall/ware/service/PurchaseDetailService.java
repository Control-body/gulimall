package com.atguigu.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author ${author}
 * @email 55333@qq.com
 * @date 2022-07-05 20:39:22
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

