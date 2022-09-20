package com.atguigu.gmall.seckill.service.impl;


import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.feign.seckill.SeckillFeignClient;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.seckill.service.SeckillGoodsCacheOpsService;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author Xiaoxin
* @description 针对表【seckill_goods】的数据库操作Service实现
* @createDate 2022-09-20 19:01:57
*/
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods>
    implements SeckillGoodsService {

    @Autowired
    SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    SeckillGoodsCacheOpsService cacheOpsService;

    /**
     * 获取当天所有秒杀商品的数据
     * @return
     */
    @Override
    public List<SeckillGoods> getCurrentDaySeckillGoodsList() {

        String date = DateUtil.formatDate(new Date());

        //获取指定时间(那一天)的商品
        return seckillGoodsMapper.getSeckillGoodsByDate(date);
    }

    /**
     * 查询缓存中是否有当天秒杀数据
     * @return
     */
    @Override
    public List<SeckillGoods> getCurrentDaySeckillGoodsCache() {

        //查询缓存中是否有当天秒杀数据
        return cacheOpsService.getSeckillGoodsFromLocal();
    }

    @Override
    public SeckillGoods getSeckillGoodDetail(Long skuId) {

        return cacheOpsService.getSeckillGoodsDetail(skuId);
    }
}




