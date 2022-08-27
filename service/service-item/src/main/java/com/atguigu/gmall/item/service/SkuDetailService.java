package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.to.SkuDetailTo;

/**
 * @Author Xiaoxin
 * @Date 2022/8/26 22:17
 * @Version 1.0
 */
public interface SkuDetailService {
    /**
     * 更具skuId查询商品详情
     * @param skuId
     * @return
     */
    SkuDetailTo getSkuDetail(Long skuId);
}
