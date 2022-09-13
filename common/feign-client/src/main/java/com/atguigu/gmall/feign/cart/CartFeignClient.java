package com.atguigu.gmall.feign.cart;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/9/8 1:26
 * @Version 1.0
 */
@RequestMapping("/api/inner/rpc/cart")
@FeignClient("service-cart")
public interface CartFeignClient {

    /**
     * 将商品添加到购物车 方法
     */
    @GetMapping("/addToCart")
    Result<Object> addToCart(@RequestParam("skuId") Long skuId,
                              @RequestParam("num") Integer num);

    /**
     * 删除指定购物车中选中商品
     * @return
     */
    @GetMapping("/deleteChecked")
    Result deleteChecked();

    /**
     * 获取当前购物车中选中的所有商品
     * @return
     */
    @GetMapping("/checked/list")
    Result<List<CartInfo>> getChecked();
}
