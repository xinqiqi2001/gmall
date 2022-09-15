package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author Xiaoxin
 * @Date 2022/9/13 9:21
 * @Version 1.0
 */
@Controller
public class OrderTradeController {
    @Autowired
    OrderFeignClient orderFeignClient;

    /**
     * 跳转到结算界面  结算页面需要收件人地址,和商品的各种信息
     *
     * @param model
     * @return
     */
    @GetMapping("/trade.html")
    public String tradePage(Model model) {

        Result<OrderConfirmDataVo> orderConfirmData = orderFeignClient.getOrderConfirmData();

        if (orderConfirmData.isOk()) {
            OrderConfirmDataVo data = orderConfirmData.getData();
            model.addAttribute("detailArrayList", data.getDetailArrayList());
            model.addAttribute("totalNum", data.getTotalNum());
            model.addAttribute("totalAmount", data.getTotalAmount());
            //用户收货地址列表
            model.addAttribute("userAddressList", data.getUserAddressList());
            //追踪订单的“交易号”
            model.addAttribute("tradeNo", data.getTradeNo());

        }

        return "order/trade";

    }
}
