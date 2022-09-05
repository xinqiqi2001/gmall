package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Xiaoxin
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Service
* @createDate 2022-08-23 20:48:38
*/
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    /**
     * 查当前sku所有平台属性名和值
     * @param skuId
     * @return
     */
    List<SearchAttr> getSkuAttrNameAndValueName(@Param("skuId")Long skuId);
}
