package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/7/31 19:02
 *
 * @author Control.
 * @since JDK 1.8
 */
@FeignClient("gulimall-search")
public interface SearchFeignService {
    /*** 上架商品*/
    @PostMapping("/search/save/product") // ElasticSaveController
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
