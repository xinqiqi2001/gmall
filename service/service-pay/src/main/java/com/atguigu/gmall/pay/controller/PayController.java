package com.atguigu.gmall.pay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.pay.service.AlipayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author Xiaoxin
 * @Date 2022/9/16 21:28
 * @Version 1.0
 */
@Slf4j
@Controller
@RequestMapping("/api/payment")
public class PayController {

    @Autowired
    AlipayService alipayService;

    /**
     * 跳转到阿里支付宝支付收银台
     *
     * @param orderId
     * @return
     */
    @ResponseBody
    @GetMapping("/alipay/submit/{orderId}")
    public String alipay(@PathVariable("orderId") Long orderId) throws AlipayApiException {

        /**
         * 返回一个阿里支付页面
         */
        String alipayPageHtml = alipayService.getAlipayPageHtml(orderId);


        return alipayPageHtml;
    }

    /**
     * 跳转到支付成功页
     * 支付成功后支付宝会让浏览器访问http://gmall.com/api/payment/paysuccess这个地址
     * 这个地址会通过网关指定让模块执行 如下
     * 这个方法最后重定向到支付成功页面
     *
     * @return
     */
    //同步通知
    @GetMapping("/paysuccess")
    public String paySuccess(@RequestParam Map<String, String> paraMaps) throws AlipayApiException {
        System.out.println("支付成功同步通知页:收到的参数是：" + paraMaps);
        //如果在这里要修改订单状态 需要在这里验证一下签名  看看是否是支付宝发来的数据
        //验证签名(验证发来的参数是不是支付宝发来的  有可能被别人篡改)
        boolean b = alipayService.rsaCheckV1(paraMaps);
        if (b) {
            //验签通过  不能在这里修改  可能会有问题
            System.out.println("正在修改订单状态+订单信息：" + paraMaps);
        }
        //这个地址通过网关交给webAll执行
        return "redirect:http://gmall.com/pay/success.html";
    }

    /**
     * 支付宝支付成功
     * 异步通知
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("success/notify")
    public String notifySuccess(@RequestParam Map<String, String> paraMaps) throws AlipayApiException {
        //如果在这里要修改订单状态 需要在这里验证一下签名  看看是否是支付宝发来的数据
        //验证签名(验证发来的参数是不是支付宝发来的  有可能被别人篡改)
        boolean b = alipayService.rsaCheckV1(paraMaps);
        if (b) {
            log.info("支付成功，验签通过异步通知成功抵达，数据{}", Jsons.toStr(paraMaps));
            //修改订单状态 最大努力通知8次


        }else {
            //验签通过
            //验签失败  可能是恶意攻击的假数据
            return "error";
        }

        return "success";
    }
}
