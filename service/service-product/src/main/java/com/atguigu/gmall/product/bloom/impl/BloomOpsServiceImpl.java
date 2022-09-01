package com.atguigu.gmall.product.bloom.impl;

import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/9/1 18:44
 * @Version 1.0
 */
@Service
public class BloomOpsServiceImpl implements BloomOpsService {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    SkuInfoService skuInfoService;
    /**
     * 重建布隆过滤器
     * @param bloomName
     * @param dataQueryService
     */
    @Override
    public void rebuildBloom(String bloomName, BloomDataQueryService dataQueryService) {

        RBloomFilter<Object> oldBloomFilter = redissonClient.getBloomFilter(bloomName);
        //1.准备一个新的布隆过滤器 将所有的东西都初始化好
        String newBloomName = bloomName + "_new";
        //获取新的布隆过滤器
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(newBloomName);

        //2.拿到所有id
        //List<Long> allSkuId = skuInfoService.findAllSkuId();
        List allSkuId = dataQueryService.queryData();

        //3.初始化新布隆
        bloomFilter.tryInit(5000000,0.00001);
        //给新布隆添加数据
        allSkuId.forEach(skuId->{
            bloomFilter.add(skuId);
        });


        //2.1将两个交换  大数据量的删除可能会导致redis卡死
        //修改名字 下线老布隆
        oldBloomFilter.rename("backUp_bloom");

        //2.2新布隆上线
        bloomFilter.rename(bloomName);

        //2.3删除老布隆
        oldBloomFilter.deleteAsync();
        //2.4删除备份布隆
        redissonClient.getBloomFilter("backUp_bloom").deleteAsync();

    }
}
