package com.atguigu.gmall.item.service.impl;


import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.SkuProductFeignClient;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.starter.cache.annotation.GmallCache;
import com.atguigu.starter.cache.service.CacheOpsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author Xiaoxin
 * @Date 2022/8/26 22:17
 * @Version 1.0
 */
@Service
@Slf4j
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuProductFeignClient skuDetailFeignClient;

    //自定义的线程池
    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    CacheOpsService cacheOpsService;


    /**
     * 缓存优化前查询商品详情
     */
    public SkuDetailTo getSkuDetailFromRpc(Long skuId) {

        SkuDetailTo detailTo = new SkuDetailTo();
        //TODO 远程分步查询出商品的详细信息

        //1、查基本信息
        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            Result<SkuInfo> result = skuDetailFeignClient.getSkuInfo(skuId);
            SkuInfo skuInfo = result.getData();
            detailTo.setSkuInfo(skuInfo);
            return skuInfo;
        }, executor);


        //2、查商品图片信息
        //skuInfoFuture.thenRun()  不接受上次的结果 且没有返回值
        //skuInfoFuture.thenAcceptAsync() 接收上次的结果 但是没有返回值
        //skuInfoFuture.thenApplyAsync() 接受上次的记过 且有返回值

        CompletableFuture<Void> imageFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo!=null){
                Result<List<SkuImage>> skuImages = skuDetailFeignClient.getSkuImages(skuId);
                skuInfo.setSkuImageList(skuImages.getData());
            }

        }, executor);


        //3、查商品实时价格
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            Result<BigDecimal> price = skuDetailFeignClient.getSku1010Price(skuId);
            detailTo.setPrice(price.getData());
        }, executor);


        //4、查销售属性名值
        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo!=null){
                Long spuId = skuInfo.getSpuId();
                Result<List<SpuSaleAttr>> saleattrvalues = skuDetailFeignClient.getSkuSaleattrvalues(skuId, spuId);
                detailTo.setSpuSaleAttrList(saleattrvalues.getData());
            }
        }, executor);


        //5、查sku组合
        CompletableFuture<Void> skuValueFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo!=null){
                Result<String> sKuValueJson = skuDetailFeignClient.getSKuValueJson(skuInfo.getSpuId());
                detailTo.setValuesSkuJson(sKuValueJson.getData());
            }
        }, executor);


        //6、查分类
        CompletableFuture<Void> categoryFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo!=null){
                Result<CategoryViewTo> categoryView = skuDetailFeignClient.getCategoryView(skuInfo.getCategory3Id());
                detailTo.setCategoryView(categoryView.getData());
            }
        }, executor);

        /**
         * 阻塞  等上面线程都执行完毕之后join在放主线程通行
         */
        CompletableFuture
                .allOf(imageFuture, priceFuture, saleAttrFuture, skuValueFuture, categoryFuture)
                .join();
        return detailTo;
    }

    /**
     *
     * 加布隆过滤器和分布式锁
     * @param skuId
     * @return
     */
    public SkuDetailTo getSkuDetailWithCache(Long skuId) {

        String cacheKey = SysRedisConst.SKU_INFO_PREFIX + skuId;
        //1.先查缓存
        SkuDetailTo cacheData = cacheOpsService.getCacheData(cacheKey, SkuDetailTo.class);
        //2.判断缓存中有没有
        if (cacheData == null) {
            //3.缓存没有 回源查询
            //3.1先判断布隆过滤器中有没有 布隆过滤器有的话回源查询  没有的话直接返回null
            boolean contain = cacheOpsService.bloomContains(skuId);
            if (!contain) {
                //布隆过滤器中没有 可能是隐藏的攻击  直接返回null
                return null;
            }
            //3.2布隆过滤器有 回源查询时需要加锁
            boolean tryLock = cacheOpsService.tryLock(skuId);
            if (tryLock) {
                //拿到锁了
                //回源查询
                SkuDetailTo fromRpc = this.getSkuDetailFromRpc(skuId);

                //保存进redis时里面判断了查询的fromRpc是不是为空 不是空就保存  是空值的话就存入一个x
                cacheOpsService.saveData(cacheKey, fromRpc);

                //解锁
                cacheOpsService.unlock(skuId);
                return fromRpc;
            }

            //没有抢到锁
            try {//睡1s然后查询数据库  因为没有抢到锁就证明有线程在操作  可能已经存入到缓存数据库了
                Thread.sleep(1000);
                //查询缓存
                return cacheData = cacheOpsService.getCacheData(cacheKey, SkuDetailTo.class);
            } catch (Exception e) {
            }

        }

        //4.缓存中有 直接返回
        return cacheData;
    }

    /**
     * 将getSkuDetailWithCache()方法 用Aop抽取
     * 代理的目标方法
     * @param skuId
     * @return
     */
    //表达式的$params代表方法的所有参数列表
    @GmallCache(cacheKey =SysRedisConst.SKU_INFO_PREFIX+ "#{#params[0]}",
            bloomName = SysRedisConst.BLOOM_SKUID,
            bloomValue = "#{#params[0]}",
            lockName = SysRedisConst.LOCK_SKU_DETAIL+"#{#params[0]}",
            ttl = 60 * 60 *24*7
    )
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        SkuDetailTo fromRpc = this.getSkuDetailFromRpc(skuId);
        return fromRpc;
    }

    @Autowired
    SearchFeignClient searchFeignClient;
    @Override
    public void updateHotScore(Long skuId) {
        //redis统计得分
        Long increment = redisTemplate.opsForValue()
                .increment(SysRedisConst.SKU_HOTSCORE_PREFIX + skuId);
        if(increment % 100 ==0){
            //累积到一定量更新es
            searchFeignClient.updateHotScore(skuId,increment);
        }
    }
}
