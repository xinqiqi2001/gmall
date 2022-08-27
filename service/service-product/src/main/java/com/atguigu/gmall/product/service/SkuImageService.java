package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SkuImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Xiaoxin
* @description 针对表【sku_image(库存单元图片表)】的数据库操作Service
* @createDate 2022-08-23 20:48:38
*/
public interface SkuImageService extends IService<SkuImage> {

    /**
     * 根据skuId查询商品（sku）的照片
     * @param skuId
     * @return
     */
    List<SkuImage> getSkuImage(Long skuId);
}
