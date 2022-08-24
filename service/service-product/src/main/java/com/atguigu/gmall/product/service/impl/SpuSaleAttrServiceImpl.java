package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
* @author Xiaoxin
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service实现
* @createDate 2022-08-23 20:48:38
*/
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr>
    implements SpuSaleAttrService{
    @Autowired
    SpuSaleAttrValueService saleAttrValueService;

    @Override
    public List<SpuSaleAttr>  saleAttrAndValue(Long spuId) {

        List<SpuSaleAttr> list = this.list(new LambdaQueryWrapper<SpuSaleAttr>().eq(SpuSaleAttr::getSpuId, spuId));


        for (SpuSaleAttr spuSaleAttr : list) {

            Long baseSaleAttrId = spuSaleAttr.getBaseSaleAttrId();

            List<SpuSaleAttrValue> values = saleAttrValueService.list(new LambdaQueryWrapper<SpuSaleAttrValue>().eq(SpuSaleAttrValue::getSpuId, spuId)
                            .eq(SpuSaleAttrValue::getBaseSaleAttrId,baseSaleAttrId));

            spuSaleAttr.setSpuSaleAttrValueList(values);
        }

        return list;
    }
}




