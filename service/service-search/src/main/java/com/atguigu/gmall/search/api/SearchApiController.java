package com.atguigu.gmall.search.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Xiaoxin
 * @Date 2022/9/5 12:26
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/inner/rpc/search")
public class SearchApiController {


    @Autowired
    GoodsService goodsService;
    /**
     * 保存一个商品到es
     * @return
     */
    @PostMapping("/goods")
    public Result saveGoods(@RequestBody Goods goods){

        goodsService.saveGoods(goods);
        return Result.ok();
    }
    /**
     * 从es中删除商品
     */
    @PostMapping("/goods/{skuId}")
    public Result deleteGoods(@PathVariable("skuId") Long skuId){
        goodsService.deleteGoods(skuId);
        return Result.ok();
    }


    /**
     * 商品检索功能
     * @param paramVo
     * @return
     */
    @PostMapping("/goods/search")
    public Result<SearchResponseVo> search(@RequestBody SearchParamVo paramVo){
        SearchResponseVo responseVo = goodsService.search(paramVo);
        return Result.ok(responseVo);
    }


}
