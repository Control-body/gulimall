package com.atguigu.gulimall.product.entity;

import com.atguigu.common.valid.AddGroup;
import com.atguigu.common.valid.ListValue;
import com.atguigu.common.valid.UpdateGroup;
import com.atguigu.common.valid.UpdateStatusGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author Control
 * @email 1741642120@qq.com
 * @date 2022-07-05 17:39:36
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message="修改必须指定品牌ID",groups={UpdateGroup.class})
	@Null(message="新增不能指定Id",groups={AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message="品牌名字不能为空" ,groups={UpdateGroup.class,AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(groups={AddGroup.class})
	@URL(message="logo必须是一个合法的url的地址",groups={UpdateGroup.class,AddGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	@NotEmpty(message="介绍不能为空")
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(message="显示状态不能为空",groups={AddGroup.class, UpdateStatusGroup.class})
	@ListValue(vals = {0,1},groups={AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(message="检索首字母不能为空",groups={AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$",message="检索首字母必须是一个字母",groups={UpdateGroup.class,AddGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
//	@NotNull
	@Min(value=0 ,message="校验的数据必须大于0",groups={UpdateGroup.class,AddGroup.class})
	private Integer sort;

}
