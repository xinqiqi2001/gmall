package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.utiles.AuthUtils;
import com.atguigu.gmall.feign.product.SkuProductFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author Xiaoxin
 * @Date 2022/9/8 20:02
 * @Version 1.0
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SkuProductFeignClient skuFeignClient;

    /**
     * 添加指定商品到购物车 添加num件
     *
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public SkuInfo addToCart(Long skuId, Integer num) {
        //1.决定购物车使用哪个键(用户id 或临时id)
        String carKey = determinCartKey();
        //2.给购物车添加指定商品
        SkuInfo skuInfo = addItemToCart(skuId, num, carKey);

        //3.给购物车设置超时时间
        //获取用户id或临时用户id
        UserAuthInfo currentAuthInfo = AuthUtils.getCurrentAuthInfo();
        if (currentAuthInfo.getUserId() == null) {
            //未登录状态操作数据库
            String tempKey = SysRedisConst.CART_KEY + currentAuthInfo.getUserTempId();
            //临时购物车都有过期时间，自动延期
            redisTemplate.expire(tempKey, 30, TimeUnit.DAYS);
        }

        return skuInfo;
    }


    /**
     * 给购物车(carKey) 添加商品id是skuId的商品 添加num件 返回保存成功的商品
     *
     * @param skuId
     * @param num
     * @param cartKey
     * @return
     */
    @Override
    public SkuInfo addItemToCart(Long skuId, Integer num, String cartKey) {

        //拿到了购物车
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);
        //1.如果该商品在该购物车中没有  就添加   在redis中购物车里找指定商品信息
        Boolean aBoolean = cart.hasKey(skuId.toString());
        //获取当前购物车的品类数量
        Long itemsSize = cart.size();
        if (!aBoolean) {
            if(itemsSize + 1 > SysRedisConst.CART_ITEMS_LIMIT ){
                //异常机制
                throw new GmallException(ResultCodeEnum.CART_OVERFLOW);
            }
            //没找到该商品  远程调用skuProduct添加
            SkuInfo data = skuFeignClient.getSkuInfo(skuId).getData();

            //将要存到redis中购物车的商品转换成指定类型
            CartInfo item = converSkuInfo2CartInfo(data);
            //设置前端传来的添加到购物车商品的数量
            item.setSkuNum(num);

            //添加到购物车
            cart.put(skuId.toString(), Jsons.toStr(item));
            return data;
        } else {
            //2.如果该商品在该购物车中有  那么就给数量+1
            //在购物车中找到了商品 num数量+1
            //2.1、获取实时价格
            BigDecimal newPrice = skuFeignClient.getSku1010Price(skuId).getData();

            //2.2 获取原来的信息
            CartInfo fromCart = getItemFromCart(cartKey, skuId);
            //2.3 更新信息
            //更新最新价格
            fromCart.setSkuPrice(newPrice);
            //更新修改时间
            fromCart.setUpdateTime(new Date());
            //更新购物车中该商品的数量
            fromCart.setSkuNum(fromCart.getSkuNum() + num);
            //2.4 同步到redis
            cart.put(skuId.toString(), Jsons.toStr(fromCart));

            SkuInfo skuInfo = converCartInfo2SkuInfo(fromCart);
            return skuInfo;
        }
    }

    /**
     * 根据商品(skuId)获取 指定购物车中商品的信息
     * @param cartKey
     * @param skuId
     */
    @Override
    public CartInfo getItemFromCart(String cartKey, Long skuId) {
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(cartKey);
        //1、拿到购物车中指定商品的json数据
        String jsonData = ops.get(skuId.toString());
        return Jsons.toObj(jsonData,CartInfo.class);
    }

    /**
     * 获取指定购物车中所有商品
     * @param cartKey
     * @return
     */
    @Override
    public List<CartInfo> getCartList(String cartKey) {

        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

        //用流式编程查询指定购物车中的所有数据 并返回一个集合 泛型是CartInfo 并按照创建时间进行排序
        List<CartInfo> collect = hashOps.values().stream()
                .map(str -> Jsons.toObj(str, CartInfo.class))
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .collect(Collectors.toList());
        return collect;
    }

    /**
     * 修改指定购物车中的指定商品的数量
     * @param skuId
     * @param num
     * @param cartKey
     */
    @Override
    public void updateItemNum(Long skuId, Integer num, String cartKey) {
        //获取指定购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

        //获取指定购物车中的指定商品
        CartInfo fromCart = getItemFromCart(cartKey, skuId);
        //修改商品数量信息(+1 或 -1)
        fromCart.setSkuNum(fromCart.getSkuNum()+num);
        //更新商品的修改时间
        fromCart.setUpdateTime(new Date());
        //将这个商品再存入数据库
        hashOps.put(skuId.toString(),Jsons.toStr(fromCart));
    }

    /**
     * 修改商品的选中状态
     * @param skuId
     * @param status
     * @param cartKey
     */
    @Override
    public void updateChecked(Long skuId, Integer status, String cartKey) {
        //获取指定购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

        //获取指定购物车中的指定商品
        CartInfo fromCart = getItemFromCart(cartKey, skuId);
        //修改商品的选中状态
        fromCart.setIsChecked(status);
        //更新商品的修改时间
        fromCart.setUpdateTime(new Date());
        //将这个商品再存入数据库
        hashOps.put(cartKey,Jsons.toStr(fromCart));
    }

    /**
     * 删除购物车中的商品
     * @param skuId
     * @param cartKey
     */
    @Override
    public void deleteCartItem(Long skuId, String cartKey) {
        //获取指定购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

        hashOps.delete(skuId.toString());

    }

    @Override
    public void deleteChecked(String cartKey) {
        //获取指定购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        //获取到所有选中的商品
        List<String> collect = getCheckedItems(cartKey).stream()
                .map(cartInfo -> cartInfo.getSkuId().toString())
                .collect(Collectors.toList());

        //删除所有选中状态(1为选中)的商品
        if (collect!=null&& collect.size()>0){

            hashOps.delete(collect.toArray());
        }
    }

    /**
     * 获取指定购物车中所有选中的商品
     * @param cartKey
     * @return
     */
    @Override
    public List<CartInfo> getCheckedItems(String cartKey) {
        //获取购物车中所有的商品
        List<CartInfo> cartList = getCartList(cartKey);

        //从所有商品中筛选出选中状态的商品(isChecked字段为1的)
        List<CartInfo> collect = cartList.stream()
                .filter(cartInfo -> cartInfo.getIsChecked() == 1)
                .collect(Collectors.toList());

        return collect;
    }

    @Override
    public void mergeUserAndTempCart() {

        UserAuthInfo authInfo = AuthUtils.getCurrentAuthInfo();
        //1、判断是否需要合并
        if(authInfo.getUserId()!=null && !StringUtils.isEmpty(authInfo.getUserTempId())){
            //2、可能需要合并
            //3、临时购物车有东西。合并后删除临时购物车
            String tempCartKey = SysRedisConst.CART_KEY+authInfo.getUserTempId();
            //3.1、获取临时购物车中所有商品
            List<CartInfo> tempCartList = getCartList(tempCartKey);
            if(tempCartList!=null && tempCartList.size()>0){
                //临时购物车有数据，需要合并
                String userCartKey = SysRedisConst.CART_KEY+authInfo.getUserId();
                for (CartInfo info : tempCartList) {
                    Long skuId = info.getSkuId();
                    Integer skuNum = info.getSkuNum();
                    addItemToCart(skuId,skuNum,userCartKey);
                    //3.2、合并成一个商品就删除一个
                    redisTemplate.opsForHash().delete(tempCartKey,skuId.toString());
                }

            }
        }

    }

    /**
     * 根据用户信息决定使用哪个(用户id或临时用户id)作为购物键
     *
     * @return
     */
    @Override
    public String determinCartKey() {
        UserAuthInfo authInfo = AuthUtils.getCurrentAuthInfo();
        String cartKey = SysRedisConst.CART_KEY;
        if (authInfo.getUserId() != null) {
            //用户登录了 将用户id作为key
            cartKey = cartKey + "" + authInfo.getUserId();
            return cartKey;
        }
        //没有登录 将临时用户id作为key
        cartKey = cartKey + "" + authInfo.getUserTempId();

        return cartKey;
    }

    /**
     * 把SkuInfo转为CartInfo
     *
     * @param data
     * @return
     */
    private CartInfo converSkuInfo2CartInfo(SkuInfo data) {

        CartInfo cartInfo = new CartInfo();
        cartInfo.setSkuId(data.getId());
        cartInfo.setImgUrl(data.getSkuDefaultImg());
        cartInfo.setSkuName(data.getSkuName());
        cartInfo.setIsChecked(1);
        cartInfo.setCreateTime(new Date());
        cartInfo.setUpdateTime(new Date());
        cartInfo.setSkuPrice(data.getPrice());
        cartInfo.setCartPrice(data.getPrice());


        return cartInfo;
    }

    /**
     * 把CartInfo转为SkuInfo 返回给前端
     *
     * @param cartInfo
     * @return
     */
    private SkuInfo converCartInfo2SkuInfo(CartInfo cartInfo) {
        SkuInfo skuInfo = new SkuInfo();


        skuInfo.setSkuName(cartInfo.getSkuName());
        skuInfo.setSkuDefaultImg(cartInfo.getImgUrl());
        skuInfo.setId(cartInfo.getSkuId());

        return skuInfo;
    }

}
