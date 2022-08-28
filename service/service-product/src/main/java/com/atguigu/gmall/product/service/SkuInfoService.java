package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
* @author Xiaoxin
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2022-08-23 20:48:38
*/
public interface SkuInfoService extends IService<SkuInfo> {

    /**
     * 保存sku
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 获取Sku商品
     * @param skuId
     * @return
     */
    SkuDetailTo getSkuDetail(Long skuId);

    /**
     * 获取sku的实时价格
     * @param skuId
     * @return
     */
    BigDecimal get1010Price(Long skuId);

    /**
     * 获取SkuInfo(基本数据)的信息
     * @param skuId
     * @return
     */
    SkuInfo getDetailSkuInfo(Long skuId);

    /**
     * 查询sku的图片信息
     * @param skuId
     * @return
     */
    List<SkuImage> getDetailSkuImages(Long skuId);
}
