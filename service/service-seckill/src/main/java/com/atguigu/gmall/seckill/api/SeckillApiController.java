package com.atguigu.gmall.seckill.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.seckill.SeckillFeignClient;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsCacheOpsService;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/9/20 19:11
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/inner/rpc/seckill")
public class SeckillApiController {

    @Autowired
    SeckillGoodsCacheOpsService cacheOpsService;

    @Autowired
    SeckillGoodsService seckillGoodsService;
    /**
     * 获取当天所有秒杀商品的数据
     *
     * @return
     */
    @GetMapping("/currentday/goods/list")
    Result<List<SeckillGoods>> getCurrentDaySeckillGoodsList(){
        //先查redis中是否有数据
        //获取当天所有秒杀商品的数据
        List<SeckillGoods> goods =  seckillGoodsService.getCurrentDaySeckillGoodsCache();

        return Result.ok(goods);
    }

    @GetMapping("/good/detail/{skuId}")
    public Result<SeckillGoods> getSeckillGood(@PathVariable("skuId") Long skuId){

        SeckillGoods seckillGoods = seckillGoodsService.getSeckillGoodDetail(skuId);
        return Result.ok(seckillGoods);
    }

}
