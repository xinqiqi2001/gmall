package com.atguigu.gmall.order;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.Nullable;

@SpringBootTest
public class RabbitTest {


    @Autowired
    RabbitTemplate rabbitTemplate;



    @Test
    void  testSend(){

        rabbitTemplate.convertAndSend("xinqiqi","hhh","123");
    }
}
