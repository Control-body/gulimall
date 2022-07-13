package com.atguigu.gulimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/7/13 8:56
 *
 * @author Control.
 * @since JDK 1.8
 */
@Configuration
public class GulimallCorsConfiguration {
    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
//       //配置跨域
        corsConfiguration.addAllowedHeader("*");//任意请求头
        corsConfiguration.addAllowedMethod("*");//任意请求方法
        corsConfiguration.addAllowedOrigin("*");//任意请求源
        corsConfiguration.setAllowCredentials(true);//是否支持cookie
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }


}
