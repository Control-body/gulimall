package com.atguigu.gulimall.cart.config;

import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/22 20:16
 *
 * @author Control.
 * @since JDK 1.8
 */
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//       给注册 列表 添加 拦截器
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}
