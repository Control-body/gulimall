package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author ${author}
 * @email 55333@qq.com
 * @date 2022-07-05 20:25:21
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
