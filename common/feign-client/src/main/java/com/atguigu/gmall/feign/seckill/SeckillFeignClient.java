package com.atguigu.gmall.feign.seckill;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.vo.seckill.SeckillOrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/9/20 19:21
 * @Version 1.0
 */
@RequestMapping("/api/inner/rpc/seckill")
@FeignClient("service-seckill")
public interface SeckillFeignClient {


    /**
     * 获取当天所有秒杀商品的数据
     *
     * @return
     */
    @GetMapping("/currentday/goods/list")
    Result<List<SeckillGoods>> getCurrentDaySeckillGoodsList();

    /**
     * 秒杀商品详情页
     * @param skuId
     * @return
     */
    @GetMapping("/good/detail/{skuId}")
    Result<SeckillGoods> getSeckillGood(@PathVariable("skuId") Long skuId);

    /**
     * 获取秒杀确认页数据
     * @param skuId
     * @return
     */
    Result<SeckillOrderConfirmVo> getSeckillOrderConfirmVo(Long skuId);
}
