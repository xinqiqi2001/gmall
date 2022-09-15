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
     * @param closed
     * @param expected
     */
    void changeOrderStatus(Long orderId, Long userId, ProcessStatus closed, List<ProcessStatus> expected);
}
