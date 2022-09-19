package com.atguigu.gmall.model.vo.order;

import lombok.Data;

/**
 * @author Xiaoxin
 */
@Data
public class OrderWareMapVo {
    private Long orderId;

    //json 是 OrderWareMapSkuItemVo 的集合
    private String wareSkuMap;
}
