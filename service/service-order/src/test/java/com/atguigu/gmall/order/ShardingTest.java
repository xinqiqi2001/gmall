package com.atguigu.gmall.order;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.order.OrderStatusLog;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.mapper.OrderStatusLogMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Connor
 * @date 2022/9/13
 */
@SpringBootTest
public class ShardingTest {
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderStatusLogMapper orderStatusLogMapper;

    @Test
    public void testSelect(){
        List<OrderInfo> orderInfos = orderInfoMapper.selectList(null);
        orderInfos.forEach(System.out::println);
    }

    @Test
    public void testInsert(){
        OrderInfo info = new OrderInfo();
        info.setTotalAmount(new BigDecimal("777"));
        info.setUserId(1L);
        orderInfoMapper.insert(info);


        System.out.println("1号用户订单插入完成....去 1库1表找");


        OrderInfo info2 = new OrderInfo();
        info2.setTotalAmount(new BigDecimal("666"));
        info2.setUserId(2L);
        orderInfoMapper.insert(info2);
        System.out.println("2号用户订单插入完成....去 0库2表找");
    }
    @Test
    public void testInsert2(){
        OrderStatusLog orderStatusLog = new OrderStatusLog();
        orderStatusLog.setOrderId(1111L);
        orderStatusLog.setOrderStatus("UNPAID");
        orderStatusLog.setOperateTime(new Date());
        orderStatusLog.setId(null);
        orderStatusLogMapper.insert(orderStatusLog);

    }
}
