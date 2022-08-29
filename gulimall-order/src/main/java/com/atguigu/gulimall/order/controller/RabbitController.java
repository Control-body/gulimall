package com.atguigu.gulimall.order.controller;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * ClassName: Control.
 * Description:
 * date: 2022/8/27 16:43
 *
 * @author Control.
 * @since JDK 1.8
 */
@RestController
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @GetMapping("/sendMq")
    public  String  sendMessage(@RequestParam(value="num",defaultValue="10")Integer num){
        for(int i=0;i<num;i++){
            OrderReturnReasonEntity qrderReturnReasonEntity = new OrderReturnReasonEntity();
            qrderReturnReasonEntity.setId(1L);
            qrderReturnReasonEntity.setCreateTime(new Date());
            qrderReturnReasonEntity.setName("刘斌"+i);
            rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",qrderReturnReasonEntity);
        }
        return "ok";
    }
}
