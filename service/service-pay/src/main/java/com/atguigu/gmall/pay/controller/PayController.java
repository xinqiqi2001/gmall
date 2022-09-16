package com.atguigu.gmall.pay.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.pay.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Xiaoxin
 * @Date 2022/9/16 21:28
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/payment")
public class PayController {

    @Autowired
    AlipayService alipayService;

    /**
     * 跳转到阿里支付宝支付收银台
     * @param orderId
     * @return
     */
    @GetMapping ("/alipay/submit/{orderId}")
    public String alipay(@PathVariable("orderId") Long orderId) throws AlipayApiException {

        String alipayPageHtml = alipayService.getAlipayPageHtml(orderId);


        return alipayPageHtml;
    }
}
