package com.atguigu.gmall.feign.search;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Xiaoxin
 * @Date 2022/9/5 12:37
 * @Version 1.0
 */

@RequestMapping("/api/inner/rpc/search")
@FeignClient("service-search")
public interface SearchFeignClient {

    /**
     * 将商品保存到es中
     * @param goods
     * @return
     */
    @PostMapping("/goods")
    Result saveGoods(@RequestBody Goods goods);


    /**
     * 从es中删除商品
     */
    @PostMapping("/goods/{skuId}")
    Result deleteGoods(@PathVariable("skuId") Long skuId);

    /**
     * 商品检索功能
     * @param searchParamVo
     * @return
     */
    @PostMapping("/goods/search")
    Result<SearchResponseVo> search(@RequestBody SearchParamVo searchParamVo);


    /**
     * 按热度更新 攒100更新一次
     * @param skuId
     * @param score
     * @return
     */
    @GetMapping("/goods/hotscore/{skuId}")
    Result updateHotScore(@PathVariable("skuId") Long skuId,
                          @RequestParam("score") Long score);
}
