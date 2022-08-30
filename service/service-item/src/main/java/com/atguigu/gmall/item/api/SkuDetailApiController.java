package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.to.SkuDetailTo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Xiaoxin
 * @Date 2022/8/26 21:58
 * @Version 1.0
 */
@Api(tags = "三级分类的RPC接口")
@RestController
@RequestMapping("/api/inner/rpc/item")
public class SkuDetailApiController {

    @Autowired
    SkuDetailService skuDetailService;

    @GetMapping("/skudetail/{skuId}")
    public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId) {
        // 远程查询出商品的详细信息
        //TODO 商品详情需要远程调用service-product服务

        SkuDetailTo skuDetailTo=skuDetailService.getSkuDetail(skuId);

//        skuDetailService.getCache(skuId);

        return Result.ok(skuDetailTo);
    }


}
