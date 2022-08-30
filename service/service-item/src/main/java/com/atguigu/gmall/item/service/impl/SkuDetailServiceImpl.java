package com.atguigu.gmall.item.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author Xiaoxin
 * @Date 2022/8/26 22:17
 * @Version 1.0
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    //自定义的线程池
    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    StringRedisTemplate redisTemplate;

    Map<Long,SkuDetailTo> skuCache=new ConcurrentHashMap<>();

    /**
     * 缓存优化前
     */
    public SkuDetailTo getSkuDetailFromRpc(Long skuId) {

//        Result<SkuDetailTo> result = skuDetailFeignClient.getSkuDetail(skuId);

        SkuDetailTo detailTo = new SkuDetailTo();
        //TODO 远程分步查询出商品的详细信息

//        CompletableFuture.supplyAsync(()->{ //会有返回值
//
//            return "";
//        },executor);
//        CompletableFuture.runAsync(()->{ 没有返回值
//
//        },executor);
//
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
            Result<List<SkuImage>> skuImages = skuDetailFeignClient.getSkuImages(skuId);
            skuInfo.setSkuImageList(skuImages.getData());
        }, executor);




        //3、查商品实时价格
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            Result<BigDecimal> price = skuDetailFeignClient.getSku1010Price(skuId);
            detailTo.setPrice(price.getData());
        }, executor);


        //4、查销售属性名值
        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Long spuId = skuInfo.getSpuId();
            Result<List<SpuSaleAttr>> saleattrvalues = skuDetailFeignClient.getSkuSaleattrvalues(skuId, spuId);
            detailTo.setSpuSaleAttrList(saleattrvalues.getData());
        }, executor);


        //5、查sku组合
        CompletableFuture<Void> skuValueFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<String> sKuValueJson = skuDetailFeignClient.getSKuValueJson(skuInfo.getSpuId());
            detailTo.setValuesSkuJson(sKuValueJson.getData());
        }, executor);


        //6、查分类
        CompletableFuture<Void> categoryFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<CategoryViewTo> categoryView = skuDetailFeignClient.getCategoryView(skuInfo.getCategory3Id());
            detailTo.setCategoryView(categoryView.getData());
        },executor);

        /**
         * 阻塞  等上面线程都执行完毕之后join在放主线程通行
         */
        CompletableFuture
                .allOf(imageFuture,priceFuture,saleAttrFuture,skuValueFuture,categoryFuture)
                .join();
        return detailTo;
    }

    /**
     * 用redis缓存优化查询
     * @param skuId
     * @return
     */
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {


        //1查询缓存数据库中是否有需要的信息
        String jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
        if ("x".equals(jsonStr)) {
            //说明之前回源过  但是没有数据 所有放了个字符串作为标记
            return null;
        }

        if (StringUtils.isEmpty(jsonStr)) {
        //2缓存数据库中没有需要的数据
            //2.1回源查询
            SkuDetailTo skuDetail = this.getSkuDetailFromRpc(skuId);

            //判断回源的数据是否为空
            String cacheJson="x";
            if (skuDetail !=null) {
                //如果正常查到值了 那么久正常转化为string字符串返回
                cacheJson = Jsons.toStr(skuDetail);
                //2.2将查询到的数据存入redis缓存数据库中
                redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson,7, TimeUnit.DAYS);

            }else {
                //如果数据库中并没有这条数据 那么就传一个字符串x为占位符
                redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson,30, TimeUnit.MINUTES);
            }

            return skuDetail;
        }
        //3缓存数据库中有 查询出后直接返回
        SkuDetailTo skuDetailTo=Jsons.toObj(jsonStr,SkuDetailTo.class);
        return skuDetailTo;
    }


    /**
     * 使用本地缓存优化
     * 缓存优化后
     * @param skuId
     * @return
     */
//    @Override
//    public SkuDetailTo getSkuDetail(Long skuId) {
//        //1先查看缓存
//        SkuDetailTo cacheData = skuCache.get(skuId);
//        //2判断缓存是否存在
//        if (cacheData==null) {
//            //3没有命中缓存,进行回源查询数据库  因为缓存很浪费资源 所以要尽可能的提高命中缓存
//            SkuDetailTo skuDetailFromRpc = this.getSkuDetailFromRpc(skuId);
//            //4将首次查询到的数据存入到缓存数据库中
//            //此Map是本地缓存 无法持久化 且遇到大数据内存不够
//            skuCache.put(skuId,skuDetailFromRpc);
//            return skuDetailFromRpc;
//        }
//        return cacheData;
//    }
}
