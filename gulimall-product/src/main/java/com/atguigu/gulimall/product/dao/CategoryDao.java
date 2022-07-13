package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author Control
 * @email 1741642120@qq.com
 * @date 2022-07-05 17:39:36
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
