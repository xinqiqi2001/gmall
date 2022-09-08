package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/9/8 23:32
 * @Version 1.0
 */
@RestController
@RequestMapping("api/cart")
public class CartRestController {
    @Autowired
    CartService cartService;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 购物车列表发送
     * 查询购物车全部信息的请求
     */
    @GetMapping("/cartList")
    public Result cartList(){
        //1.判断要查询哪个购物车
        String cartKey = cartService.determinCartKey();

        //尝试合并购物车
        cartService.mergeUserAndTempCart();

        //2.获取这个购物车中所有商品
        List<CartInfo> cartList =cartService.getCartList(cartKey);

        return Result.ok(cartList);
    }

    /**
     * 更新购物车商品数量
     * @param skuId
     * @param num
     * @return
     */
    @PostMapping("/addToCart/{skuId}/{num}")
    public Result addToCart( @PathVariable("skuId") Long skuId,
                             @PathVariable("num") Integer num){
        //获取指定购物车cartKey(用户id 或 临时用户id指定的购物车)
        //判断要操作哪个购物车
        String cartKey = cartService.determinCartKey();
        cartService.updateItemNum(skuId,num,cartKey);
        return Result.ok();
    }

    /**
     *修改商品的选中状态
     * @param skuId
     * @param status
     * @return
     */
    @GetMapping("/checkCart/{skuId}/{status}")
    public Result checkCart(@PathVariable("skuId") Long skuId,
                            @PathVariable("status") Integer status){

        //获取指定购物车cartKey(用户id 或 临时用户id指定的购物车)
        //判断要操作哪个购物车
        String cartKey = cartService.determinCartKey();
        cartService.updateChecked(skuId,status,cartKey);

        return Result.ok();
    }

    /**
     * 删除购物车中的商品
     * @param skuId
     * @return
     */
    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId){
        //判断要操作哪个购物车
        String cartKey = cartService.determinCartKey();

        cartService.deleteCartItem(skuId,cartKey);

        return Result.ok();
    }

    /**
     * 删除选中状态的商品
     * @return
     */
//    @GetMapping("/deleteChecked")
//    public Result deleteChecked(){
//        //判断要操作哪个购物车
//        String cartKey = cartService.determinCartKey();
//        cartService.deleteChecked(cartKey);
//        return Result.ok();
//    }
}
