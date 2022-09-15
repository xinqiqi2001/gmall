package com.atguigu.gmall.order.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @Author Xiaoxin
 * @Date 2022/9/15 9:33
 * @Version 1.0
 */
@Component
public class Queues {
//    配合@EnableRabbit获取指定队列信息
    private ConcurrentHashMap<String, AtomicInteger> counter=new ConcurrentHashMap<>();

//    @RabbitListener(queues = "haha")
    public void listenHaha(Message message, Channel channel) throws IOException {
        String content = new String(message.getBody());
        //获取信息标签
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        counter.putIfAbsent(content,new AtomicInteger(0));
        try {

            System.out.println("message = " + content);
            channel.basicAck(deliveryTag,false);//不批量回复
        } catch (Exception e) {
            System.out.println("消息消费失败");
            AtomicInteger integer = counter.get(content);
            System.out.println(deliveryTag+"加到："+integer);
            //重试10次
            if (integer.incrementAndGet()<=10){
                //重新存储消息 等待下个人处理
                System.out.println("重新存储消息 等待下个人处理");
                channel.basicNack(deliveryTag,false,true);
            }else {
                //超过最大重试次数   不存入队列
                //可以将失败的消息存入到一张数据库表
                System.out.println("超过最大重试次数   不存入队列  ");
                channel.basicNack(deliveryTag,false,false);
                counter.remove(content);
            }

        }

    }

}
