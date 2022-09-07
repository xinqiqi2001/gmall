package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

/**
 * @Author Xiaoxin
 * @Date 2022/9/8 1:20
 * @Version 1.0
 */
@Controller  //页面跳转  用@controller
public class CartController {
    @Resource
    CartFeignClient cartFeignClient;

    @GetMapping("/addCart.html")
    public String addCarthtml(@RequestParam("skuId") Long skuId,
                              @RequestParam("skuNum") Integer skuNum,
                              @RequestHeader(SysRedisConst.USERID_HEADER)String userId,
                              Model model) {
        System.out.println("获取到了用户id："+userId);


        //添加指定商品进购物车
        Result<SkuInfo> result = cartFeignClient.addToCart(skuId, skuNum);

        //存入共享域中
        model.addAttribute("skuInfo",result.getData());
        model.addAttribute("skuNum",skuNum);

        return "cart/addCart";
    }

}
