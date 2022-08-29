package com.atguigu.gulimall.gulimallauthserver.config;

import com.atguigu.common.constant.AuthServerConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/20 16:31
 *
 * @author Control.
 * @since JDK 1.8
 */
@Configuration
public class GulimallSessionConfig {


    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        // 明确的指定Cookie的作用域
        cookieSerializer.setDomainName("gulimall.com");
        cookieSerializer.setCookieName(AuthServerConstant.SESSION);
        return cookieSerializer;
    }
    /**
     * 自定义序列化机制
     * 这里方法名必须是：springSessionDefaultRedisSerializer
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }

}
