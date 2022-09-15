package com.atguigu.gmall.order.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.utiles.AuthUtils;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Xiaoxin
 * @Date 2022/9/13 18:52
 * @Version 1.0
 */
@RestController()
@RequestMapping("/api/inner/rpc/order")
public class OrderApiController {

    @Autowired
    OrderBizService orderBizService;

    @Autowired
    OrderInfoService orderInfoService;


    /**
     * 获取订单页所需要的全部数据
     * @return
     */
    @GetMapping("/confirm/data")
    public Result<OrderConfirmDataVo> getOrderConfirmData(){

        OrderConfirmDataVo vo =  orderBizService.getConfirmData();

        return Result.ok(vo);
    }

    /**
     * 提交订单成功后获取订单指定信息回显
     * 获取某个订单数据
     * @param orderId
     * @return
     */
    @GetMapping("/info/{orderId}")
    public Result<OrderInfo> getOrderInfo(@PathVariable("orderId") Long orderId){

        Long userId = AuthUtils.getCurrentAuthInfo().getUserId();

        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getId, orderId)
                .eq(OrderInfo::getUserId, userId);
        //根据订单id和用户id获取订单数据
        OrderInfo orderInfo = orderInfoService.getOne(wrapper);
        return Result.ok(orderInfo);
    }



}
