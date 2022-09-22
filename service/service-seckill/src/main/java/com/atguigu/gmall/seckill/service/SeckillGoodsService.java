package com.atguigu.gmall.seckill.service;


import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Xiaoxin
* @description 针对表【seckill_goods】的数据库操作Service
* @createDate 2022-09-20 19:01:57
*/
public interface SeckillGoodsService extends IService<SeckillGoods> {




    /**
     * 获取当天所有秒杀商品的数据
     * @return
     */
    List<SeckillGoods> getCurrentDaySeckillGoodsList();


    /**
     * 查询缓存中是否有当天秒杀数据
     * @return
     */
    List<SeckillGoods> getCurrentDaySeckillGoodsCache();

    /**
     * 根据商品id 查询秒杀商品详情
     * @param skuId
     * @return
     */
    SeckillGoods getSeckillGoodDetail(Long skuId);

    /**
     * 根据商品id减秒杀库存
     * @param skuId
     */
    void deduceSeckillGoods(Long skuId);
}
