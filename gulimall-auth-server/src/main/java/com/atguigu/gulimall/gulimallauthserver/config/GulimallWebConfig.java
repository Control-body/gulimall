package com.atguigu.gulimall.gulimallauthserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/12 8:59
 *
 * @author Control.
 * @since JDK 1.8
 */
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry SpringMVC 的注册中心
        /**
         * 专门写一个 空方法 比较麻烦
         *     @GetMapping("/login.html")
         *     public  String loginPage(){
         *         return "login";
         *     }
         *     @GetMapping("/reg.html")
         *     public  String regPage(){
         *         return "reg";
         *     }
         */
//        registry.addViewController("/login.html").setViewName("login");  写自己的逻辑
        registry.addViewController("/reg.html").setViewName("reg");
    }
}
