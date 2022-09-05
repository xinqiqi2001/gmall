package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Xiaoxin
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Mapper
* @createDate 2022-08-23 20:48:38
* @Entity com.atguigu.gmall.product.domain.SkuAttrValue
*/
public interface SkuAttrValueMapper extends BaseMapper<SkuAttrValue> {

    /**
     * 根据SkuId查当前sku所有平台属性名和值
     * @param skuId
     * @return
     */
    List<SearchAttr> getSkuAttrNameAndValueName(Long skuId);
}




