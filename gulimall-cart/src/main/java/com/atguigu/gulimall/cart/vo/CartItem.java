package com.atguigu.gulimall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>Title: CartItem</p>
 * Description：购物项
 */
@Data
public class CartItem {

	private Long skuId;

	/*** 是否被选中*/
	private Boolean check = true;

	private String title;
	private String image;

	private List<String> skuAttr;

	/** 价格、数量、单项总价 */
	private BigDecimal price;
	private Integer count;
	private BigDecimal totalPrice;

	/**
	 * 手动计算sku总价
	 */
	public BigDecimal getTotalPrice() {
		return this.price.multiply(new BigDecimal("" + this.count));
	}

}
