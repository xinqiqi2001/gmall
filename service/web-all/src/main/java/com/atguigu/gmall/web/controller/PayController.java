package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class PayController {


    @Autowired
    OrderFeignClient orderFeignClient;

    /**
     * 支付信息确认页(会带一个订单id)
     * orderId=776479833227001856
     *
     * @return
     */
    @GetMapping("/pay.html")
    public String payPage(Model model,
                          @RequestParam("orderId") Long orderId) {

        //根据订单id和用户id获取订单数据
        Result<OrderInfo> orderInfo = orderFeignClient.getOrderInfo(orderId);
        //获取订单失效时间
        Date ttl = orderInfo.getData().getExpireTime();
        Date cur = new Date();
        //如果当前时间在失效时间之前  那么证明订单未过期
        if (cur.before(ttl)) {
            //订单未过期，可以展示支付页
            model.addAttribute("orderInfo", orderInfo.getData());
            return "payment/pay";
        }

        return "payment/error";

    }
}
