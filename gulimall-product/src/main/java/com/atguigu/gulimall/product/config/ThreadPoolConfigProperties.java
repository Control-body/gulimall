package com.atguigu.gulimall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static org.apache.naming.SelectorContext.prefix;

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
