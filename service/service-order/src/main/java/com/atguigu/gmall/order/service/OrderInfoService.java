package com.atguigu.gmall.order.service;


import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Xiaoxin
* @description 针对表【order_info(订单表 订单表)】的数据库操作Service
* @createDate 2022-09-11 14:34:46
*/
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 将商品信息和令牌存入数据库
     * @param submitVo
     * @param tradeNo
     * @return
     */
    Long saveOrder(OrderSubmitVo submitVo, String tradeNo);

    /**
     * 将未支付或者已结束状态的订单关闭
     * @param orderId
     * @param userId
     * @param whileChange 想要改变成什么状态
     * @param expected  订单只有expected是这种状态才能改变
     */
    void changeOrderStatus(Long orderId, Long userId, ProcessStatus whileChange, List<ProcessStatus> expected);

    /**
     * 根据对外交易号和用户id获取订单信息
     * @param outTradeNo
     * @param userId
     * @return
     */
    OrderInfo getOrderInfoByOutTradeNoAndUserId(String outTradeNo, Long userId);

    /**
     * 查询订单数据。
     * @param orderId
     * @param userId
     * @return
     */
    OrderInfo getOrderInfoByOrderIdAndUserId(Long orderId, Long userId);
}
