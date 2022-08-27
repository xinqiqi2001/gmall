package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.feign.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Xiaoxin
 * @Date 2022/8/26 22:17
 * @Version 1.0
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {

        Result<SkuDetailTo> result = skuDetailFeignClient.getSkuDetail(skuId);
        //TODO 远程查询出商品的详细信息
        //1.商品(sku)所属的完成分类信息
        //2.商品(sku)的基本信息[重量 价格 名字...] sku_info
        //3.商品(sku)的图片  sku_image
        //4.商品(sku)所属的spu当时定义的所有销售属性名值组合spu_sale_attr spu_sale_attr_value
        // 并标识出当前sku和spu的那种组合 方便页面进行高亮显示  sku_sale_attr_value

        //5.商品(sku)类似推荐(x)
        //6.商品(sku)介绍(x)
        //7.商品(sku)的规格参数
        //8.商品(sku)售后,评论(x)

        return result.getData();
    }



}
