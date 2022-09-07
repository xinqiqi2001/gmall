package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Xiaoxin
 * @Date 2022/9/8 1:32
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/inner/rpc/cart")
public class CartApiController {



    //@RequestHeader(value= SysRedisConst.USERID_HEADER) 从请求头中获取透传过来的用户id
    @GetMapping("/addToCart")
    Result<SkuInfo> addToCart(@RequestParam("skuId") Long skuId,
                              @RequestParam("num") Integer num,
                              @RequestHeader(value=SysRedisConst.USERID_HEADER,required = false)
                                      String userId){

        System.out.println("service-cart 获取到的用户id："+userId);
        return Result.ok();
    }
}
