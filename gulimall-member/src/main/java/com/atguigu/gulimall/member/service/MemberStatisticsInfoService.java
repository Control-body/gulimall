package com.atguigu.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author ${author}
 * @email 55333@qq.com
 * @date 2022-07-05 20:25:21
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

