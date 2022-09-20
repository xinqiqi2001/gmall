package com.atguigu.gmall.seckill.mapper;


import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Xiaoxin
* @description 针对表【seckill_goods】的数据库操作Mapper
* @createDate 2022-09-20 19:01:57
* @Entity com.atguigu.gmall.seckill.domain.SeckillGoods
*/
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    /**
     * 获取指定时间(那一天)的商品
     * @param date
     * @return
     */
    List<SeckillGoods> getSeckillGoodsByDate(@Param("date") String date);
}




