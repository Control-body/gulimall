package com.atguigu.gulimall.product;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/2 9:33
 *
 * @author Control.
 * @since JDK 1.8
 */
@Slf4j
@SpringBootTest
public class RediRsonTest {
    @Autowired
    RedissonClient redissonClient;

    @Test
    public void redisson(){
        System.out.println(redissonClient);
    }

}
