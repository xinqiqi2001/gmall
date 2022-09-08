package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;

import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/9/8 20:02
 * @Version 1.0
 */
public interface CartService {
    /**
     * 添加指定商品到购物车 添加num件
     * @param skuId
     * @param num
     * @return
     */
    SkuInfo addToCart(Long skuId, Integer num);

    /**
     * 决定购物车使用哪个键(用户id 或临时id)
     * @return
     */
    String determinCartKey();

    /**
     * 给购物车(carKey)添加指定商品(skuId) 商品数量(num)
     * @param skuId
     * @param num
     * @param cartKey
     * @return
     */
    SkuInfo addItemToCart(Long skuId, Integer num, String cartKey);

    /**
     * 根据商品(skuId)获取 指定购物车(cartKey)中商品的信息
     * @param cartKey
     * @param skuId
     * @return
     */
    CartInfo getItemFromCart(String cartKey, Long skuId);

    /**
     * 获取指定购物车中所有商品 并按创建时间排序
     * @param cartKey
     * @return
     */
    List<CartInfo> getCartList(String cartKey);

    /**
     * 修改指定购物车中的指定商品的数量
     * @param skuId
     * @param num
     * @param cartKey
     */
    void updateItemNum(Long skuId, Integer num, String cartKey);

    /**
     * 修改商品的选中状态
     * @param skuId
     * @param status
     * @param cartKey
     */
    void updateChecked(Long skuId, Integer status, String cartKey);

    /**
     * 删除购物车中的商品
     * @param skuId
     * @param cartKey
     */
    void deleteCartItem(Long skuId, String cartKey);

    /**
     * 删除购物车中选中的商品
     * @param cartKey
     */
    void deleteChecked(String cartKey);

    /**
     * 获取指定购物车中所有选中的商品
     * @param cartKey
     * @return
     */
    List<CartInfo> getCheckedItems(String cartKey);

    /**
     * 合并购物车
     */
    void mergeUserAndTempCart();

}
