package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/2 9:28
 *
 * @author Control.
 * @since JDK 1.8
 */
@Configuration
public class MyRedissonConfig {
    /**
     * 所有的 rediss 的使用 都是通过 RedissonClient来进行操作
     * @return
     * @throws IOException
     */

    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException {
//        1.创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://101.132.174.210:6379");
//        返回实例
        return Redisson.create(config);
    }
}
