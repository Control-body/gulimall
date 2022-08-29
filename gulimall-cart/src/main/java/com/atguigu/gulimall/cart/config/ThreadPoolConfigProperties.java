package com.atguigu.gulimall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/11 22:51
 *
 * @author Control.
 * @since JDK 1.8
 */

@ConfigurationProperties(prefix="gulimall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
    private Integer coreSize;

    private Integer maxSize;

    private Integer keepAliveTime;
}
