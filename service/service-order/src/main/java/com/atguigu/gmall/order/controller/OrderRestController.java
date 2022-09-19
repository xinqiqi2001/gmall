package com.atguigu.gmall.order.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.utiles.AuthUtils;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/order/auth")
@RestController
public class OrderRestController {


    @Autowired
    OrderInfoService orderInfoService;
    @Autowired
    OrderBizService orderBizService;

    /**
     * 提交订单
     *
     * @return
     */
    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo") String tradeNo,
                              @RequestBody OrderSubmitVo submitVo) {

        Long orderId = orderBizService.submitOrder(submitVo, tradeNo);

        return Result.ok(orderId.toString());
    }


    @Autowired
    OrderDetailService orderDetailService;
    @GetMapping("/{pn}/{ps}")
    public Result orderList(@PathVariable("pn") Long pn, @PathVariable("ps") Long ps) {

        Page<OrderInfo> orderInfoPage = new Page<>(pn, ps);

        Long userId = AuthUtils.getCurrentAuthInfo().getUserId();

        LambdaQueryWrapper<OrderInfo> eq = new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getUserId, userId);

        //查询OrderInfo
        Page<OrderInfo> page = orderInfoService.page(orderInfoPage, eq);
        page.getRecords().stream().parallel().forEach(orderInfo -> {
                    //查询订单详情
            List<OrderDetail> orderDetails = orderDetailService.getOrderDetails(orderInfo.getId(), orderInfo.getUserId());

            orderInfo.setOrderDetailList(orderDetails);
                }
        );


        //查询OrderInfo的所有商品
        return Result.ok(page);
    }
}
