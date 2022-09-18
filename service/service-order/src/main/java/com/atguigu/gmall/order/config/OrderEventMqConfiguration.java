package com.atguigu.gmall.order.config;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.constant.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Xiaoxin
 * @Date 2022/9/15 14:52
 * @Version 1.0
 */
@Configuration
public class OrderEventMqConfiguration {

    /**
     * 项目启动发现没有这个交换机就会自动创建出来
     * 订单事件交换机
     * @return
     */
    @Bean
    public Exchange orderEventExchange(){

        //交换机名，是否持久化，是否自动删除
        TopicExchange exchange =
                new TopicExchange(MqConst.EXCHANGE_ORDER_EVNT,true,false);

        return exchange;
    }


    /**
     * 订单延迟队列
     */
    @Bean
    public Queue orderDelayQueue(){

        /**
         * String name,
         * 持久化
         * boolean durable,
         * 排他队列
         * boolean exclusive,
         * 自动删除
         * boolean autoDelete,
         * 队列参数设置项
         * @Nullable Map<String, Object> arguments
         */
        Map<String, Object> arguments = new HashMap<>();
        //设置延迟队列参数
        //x-message-ttl队列45分钟后超时进入死信队列
        arguments.put("x-message-ttl", SysRedisConst.ORDER_CLOSE_TTL*1000);
        //死信队列的交换机名 信死了之后交给x-dead-letter-exchange这个交换机
        arguments.put("x-dead-letter-exchange",MqConst.EXCHANGE_ORDER_EVNT);
        //信死了之后交给x-dead-letter-exchange这个交换机用x-dead-letter-routing-key这个路由键交
        arguments.put("x-dead-letter-routing-key",MqConst.RK_ORDER_DEAD);

        return new Queue(MqConst.QUEUE_ORDER_DELAY,
                true,
                false,
                false,
                arguments
        );
    }

    /**
     * 延迟队列和交换机绑定
     * @return
     */
    @Bean
    public Binding orderDelayQueueBinding(){

        /**
         * String destination, 目的地
         * DestinationType destinationType, 目的地类型
         * String exchange, 交换机
         * String routingKey, 路由键
         * @Nullable Map<String, Object> arguments 参数项
         *
         * 这个exchange交换机和这个destinationType类型的目的地（destination）
         * 使用routingKey进行绑定，
         */

        return new Binding(MqConst.QUEUE_ORDER_DELAY,
                Binding.DestinationType.QUEUE,
                MqConst.EXCHANGE_ORDER_EVNT,
                MqConst.RK_ORDER_CREATED,
                null
                );
    }

    /**
     * 死单队列：保存所有过期订单，需要进行关单
     * @return
     */
    @Bean
    public Queue orderDeadQueue(){
        /**
         * String name,
         * boolean durable,
         * boolean exclusive, boolean autoDelete,
         *                        @Nullable Map<String, Object> arguments
         */
        return new Queue(MqConst.QUEUE_ORDER_DEAD,
                true,
                false,
                false);
    }


    /**
     * 死单队列和订单事件交换机绑定
     * @return
     */
    @Bean
    public Binding orderDeadQueueBinding(){
        return new Binding(MqConst.QUEUE_ORDER_DEAD,
                Binding.DestinationType.QUEUE,
                MqConst.EXCHANGE_ORDER_EVNT,
                MqConst.RK_ORDER_DEAD,
                null);
    }



}
