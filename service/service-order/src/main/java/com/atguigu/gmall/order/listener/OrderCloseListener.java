package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.model.to.mq.OrderMsg;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author Xiaoxin
 * 订单关闭监听器
 * @Date 2022/9/15 15:54
 * @Version 1.0
 */
@Component
@Slf4j
public class OrderCloseListener {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderBizService orderBizService;

    @RabbitListener(queues = MqConst.QUEUE_ORDER_DEAD)
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
            // 给这个along+1
            Long aLong = redisTemplate.opsForValue().increment(SysRedisConst.MQ_RETRY + "order:" + orderMsg.getOrderId());
            if(aLong <= 10){
                channel.basicNack(tag,false,true);
            }else {
                //不入队
                channel.basicNack(tag,false,false);
                //删除
                redisTemplate.delete(SysRedisConst.MQ_RETRY + "order:" + orderMsg.getOrderId());
            }
        }


    }

}
