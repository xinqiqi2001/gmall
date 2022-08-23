package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Xiaoxin
* @description 针对表【base_attr_value(属性值表)】的数据库操作Service
* @createDate 2022-08-23 20:48:38
*/
public interface BaseAttrValueService extends IService<BaseAttrValue> {

    /**
     * 根据平台属性id 查询这个属性的所有信息
     * @param attrId
     * @return
     */
    List<BaseAttrValue> getAttrValueList(Long attrId);
}
