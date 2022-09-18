package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.model.to.mq.OrderMsg;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.atguigu.gmall.service.RabbitService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author Xiaoxin
 * 订单关闭监听器
 * @Date 2022/9/15 15:54
 * @Version 1.0
 */
@Component
@Slf4j
public class OrderCloseListener {

    StringRedisTemplate redisTemplate;
    OrderBizService orderBizService;
    RabbitService rabbitService;

    public OrderCloseListener(StringRedisTemplate redisTemplate,
                              OrderBizService orderBizService,
                              RabbitService rabbitService){
        this.redisTemplate = redisTemplate;
        this.orderBizService = orderBizService;
        this.rabbitService = rabbitService;
    }

    @RabbitListener(queues = MqConst.QUEUE_ORDER_DEAD)//死单队列
    public void orderClose(Message message,Channel channel)throws IOException{
        long tag = message.getMessageProperties().getDeliveryTag();
        //1拿到订单消息
        OrderMsg orderMsg=Jsons.toObj(message, OrderMsg.class);

        try {
            //2进行订单关闭
            log.info("监听到了超时订单{},正在关闭：",orderMsg);
            orderBizService.closeOrder(orderMsg.getOrderId(),orderMsg.getUserId());
            channel.basicAck(tag,false);
        } catch (Exception e) {
            log.error("订单业务关闭失败,消息:{},失败原因:{}",message,e);
            String uniqKey=SysRedisConst.MQ_RETRY+"order:"+orderMsg.getOrderId();
            rabbitService.retryConsumMsg(10L,uniqKey,tag,channel);
        }


    }


}
