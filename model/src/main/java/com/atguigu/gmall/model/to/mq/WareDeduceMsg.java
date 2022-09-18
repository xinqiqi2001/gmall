package com.atguigu.gmall.model.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @author Xiaoxin
 */
@Data
public class WareDeduceMsg {

    /**
     * 减库存需要的数据
     */
    Long orderId;
    String consignee;
    String consigneeTel;
    String orderComment;
    String orderBody;
    String deliveryAddress;
    String paymentWay = "2";
    List<WareDeduceSkuInfo> details;

}
