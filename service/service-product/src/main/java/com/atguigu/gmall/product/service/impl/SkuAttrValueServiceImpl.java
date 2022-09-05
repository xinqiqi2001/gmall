package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Xiaoxin
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Service实现
* @createDate 2022-08-23 20:48:38
*/
@Service
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValue>
    implements SkuAttrValueService{
    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    /**
     * 查当前sku所有平台属性名和值
     * @param skuId
     * @return
     */
    @Override
    public List<SearchAttr> getSkuAttrNameAndValueName(Long skuId) {
        List<SearchAttr> attrs=skuAttrValueMapper.getSkuAttrNameAndValueName(skuId);
        return attrs;
    }
}




