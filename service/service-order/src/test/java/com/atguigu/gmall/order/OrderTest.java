package com.atguigu.gmall.order;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author Xiaoxin
 * @Date 2022/9/11 14:39
 * @Version 1.0
 */
@SpringBootTest
public class OrderTest {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Test
    void test1(){
        OrderInfo orderInfo = orderInfoMapper.selectById(197);
        System.out.println("orderInfo = " + orderInfo);
    }

}
