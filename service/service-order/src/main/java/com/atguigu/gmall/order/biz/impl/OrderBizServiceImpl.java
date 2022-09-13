package com.atguigu.gmall.order.biz.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.utiles.AuthUtils;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.SkuProductFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.order.CartInfoVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.google.common.collect.Lists;

import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
/**
 * @Author Xiaoxin
 * @Date 2022/9/13 18:54
 * @Version 1.0
 */
@Service
public class OrderBizServiceImpl implements OrderBizService {


    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    SkuProductFeignClient skuProductFeignClient;

    @Autowired
    WareFeignClient wareFeignClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderInfoService orderInfoService;


    /**
     * Result<OrderConfirmDataVo>
     *
     * @return
     */
    @Override
    public OrderConfirmDataVo getConfirmData() {
        OrderConfirmDataVo orderConfirmDataVo = new OrderConfirmDataVo();
        //1、获取购物车中选中的商品
        List<CartInfo> data = cartFeignClient.getChecked().getData();
        List<CartInfoVo> infoVos = data.stream().map(cartInfo -> {
            CartInfoVo infoVo = new CartInfoVo();
            infoVo.setSkuId(cartInfo.getSkuId());
            infoVo.setImgUrl(cartInfo.getImgUrl());
            infoVo.setSkuName(cartInfo.getSkuName());
            infoVo.setSkuNum(cartInfo.getSkuNum());
            //实时查价
            Result<BigDecimal> price = skuProductFeignClient.getSku1010Price(cartInfo.getSkuId());
            infoVo.setOrderPrice(price.getData());
            //查询商品库存
            String stock = wareFeignClient.hasStock(cartInfo.getSkuId(), cartInfo.getSkuNum());
            infoVo.setHasStock(stock);

            return infoVo;

        }).collect(Collectors.toList());

        //设置所有商品信息
        orderConfirmDataVo.setDetailArrayList(infoVos);

        //2、统计商品的总数量
        Integer totalNum = infoVos.stream().map(CartInfoVo::getSkuNum)
                .reduce((o1, o2) -> o1 + o2)
                .get();

        orderConfirmDataVo.setTotalNum(totalNum);
        //3、统计商品的总金额
        BigDecimal totalAmount = infoVos.stream()
                .map(items -> items.getOrderPrice().multiply(new BigDecimal(items.getSkuNum() + "")))
                .reduce((o1, o2) ->
                        o1.add(o2)
                ).get();

        orderConfirmDataVo.setTotalAmount(totalAmount);
        //4、获取用户收货地址列表
        Result<List<UserAddress>> addressList = userFeignClient.getUserAddressList();
        orderConfirmDataVo.setUserAddressList(addressList.getData());
        //5、生成一个追踪号
        String tradeNo = generateTradeNo();
        //5.1、订单的唯一追踪号，对外交易号（和第三方交互）。
        //5.2、用来防重复提交。 做防重令牌
        orderConfirmDataVo.setTradeNo(tradeNo);
        return orderConfirmDataVo;
    }

    @Override
    public String generateTradeNo() {

        long millis = System.currentTimeMillis();
        UserAuthInfo info = AuthUtils.getCurrentAuthInfo();
        String tradeNo = millis + "_" + info.getUserId();

        //令牌在redis存一份。
        redisTemplate.opsForValue()
                .set(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo, "1", 15, TimeUnit.MINUTES);

        return tradeNo;
    }

    /**
     * 校验令牌
     * @param tradeNo
     * @return
     */
    @Override
    public boolean checkTradeNo(String tradeNo) {
        //1、先看有没有，如果有就是正确令牌, 1, 0 。脚本校验令牌
        String lua = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then " +
                "    return redis.call(\"del\",KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end";

        /**
         * RedisScript<T> script,
         * List<K> keys, Object... args
         * execute执行lua脚本
         * 判断执行完毕后是不是1
         */
        Long execute = redisTemplate.execute(new DefaultRedisScript<Long>(lua, Long.class),
                Arrays.asList(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo),
                new String[]{"1"});

        if(execute > 0){
            //令牌正确，并且已经删除
            return true;
        }

        return false;
    }

    @Override
    public Long submitOrder(OrderSubmitVo submitVo, String tradeNo) {
        //1.校验令牌
        boolean b = checkTradeNo(tradeNo);
        if (!b){
            throw new GmallException(ResultCodeEnum.TOKEN_INVAILD);
        }


        //2.校验库存
        List<String> noStockSkus = new ArrayList<>();
        submitVo.getOrderDetailList().forEach(cartInfoVo ->{

            Long skuId = cartInfoVo.getSkuId();
            //2.1 根据提交来的商品id和商品数量用wareFeignClient来判断库存是否足够(1是足够 0是不够)
            String hasStock = wareFeignClient.hasStock(skuId, cartInfoVo.getSkuNum());
            if (!"1".equals(hasStock)){
                //没有库存
                noStockSkus.add(cartInfoVo.getSkuName());
            }

        } );

        //2.2遍历集合里有没有异常 有异常的话一并抛出
        if(noStockSkus.size() > 0){
            //2.21有异常
            String skuNames = noStockSkus.stream()
                    .reduce((s1, s2) -> s1 + " " + s2)
                    .get();

            throw  new GmallException(
                    //2.22抛出指定异常信息
                    ResultCodeEnum.ORDER_NO_STOCK.getMessage() + skuNames,
                    ResultCodeEnum.ORDER_NO_STOCK.getCode());
        }


        //3.验证价格
        List<String> skuNames = new ArrayList<>();
        //获取全部商品信息
        submitVo.getOrderDetailList().forEach(cartInfoVo -> {
            //获取实时价格
            Result<BigDecimal> price = skuProductFeignClient.getSku1010Price(cartInfoVo.getSkuId());

            if (!price.getData().equals(cartInfoVo.getOrderPrice())){
                //价格不相等 说明有异常 拿取异常商品的名字
                skuNames.add(cartInfoVo.getSkuName());
            }

        });

        if (skuNames.size()>0){
            //说明有异常
            String skuName = skuNames.stream()
                    .reduce((s1, s2) -> s1 + " " + s2)
                    .get();
            //有价格发生变化的商品
            throw  new GmallException(
                    ResultCodeEnum.ORDER_PRICE_CHANGED.getMessage() + "<br/>" +skuName,
                    ResultCodeEnum.ORDER_PRICE_CHANGED.getCode());
        }

        //4、把订单信息保存到数据库
        Long orderId = orderInfoService.saveOrder(submitVo,tradeNo);

        //5、清除购物车中选中的商品
        cartFeignClient.deleteChecked();

        //45min不支付就要关闭。
        //TODO 延时任务未做
        return orderId;
    }
}
