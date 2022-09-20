package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.model.activity.SeckillGoods;

import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/9/20 23:54
 * @Version 1.0
 */

//对缓存的所有操作
public interface SeckillGoodsCacheOpsService {
    /**
     * 缓存所有当天的秒杀商品(上架)
     * @param seckillGoodsList //所有当天秒杀商品的集合
     */
    void upSeckillGoods(List<SeckillGoods> seckillGoodsList);

    /**
     * 清除本地缓存
     */
    void clearCache();


    /**
     * 查询缓存中是否有当天秒杀数据
     * @return
     */
    List<SeckillGoods> getSeckillGoodsFromLocal();

    /**
     * 本地与redis同步缓存
     */
    void syncLocalAndRedisCache();

    /**
     * 在缓存中查询秒杀商品详情
     * @param skuId
     * @return
     */
    SeckillGoods getSeckillGoodsDetail(Long skuId);

    /**
     * 查到redis数据
     * @return
     */
    List<SeckillGoods> getSeckillGoodsFromRemote();
}
