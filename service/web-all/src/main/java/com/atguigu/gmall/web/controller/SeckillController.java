package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.seckill.SeckillFeignClient;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.vo.seckill.SeckillOrderConfirmVo;
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
    /**
     * 秒杀排队页
     * 隐藏秒杀码
     * http://activity.gmall.com/seckill/queue.html?skuId=53&skuIdStr=28c6f1241b8feae0822785b40b9b3995
     */
    @GetMapping("/seckill/queue.html")
    public String seckillQueue(@RequestParam("skuId") Long skuId,
                               @RequestParam("skuIdStr") String skuIdStr,
                               Model model){
        model.addAttribute("skuId",skuId);
        model.addAttribute("skuIdStr",skuIdStr);
        return "seckill/queue";
    }

    @GetMapping("/seckill/trade.html")
    public String seckillTrade(Model model,@RequestParam("skuId") Long skuId){


        Result<SeckillOrderConfirmVo> confirmVo =
                seckillFeignClient.getSeckillOrderConfirmVo(skuId);

        SeckillOrderConfirmVo voData = confirmVo.getData();
        //返回的是订单确认页的数据
        model.addAttribute("detailArrayList",voData.getTempOrder().getOrderDetailList());
        model.addAttribute("userAddressList",voData.getUserAddressList());
        model.addAttribute("totalNum",voData.getTempOrder().getOrderDetailList().size());

        model.addAttribute("totalAmount",voData.getTempOrder().getTotalAmount());
        return "seckill/trade";
    }

}
