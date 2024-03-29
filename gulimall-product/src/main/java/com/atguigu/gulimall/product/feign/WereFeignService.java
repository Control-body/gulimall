package com.atguigu.gulimall.product.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/7/31 17:36
 *
 * @author Control.
 * @since JDK 1.8
 */
@FeignClient("gulimall-ware")
public interface WereFeignService {

    /**
     * 修改真个系统的 R 带上泛型
     */
    @PostMapping("/ware/waresku/hasStock")
//	List<SkuHasStockVo> getSkuHasStock(@RequestBody List<Long> SkuIds);
    R getSkuHasStock(@RequestBody List<Long> SkuIds);
}
