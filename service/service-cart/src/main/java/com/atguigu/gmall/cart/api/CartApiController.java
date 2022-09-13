package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.utiles.AuthUtils;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/9/8 1:32
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/inner/rpc/cart")
public class CartApiController {

    @Autowired
    CartService cartService;

    /**
     * @RequestHeader(value=SysRedisConst.USERID_HEADER,required = false)
     *                                       String userId,
     *                               @RequestHeader(value=SysRedisConst.USERTEMPID_HEADER,required = false)
     *                                       String tempUserId
     *                                               System.out.println("service-cart 获取到的用户id："+userId);
     *         System.out.println("service-cart 获取到的临时用户id："+tempUserId);
     * @param skuId
     * @param num
     * @return
     */
    //@RequestHeader(value= SysRedisConst.USERID_HEADER) 从请求头中获取透传过来的用户id
    @GetMapping("/addToCart")
    Result<SkuInfo> addToCart(@RequestParam("skuId") Long skuId,
                              @RequestParam("num") Integer num){

        //添加商品到购物车 把商品为skuId的添加进购物车 共添加num件
        SkuInfo skuInfo=cartService.addToCart(skuId,num);

        return Result.ok(skuInfo);
    }


    /**
     * 删除购物车中选中的商品
     *
     */
    @GetMapping("/deleteChecked")
    Result deleteChecked(){
        String cartKey = cartService.determinCartKey();
        cartService.deleteChecked(cartKey);
        return Result.ok();
    }
    /**
     * 获取所有被选中的商品
     */
    @GetMapping("/checked/list")
    Result<List<CartInfo>> getChecked(){

        String cartKey = cartService.determinCartKey();
        List<CartInfo> checkedItems = cartService.getCheckedItems(cartKey);



        return Result.ok(checkedItems);

    }



}


















