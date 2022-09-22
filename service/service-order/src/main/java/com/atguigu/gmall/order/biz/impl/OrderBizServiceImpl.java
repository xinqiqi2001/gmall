package com.atguigu.gmall.order.biz.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.utiles.AuthUtils;
import com.atguigu.gmall.constant.MqConst;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.SkuProductFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.to.mq.OrderMsg;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.order.*;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;

import com.atguigu.gmall.order.biz.OrderBizService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.jooq.JSON;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

/**
 * @Author Xiaoxin
 * @Date 2022/9/13 18:54
 * @Version 1.0
 */
@Service
public class OrderBizServiceImpl implements OrderBizService {


    @Autowired
    OrderDetailService orderDetailService;

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

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 获取订单页所需要的全部数据
     * (订购物车中选中的商品信息,商品的总数量,总金额,收货地址列表)
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
        //每个商品的价格乘以每个商品的数量 用reduce叠加结果
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

        //令牌在redis存一份。  我们是根据key来辨别  所以放什么value都无所谓
        redisTemplate.opsForValue()
                .set(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo, "1", 15, TimeUnit.MINUTES);

        return tradeNo;
    }

    /**
     * 校验令牌
     *
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

        if (execute > 0) {
            //令牌正确，并且已经删除
            return true;
        }

        return false;
    }

    @Override
    public Long submitOrder(OrderSubmitVo submitVo, String tradeNo) {
        //1.校验令牌
        boolean b = checkTradeNo(tradeNo);
        if (!b) {
            throw new GmallException(ResultCodeEnum.TOKEN_INVAILD);
        }


        //2.校验库存
        List<String> noStockSkus = new ArrayList<>();
        submitVo.getOrderDetailList().forEach(cartInfoVo -> {

            Long skuId = cartInfoVo.getSkuId();
            //2.1 根据提交来的商品id和商品数量用wareFeignClient来判断库存是否足够(1是足够 0是不够)
            String hasStock = wareFeignClient.hasStock(skuId, cartInfoVo.getSkuNum());
            if (!"1".equals(hasStock)) {
                //没有库存
                noStockSkus.add(cartInfoVo.getSkuName());
            }

        });

        //2.2遍历集合里有没有异常 有异常的话一并抛出
        if (noStockSkus.size() > 0) {
            //2.21有异常
            String skuNames = noStockSkus.stream()
                    .reduce((s1, s2) -> s1 + " " + s2)
                    .get();

            throw new GmallException(
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

            if (!price.getData().equals(cartInfoVo.getOrderPrice())) {
                //价格不相等 说明有异常 拿取异常商品的名字
                skuNames.add(cartInfoVo.getSkuName());
            }

        });

        if (skuNames.size() > 0) {
            //说明有异常
            String skuName = skuNames.stream()
                    .reduce((s1, s2) -> s1 + " " + s2)
                    .get();
            //有价格发生变化的商品
            throw new GmallException(
                    //把有异常信息反馈给前端
                    ResultCodeEnum.ORDER_PRICE_CHANGED.getMessage() + ":" + skuName,
                    ResultCodeEnum.ORDER_PRICE_CHANGED.getCode());
        }

        //4、把订单信息保存到数据库
        Long orderId = orderInfoService.saveOrder(submitVo, tradeNo);


        //5、清除购物车中选中的商品
        cartFeignClient.deleteChecked();


        return orderId;
    }

    /**
     * 关闭订单  修改订单状态
     *
     * @param orderId 订单id
     * @param userId  用于id
     */
    @Override
    public void closeOrder(Long orderId, Long userId) {
        ProcessStatus closed = ProcessStatus.CLOSED;
        List<ProcessStatus> expected = Arrays.asList(ProcessStatus.UNPAID, ProcessStatus.FINISHED);
        //如果是未支付 或者是已结束才可以关闭订单
        orderInfoService.changeOrderStatus(orderId, userId, closed, expected);
    }

    /**
     * 把一个大订单拆分成指定的多个小订单(大订单中商品不是从一个仓库里发出的就需要拆分)
     *
     * @param params
     * @return
     */
    @Override
    public List<WareChildOrderVo> orderSplit(OrderWareMapVo params) {
        //1父订单id
        Long orderId = params.getOrderId();
        //1.1、查询父单
        OrderInfo parentOrder = orderInfoService.getById(orderId);
        //1.2、查询父单明细
        List<OrderDetail> details = orderDetailService.getOrderDetails(orderId, parentOrder.getUserId());
        parentOrder.setOrderDetailList(details);
        //2.库存的组合
        List<WareMapItem> items = Jsons.toObj(params.getWareSkuMap(), new TypeReference<List<WareMapItem>>() {
        });
        //3.拆分库存的组合
        List<OrderInfo> collect = items.stream().map(wareMapItem -> {
            //4保存每一个子订单到数据库
            OrderInfo orderInfo = saveChildOrderInfo(wareMapItem, parentOrder);
            return orderInfo;
        }).collect(Collectors.toList());

        //修改父订单状态为已拆分
        orderInfoService.changeOrderStatus(parentOrder.getId(),
                parentOrder.getUserId(),
                ProcessStatus.SPLIT,
                Arrays.asList(ProcessStatus.PAID)
        );
        //4、转换为库存系统需要的数据
        return convertSpiltOrdersToWareChildOrderVo(collect);
    }

    /**
     * 转换为库存系统需要的数据
     * @param spiltOrders
     * @return
     */
    private List<WareChildOrderVo> convertSpiltOrdersToWareChildOrderVo(List<OrderInfo> spiltOrders) {
        List<WareChildOrderVo> orderVos = spiltOrders.stream().map(orderInfo -> {
            WareChildOrderVo orderVo = new WareChildOrderVo();
            //封装:
            orderVo.setOrderId(orderInfo.getId());
            orderVo.setConsignee(orderInfo.getConsignee());
            orderVo.setConsigneeTel(orderInfo.getConsigneeTel());
            orderVo.setOrderComment(orderInfo.getOrderComment());
            orderVo.setOrderBody(orderInfo.getTradeBody());
            orderVo.setDeliveryAddress(orderInfo.getDeliveryAddress());
            orderVo.setPaymentWay(orderInfo.getPaymentWay());
            orderVo.setWareId(orderInfo.getWareId());

            //子订单明细 List<WareChildOrderDetailItemVo>  List<OrderDetail>
            List<WareChildOrderDetailItemVo> itemVos = orderInfo.getOrderDetailList()
                    .stream()
                    .map(orderDetail -> {
                        WareChildOrderDetailItemVo itemVo = new WareChildOrderDetailItemVo();
                        itemVo.setSkuId(orderDetail.getSkuId());
                        itemVo.setSkuNum(orderDetail.getSkuNum());
                        itemVo.setSkuName(orderDetail.getSkuName());
                        return itemVo;
                    }).collect(Collectors.toList());
            orderVo.setDetails(itemVos);
            return orderVo;
        }).collect(Collectors.toList());
        return orderVos;
    }
    /**
     * 保存子订单信息
     *
     * @param wareMapItem 子订单
     * @param parentOrder  父订单
     * @return
     */
    private OrderInfo saveChildOrderInfo(WareMapItem wareMapItem, OrderInfo parentOrder) {
        //1、子订单中的所有商品的id
        List<Long> skuIds = wareMapItem.getSkuIds();
        //2、子订单是在哪个仓库出库的
        Long wareId = wareMapItem.getWareId();


        //3、子订单
        OrderInfo childOrderInfo = new OrderInfo();
        childOrderInfo.setConsignee(parentOrder.getConsignee());
        childOrderInfo.setConsigneeTel(parentOrder.getConsigneeTel());

        //4、获取到子订单的明细
        List<OrderDetail> childOrderDetails = parentOrder.getOrderDetailList()
                .stream()
                .filter(orderDetail -> skuIds.contains(orderDetail.getSkuId()))
                .collect(Collectors.toList());

        //流式计算
        BigDecimal decimal = childOrderDetails.stream()
                .map(orderDetail ->
                        orderDetail.getOrderPrice().multiply(new BigDecimal(orderDetail.getSkuNum() + "")))
                .reduce((o1, o2) -> o1.add(o2))//价格数量叠加
                .get();
        //当前子订单负责所有明细的总价
        childOrderInfo.setTotalAmount(decimal);


        childOrderInfo.setOrderStatus(parentOrder.getOrderStatus());
        childOrderInfo.setUserId(parentOrder.getUserId());
        childOrderInfo.setPaymentWay(parentOrder.getPaymentWay());
        childOrderInfo.setDeliveryAddress(parentOrder.getDeliveryAddress());
        childOrderInfo.setOrderComment(parentOrder.getOrderComment());
        //对外流水号
        childOrderInfo.setOutTradeNo(parentOrder.getOutTradeNo());
        //子订单体
        childOrderInfo.setTradeBody(childOrderDetails.get(0).getSkuName());
        childOrderInfo.setCreateTime(new Date());
        childOrderInfo.setExpireTime(parentOrder.getExpireTime());
        childOrderInfo.setProcessStatus(parentOrder.getProcessStatus());


        //每个子订单未来发货以后这个都不一样
        childOrderInfo.setTrackingNo("");
        childOrderInfo.setParentOrderId(parentOrder.getId());
        childOrderInfo.setImgUrl(childOrderDetails.get(0).getImgUrl());

        //子订单的所有明细。也要保存到数据库
        childOrderInfo.setOrderDetailList(childOrderDetails);
        childOrderInfo.setWareId("" + wareId);
        childOrderInfo.setProvinceId(0L);
        childOrderInfo.setActivityReduceAmount(new BigDecimal("0"));
        childOrderInfo.setCouponAmount(new BigDecimal("0"));
        childOrderInfo.setOriginalTotalAmount(new BigDecimal("0"));

        //根据当前负责的商品决定退货时间
        childOrderInfo.setRefundableTime(parentOrder.getRefundableTime());

        childOrderInfo.setFeightFee(parentOrder.getFeightFee());
        childOrderInfo.setOperateTime(new Date());


        //保存子订单
        orderInfoService.save(childOrderInfo);

        //保存子订单的明细 需要指定id(上面保存子订单成功之后会生成id)
        childOrderInfo.getOrderDetailList().stream().forEach(orderDetail -> orderDetail.setOrderId(childOrderInfo.getId()));

        List<OrderDetail> detailList = childOrderInfo.getOrderDetailList();
        //子单明细保存完成
        orderDetailService.saveBatch(detailList);


        return childOrderInfo;
    }

}
