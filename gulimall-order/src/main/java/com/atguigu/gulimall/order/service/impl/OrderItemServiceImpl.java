package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderItemDao;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.service.OrderItemService;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * queues 就是监听 对应的 那个 队列的信息
     *
     * 消息体类型;
     * 1.class org.springframework.amqp.core.Message  是原生的 消息 详细信息 头+体的
     * 2.T<发送消息的类型> OrderReturnReasonEntity content
     * 3. channel 消息传输 通道
     *
     *
     */
    @RabbitListener(queues = {"hello-java-query"})
    public void  recieveMessage(Object message,
                                OrderReturnReasonEntity content,
                                Channel channel){
        System.out.println("接收到消息"+message+"类型"+message.getClass());
        System.out.println("接收到对应的实体类型"+content);
        System.out.println("消息传输 通道"+channel);
    }
}