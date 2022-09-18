package com.atguigu.gmall.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Author Xiaoxin
 * @Date 2022/9/18 19:55
 * @Version 1.0
 */
@Service
@Slf4j
public class RabbitService {


    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     *
     * @param maxNum 指定最大尝试次数
     * @param uniqkey 指定识别消息的唯一key
     * @param messageTag 消息tag
     * @param channel 通道
     * @throws IOException
     */
    public void  retryConsumMsg(Long maxNum, String uniqkey, Long messageTag, Channel channel) throws IOException {
        //在redis中存入一个数据 获取一个增量 尝试10遍消费
        Long aLong = redisTemplate.opsForValue().increment(uniqkey);
        if(aLong <= maxNum){
            //消费不成功就在放回队列
            channel.basicNack(messageTag,false,true);
        }else {
            //不入队
            channel.basicNack(messageTag,false,false);
            //删除
            redisTemplate.delete(uniqkey);
            //记录到数据库,消费10次都未成功
            log.error("消费了{}次,全都消费失败 ",maxNum);
        }
    }
}
