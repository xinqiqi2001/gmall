package com.atguigu.gmall.pay.service;

import com.alipay.api.AlipayApiException;

import java.util.Map;

/**
 * @Author Xiaoxin
 * @Date 2022/9/16 23:32
 * @Version 1.0
 */
public interface AlipayService {

    /**
     *  阿里支付宝支付收银台
     *  生成指定订单的支付页
     * @param orderId
     * @return
     */
    String getAlipayPageHtml(Long orderId) throws AlipayApiException;

    /**
     * //验证签名(验证发来的参数是不是支付宝发来的  有可能被别人篡改)
     * @param paraMaps
     * @return
     */
    boolean rsaCheckV1(Map<String, String> paraMaps) throws AlipayApiException;
}
