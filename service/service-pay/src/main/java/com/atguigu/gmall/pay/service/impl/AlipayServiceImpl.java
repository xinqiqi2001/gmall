package com.atguigu.gmall.pay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.pay.config.AlipayProperties;
import com.atguigu.gmall.pay.service.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

/**
 * @Author Xiaoxin
 * @Date 2022/9/16 23:32
 * @Version 1.0
 */
@Service
public class AlipayServiceImpl implements AlipayService {


    @Autowired
    AlipayClient alipayClient;

    @Autowired
    AlipayProperties alipayProperties;

    @Autowired
    OrderFeignClient orderFeignClient;
    /**
     * 阿里支付宝支付收银台
     * 生成指定订单的支付页
     * @param orderId
     * @return
     */
    @Override
    public String getAlipayPageHtml(Long orderId) throws AlipayApiException {


        //3.拿到订单信息
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId).getData();
        if(orderInfo.getExpireTime().before(new Date())){
            throw new GmallException(ResultCodeEnum.ORDER_EXPIRED);
        }

        //1.创建一个支付请求
        AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();

        //2.设置同步通知地址和异步通知地址  构造支付请求需要的参数
        //2.1浏览器跳转到getReturnUrl页
        alipayTradePagePayRequest.setReturnUrl(alipayProperties.getReturnUrl());
        //2.2支付宝给getNotifyUrl发请求 通知支付成功消息
        alipayTradePagePayRequest.setNotifyUrl(alipayProperties.getNotifyUrl());


        //4.构造支付数据
        HashMap<String, Object> bizContent = new HashMap<>();
        //设置订单对外流水号
        bizContent.put("out_trade_no",orderInfo.getOutTradeNo());
        //订单总金额最小0.01
        bizContent.put("total_amount",orderInfo.getTotalAmount().toString());
        //订单标题
        bizContent.put("subject","尚品汇订单-"+orderInfo.getOutTradeNo());
        //产品码  目前只支持FAST_INSTANT_TRADE_PAY
        bizContent.put("product_code","FAST_INSTANT_TRADE_PAY");
        //订单的附加信息
        bizContent.put("body",orderInfo.getTradeBody());

        //添加绝对订单超时时间(自动收单)
        String date = DateUtil.formatDate(orderInfo.getExpireTime(),"yyyy-MM-dd HH:mm:ss");
        bizContent.put("time_expire",date);

        alipayTradePagePayRequest.setBizContent(Jsons.toStr(bizContent));


        //5.用支付宝客户端给支付宝发送支付请求 得到二维码收银台页面
        String body = alipayClient.pageExecute(alipayTradePagePayRequest).getBody();



        return body;
    }
}
