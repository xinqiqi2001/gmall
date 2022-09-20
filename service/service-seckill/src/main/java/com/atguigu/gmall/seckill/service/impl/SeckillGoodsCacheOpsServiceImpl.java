package com.atguigu.gmall.seckill.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsCacheOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.spi.CurrencyNameProvider;
import java.util.stream.Collectors;

/**
 * @Author Xiaoxin
 * @Date 2022/9/20 23:54
 * @Version 1.0
 */
@Service
public class SeckillGoodsCacheOpsServiceImpl implements SeckillGoodsCacheOpsService {


    @Autowired
    StringRedisTemplate redisTemplate;

    private Map<Long, SeckillGoods> seckillGoodsMap = new ConcurrentHashMap<>();

    /**
     * 缓存所有当天的秒杀商品(上架)
     *
     * @param seckillGoodsList //所有当天秒杀商品的集合
     */
    @Override
    public void upSeckillGoods(List<SeckillGoods> seckillGoodsList) {

        String date = DateUtil.formatDate(new Date());
        //绑定操作redis的key
        BoundHashOperations<String, String, String> hashOps =
                redisTemplate.boundHashOps(SysRedisConst.CACHE_SECKILL_GOODS + date);

        //缓存一天(因为只保存当天的秒杀数据) 为了方便秒杀结束后定时任务的信息统计 缓存2天
        hashOps.expire(2, TimeUnit.DAYS);

        seckillGoodsList.stream().forEach(seckillGoods -> {

            //1.保存当天秒杀信息到redis
            hashOps.put(seckillGoods.getId() + "", Jsons.toStr(seckillGoods));
            //2.商品的库存数量独立存储
            String cacheKey = SysRedisConst.CACHE_SECKILL_GOODS_STOCK + seckillGoods.getSkuId();
            //3.缓存商品的(秒杀的)精确库存
            redisTemplate.opsForValue().setIfAbsent(cacheKey, seckillGoods.getStockCount() + "", 1, TimeUnit.DAYS);

            //4.存入本地缓存
            seckillGoodsMap.put(seckillGoods.getSkuId(), seckillGoods);
        });
    }

    @Override
    public void clearCache() {
        //清除本地缓存
        seckillGoodsMap.clear();
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsFromLocal() {
        //1.优先查询本地缓存
        List<SeckillGoods> goods = seckillGoodsMap.values().
                stream().
                sorted(Comparator.comparing(SeckillGoods::getStartTime)).
                collect(Collectors.toList());
        //2.本地没有
        if (goods == null || goods.size() == 0) {
            //查询redis数据库并将数据同步到本地
            syncLocalAndRedisCache();
            //本地拿到数据之后查询数据
            goods = seckillGoodsMap.values().
                    stream().
                    sorted(Comparator.comparing(SeckillGoods::getStartTime)).
                    collect(Collectors.toList());
        }

        return goods;
    }

    /**
     * 查询redis中当天缓存的秒杀商品并同步到本地缓存
     */
    @Override
    public void syncLocalAndRedisCache() {

        //1.查询redis中存储的当天的秒杀数据
        List<SeckillGoods> goods = getSeckillGoodsFromRemote();

        //2.同步到本地缓存
        goods.forEach(seckillGood -> {
            seckillGoodsMap.put(seckillGood.getSkuId(), seckillGood);
        });


    }

    @Override
    public SeckillGoods getSeckillGoodsDetail(Long skuId) {
        //1、先拿本地缓存
        SeckillGoods goods = seckillGoodsMap.get(skuId);

        //2、本地缓存没有
        if(goods == null){
            syncLocalAndRedisCache();
            goods = seckillGoodsMap.get(skuId);
        }
        return goods;
    }

    /**
     * 查询redis中当天秒杀的所有商品
     * @return
     */
    @Override
    public List<SeckillGoods> getSeckillGoodsFromRemote() {
        String cacheKey = SysRedisConst.CACHE_SECKILL_GOODS + DateUtil.formatDate(new Date());

        List<Object> values = redisTemplate.opsForHash().values(cacheKey);

        List<SeckillGoods> seckillGoods = values.stream().
                map(str -> Jsons.toObj(str.toString(), SeckillGoods.class)).
                sorted(Comparator.comparing(SeckillGoods::getStartTime)).
                collect(Collectors.toList());
        return seckillGoods;
    }


}
