package com.atguigu.gulimall.gulimallauthserver.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/12 10:38
 *
 * @author Control.
 * @since JDK 1.8
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartFeignService {
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone")String phone, @RequestParam("code") String code);

}
