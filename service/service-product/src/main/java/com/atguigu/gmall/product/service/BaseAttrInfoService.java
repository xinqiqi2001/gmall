package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Xiaoxin
* @description 针对表【base_attr_info(属性表)】的数据库操作Service
* @createDate 2022-08-23 20:48:38
*/
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {

    /**
     * 获取某平台的平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<BaseAttrInfo> getAttrInfoAndValueByCategoryId(Long category1Id, Long category2Id, Long category3Id);

    /**
     * 保存平台属性
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
}
