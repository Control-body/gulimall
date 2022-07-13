package com.atguigu.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;

import java.util.Map;

/**
 * 采购信息
 *
 * @author ${author}
 * @email 55333@qq.com
 * @date 2022-07-05 20:39:22
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

