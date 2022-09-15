package com.atguigu.gmall.model.vo.order;

import lombok.Data;

import java.util.List;

/**
 * 订单提交 数据模型
 */
@Data
public class OrderSubmitVo {
    //收货人
    private String consignee;
    //收货人电话
    private String consigneeTel;
    //收货地址
    private String deliveryAddress;
    //支付方式
    private String paymentWay;
    //订单备注
    private String orderComment;
    //交易体
    private List<CartInfoVo> orderDetailList;
}
