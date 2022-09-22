package com.atguigu.gmall.seckill.biz;

import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.vo.seckill.SeckillOrderConfirmVo;

/**
 * @Author Xiaoxin
 * @Date 2022/9/22 11:29
 * @Version 1.0
 */
public interface SeckillBizService {
    /**
     * 获取当前商品
     * @param skuId
     * @return
     */
    String getSeckillCode(Long skuId);

    /**
     * 校验秒杀码
     */
    boolean checkSeckillCode(Long skuId,String code);

    /**
     * 秒杀商品下单
     * @param skuId
     * @param skuIdStr
     * @return
     */
    ResultCodeEnum seckillOrder(Long skuId, String skuIdStr);

    /**
     * 检查订单的状态
     * @param skuId
     * @return
     */
    ResultCodeEnum checkSeckillOrderStatus(Long skuId);

    /**
     * 获取秒杀确认页数据
     * @param skuId
     * @return
     */
    SeckillOrderConfirmVo getSeckillOrderConfirmVo(Long skuId);
}
