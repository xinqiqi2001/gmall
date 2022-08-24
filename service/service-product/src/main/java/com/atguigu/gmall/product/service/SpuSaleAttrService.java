package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Xiaoxin
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service
* @createDate 2022-08-23 20:48:38
*/
public interface SpuSaleAttrService extends IService<SpuSaleAttr> {

    /**
     * 根据spuId获取销售属性
     * @return
     * @param spuId
     */
    List<SpuSaleAttr> saleAttrAndValue(Long spuId);
}