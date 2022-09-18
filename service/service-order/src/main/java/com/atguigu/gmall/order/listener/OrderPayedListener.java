package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.model.to.mq.WareDeduceMsg;
import com.atguigu.gmall.model.to.mq.WareDeduceSkuInfo;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.atguigu.gmall.service.RabbitService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Xiaoxin
 * @Date 2022/9/18 21:48
 * @Version 1.0
 */

@Service
@Slf4j
public class OrderPayedListener {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RabbitService rabbitService;

    @Autowired
    PaymentInfoService paymentInfoService;

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    OrderDetailService orderDetailService;

    @RabbitListener(queues = MqConst.QUEUE_ORDER_PAYED)//支付成功单队列
    public void payedListener(Message message, Channel channel)throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        //1.支付宝异步回调的数据转为map
        Map<String,String> map = Jsons.toObj(message, Map.class);

        //2.拿到支付宝的对外交易号
        String trade_no = map.get("trade_no");

        try {
            //处理业务(消费掉支付成功队列的数据)修改支付成功的订单状态
            //3.保存支付消息
            PaymentInfo paymentInfo = paymentInfoService.savePaymentInfo(map);
            Long orderId = paymentInfo.getOrderId();
            Long userId = paymentInfo.getUserId();
            //4.修改订单状态
            // 订单状态流转  未支付和订单关闭的订单状态才可以将订单状态改为已支付
            //只有这两种状态才可以转换为已支付的状态
            List<ProcessStatus> expected = Arrays.asList(ProcessStatus.UNPAID, ProcessStatus.CLOSED);
            orderInfoService.changeOrderStatus(orderId,userId,ProcessStatus.PAID,expected);

            //通知库存系统，扣减库存(库存系统需要一些消息) 获取到扣减库存需要的数据
            WareDeduceMsg msg = prepareWareDeduceMsg(paymentInfo);
            //发送消息
            rabbitTemplate.convertAndSend(MqConst.EXCHANGE_WARE_EVENT,
                    MqConst.RK_WARE_DEDUCE,Jsons.toStr(msg));
            
            log.info("监听到了支付成功订单{}：",paymentInfo);
            channel.basicAck(tag,false);
        } catch (Exception e) {
            log.error("订单业务关闭失败,消息:{},失败原因:{}",message,e);
            String uniqKey= SysRedisConst.MQ_RETRY+"order:payed:"+trade_no;
            //消息发生错误重试10次的方法
            rabbitService.retryConsumMsg(10L,uniqKey,tag,channel);
        }

    }
    //减库存需要的数据
    private WareDeduceMsg prepareWareDeduceMsg(PaymentInfo paymentInfo){
        /**
         * orderId
         * consignee
         * consigneeTel
         * orderComment
         * orderBody
         * deliveryAddress
         * paymentWay
         * details:
         * skuId
         * skuNum
         * skuName
         */

        WareDeduceMsg wareDeduceMsg = new WareDeduceMsg();
        Long userId = paymentInfo.getUserId();
        wareDeduceMsg.setOrderId(paymentInfo.getOrderId());
        //1、查询出当前订单
        OrderInfo orderInfo = orderInfoService.getOrderInfoByOrderIdAndUserId(paymentInfo.getOrderId(),userId);

        wareDeduceMsg.setConsignee(orderInfo.getConsignee());
        wareDeduceMsg.setConsigneeTel(orderInfo.getConsigneeTel());
        wareDeduceMsg.setOrderComment(orderInfo.getOrderComment());
        wareDeduceMsg.setOrderBody(orderInfo.getTradeBody());
        wareDeduceMsg.setDeliveryAddress(orderInfo.getDeliveryAddress());
        wareDeduceMsg.setPaymentWay("2");


        //2、查询出订单的明细
        List<WareDeduceSkuInfo> infos = orderDetailService.list(
                new LambdaQueryWrapper<OrderDetail>()
                        .eq(OrderDetail::getOrderId, orderInfo.getId())
                        .eq(OrderDetail::getUserId, userId)
        ).stream().map(orderDetail -> {
            WareDeduceSkuInfo skuInfo = new WareDeduceSkuInfo();
            skuInfo.setSkuId(orderDetail.getSkuId());
            skuInfo.setSkuNum(orderDetail.getSkuNum());
            skuInfo.setSkuName(orderDetail.getSkuName());
            return skuInfo;
        }).collect(Collectors.toList());


        //WareDeduceSkuInfo
        wareDeduceMsg.setDetails(infos);

        return wareDeduceMsg;

    }

}
