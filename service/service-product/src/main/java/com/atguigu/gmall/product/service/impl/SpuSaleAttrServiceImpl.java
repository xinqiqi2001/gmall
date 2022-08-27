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
        implements SpuSaleAttrService {
    @Autowired
    SpuSaleAttrValueService saleAttrValueService;

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    /**
     * 显示查询所有销售属性名和值
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> saleAttrAndValue(Long spuId) {

        //根据spuId查询属性名
        List<SpuSaleAttr> list = this.list(new LambdaQueryWrapper<SpuSaleAttr>().eq(SpuSaleAttr::getSpuId, spuId));

        //遍历属性名对象  给属性名对象中的属性值属性 赋值
        for (SpuSaleAttr spuSaleAttr : list) {

            //根据销售属性id查询属性值集合
            Long baseSaleAttrId = spuSaleAttr.getBaseSaleAttrId();

            List<SpuSaleAttrValue> values = saleAttrValueService.list(new LambdaQueryWrapper<SpuSaleAttrValue>().eq(SpuSaleAttrValue::getSpuId, spuId)
                    .eq(SpuSaleAttrValue::getBaseSaleAttrId, baseSaleAttrId));

            //将查询到的属性值集合 赋给属性名对象的属性
            spuSaleAttr.setSpuSaleAttrValueList(values);
        }

        return list;
    }

    /**
     *
     * 显示查询某个sku对应的销售属性名和值 并标记出当前sku是什么组合
     * @param spuId
     * @param skuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSaleAttrAndValueMarkSku(Long spuId, Long skuId) {
        List<SpuSaleAttr>saleAttrList=spuSaleAttrMapper.getSaleAttrAndValueMarkSku(spuId,skuId);
        return saleAttrList;
    }
}




