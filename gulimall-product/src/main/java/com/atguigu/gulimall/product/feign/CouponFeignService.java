package com.atguigu.gulimall.product.feign;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/7/27 15:27
 *
 * @author Control.
 * @since JDK 1.8
 */

import com.atguigu.common.to.SkuReductionTO;
import com.atguigu.common.to.SpuBoundTO;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 */
@FeignClient("gulimall-coupon")

public interface CouponFeignService {
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTO spuBoundTO);
    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction (@RequestBody SkuReductionTO skuReductionTo);
}
