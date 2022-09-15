package com.atguigu.gmall.order.biz;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;

/**
 * @Author Xiaoxin
 * @Date 2022/9/13 18:54
 * @Version 1.0
 */
public interface OrderBizService {
    /**
     * Result<OrderConfirmDataVo>
     * @return
     */
    OrderConfirmDataVo getConfirmData();

    /**
     * 生成交易流水号。
     * 1、追踪整个订单
     * 2、作为防重令牌
     * @return
     */
    String generateTradeNo();

    /**
     * 校验令牌
     * @param tradeNo
     * @return
     */
    boolean checkTradeNo(String tradeNo);

    /**
     * 验证令牌
     * @param submitVo
     * @param tradeNo
     * @return
     */
    Long submitOrder(OrderSubmitVo submitVo, String tradeNo);


    /**
     * 关闭订单(修改数据库订单状态)
     * @param orderId 订单id
     * @param userId 用于id
     */
    void closeOrder(Long orderId, Long userId);

}
