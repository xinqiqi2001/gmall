package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
