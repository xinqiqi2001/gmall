package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.seckill.SeckillFeignClient;
import com.atguigu.gmall.model.activity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/9/20 18:33
 * @Version 1.0
 */
@Controller
public class SeckillController {

    @Autowired
    SeckillFeignClient seckillFeignClient;

    /**
     * 秒杀列表页
     */
    @GetMapping("/seckill.html")
    public String seckillPage(Model model) {
        //获取当天秒杀商品数据
        Result<List<SeckillGoods>> goodsList = seckillFeignClient.getCurrentDaySeckillGoodsList();

        //查询秒杀数据{skuId,skuDefaulImg,skuName,price,costprice,num,stockCount}
        model.addAttribute("list",goodsList.getData());
        return "seckill/index";

    }

    /**
     * 秒杀商品的详情页
     * @param model
     * @return
     */
    @GetMapping("/seckill/{skuId}.html")
    public String seckillDetail(Model model, @PathVariable("skuId") Long skuId){

        Result<SeckillGoods> seckillGood = seckillFeignClient.getSeckillGood(skuId);

        model.addAttribute("item",seckillGood.getData());
        return "seckill/item";
    }


}
