package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@Slf4j
@SpringBootTest
class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin admin;

    /**
     * 1. 如何创建 Exange， Queue Binding
     *    1） 使用 admin进行 创建
     *  2.如何收发消息
     */
    @Test
    void createExchange() {
//        public DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
        DirectExchange directExchange = new DirectExchange("hello-java-exchange",true,false);
        admin.declareExchange(directExchange);
        log.info("excahnge【{}】创建成功","hello-java-exchange");
    }
    @Test
    public void createquery(){
        //public Queue(String name,（队列的名称）
        // boolean durable, （是否是可实力化的）
        // boolean exclusive, （是都是排他的 一个已经 连接好了 其他不能连接）
        // boolean autoDelete, （是否自动删除）
        // @Nullable Map<String, Object> arguments  （参数）)
        Queue queue = new Queue("hello-java-query",true,false,false);
        admin.declareQueue(queue);
        log.info("excahnge【{}】创建成功","hello-java-query");
    }
    @Test
    public void createBindings(){
//           public Binding(String destination(目的地),
//           Binding.DestinationType（目的地类型 ）
//           destinationType, String exchange（交换机）,
//           String routingKey（路由键）,
//           @Nullable Map<String, Object> arguments（参数）)
        Binding binding = new Binding("hello-java-query", Binding.DestinationType.QUEUE
        ,"hello-java-exchange","hello.java",null);
        admin.declareBinding(binding);
        log.info("excahnge【{}】创建成功","hello-java-binding");
    }
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Test
    public void createMessageTest(){
//
        for(int i=0;i<10;i++){
            OrderReturnReasonEntity qrderReturnReasonEntity = new OrderReturnReasonEntity();
            qrderReturnReasonEntity.setId(1L);
            qrderReturnReasonEntity.setCreateTime(new Date());
            qrderReturnReasonEntity.setName("刘斌"+i);
            rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",qrderReturnReasonEntity);
            log.info("消息发送完成");
        }

    }

}
