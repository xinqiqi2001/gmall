package com.atguigu.gmall.seckill.schedule;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsCacheOpsService;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/9/20 21:04
 * @Version 1.0
 */
@Service
@Slf4j
public class SeckillGoodsUpService {


    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    SeckillGoodsCacheOpsService cacheOpsService;

    /**
     * 凌晨2点上架当天秒杀的商品
     */
//    @Scheduled(cron = "0 0 2 * * ?")
    @Scheduled(cron = "0 * * * * ?")//每分钟上架一次
    public void upSeckillGoods(){

        //查询出当天参加秒杀的所有商品
        log.info("正在上架秒杀商品");
        //1.拿到当天参与秒杀的所有商品
        List<SeckillGoods> seckillGoodsList =
                seckillGoodsService.getCurrentDaySeckillGoodsList();

        //2保存到redis
        //上架所有当天秒杀的商品(缓存到redis和本地缓存)
        cacheOpsService.upSeckillGoods(seckillGoodsList);


    }
    //在秒杀商品上架之前结上一天的账(清除前一天的本地缓存)
    @Scheduled(cron = "0 0 1 * * ?")
    public void currentDaySeckillEnd(){
        //1.清除缓存
        cacheOpsService.clearCache();
    }


}
