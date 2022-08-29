package com.atguigu.gulimall.product;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/1 21:14
 *
 * @author Control.
 * @since JDK 1.8
 */
@Slf4j
@SpringBootTest
public class redisTest {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Test
    public void  test(){
//       保存简单数据
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
//        保存数据
        stringStringValueOperations.set("hello","world"+ UUID.randomUUID().toString());
//        查询数据
        String s = stringStringValueOperations.get("hello");
        System.out.println(
                "保存的数据是"+s
        );

    }
}
