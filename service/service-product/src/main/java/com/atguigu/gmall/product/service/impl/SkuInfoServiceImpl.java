package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.*;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Xiaoxin
 * @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
 * @createDate 2022-08-23 20:48:38
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
        implements SkuInfoService {

    @Autowired
    SkuImageService skuImageService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;
    //事务

    /**
     * 保存sku
     *
     * @param info
     */
    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo info) {
        //1、sku基本信息保存到 sku_info
        save(info);
        Long skuId = info.getId();

        //2、sku的图片信息保存到 sku_image
        info.getSkuImageList().forEach(skuImage -> {
            skuImage.setSkuId(skuId);

        });

        skuImageService.saveBatch(info.getSkuImageList());

        //3、sku的平台属性名和值的关系保存到 sku_attr_value
        List<SkuAttrValue> attrValueList = info.getSkuAttrValueList();

        attrValueList.forEach(attrValue -> {
            attrValue.setSkuId(skuId);
        });

        skuAttrValueService.saveBatch(attrValueList);

        //4、sku的销售属性名和值的关系保存到 sku_sale_attr_value
        List<SkuSaleAttrValue> saleAttrValueList = info.getSkuSaleAttrValueList();

        saleAttrValueList.forEach(saleAttrValue -> {
            saleAttrValue.setSkuId(skuId);
            saleAttrValue.setSpuId(info.getSpuId());
        });

        skuSaleAttrValueService.saveBatch(saleAttrValueList);

    }

    /**
     * 查询商品信息
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        //TODO 最后将这个方法里的其他方法拆分成其他方法

        SkuDetailTo detailTo = new SkuDetailTo();
        //0、查询到商品的基本信息 skuInfo
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);


        //2、商品（sku）的基本信息   对应的表是sku_info
        //把查询到的数据一定放到 SkuDetailTo 中
        detailTo.setSkuInfo(skuInfo);

        //3、商品（sku）的图片        对应的表是sku_image
        List<SkuImage> imageList = skuImageService.getSkuImage(skuId);
        skuInfo.setSkuImageList(imageList);


        //1、商品（sku）所属的完整分类信息：  base_category1、base_category2、base_category3
        CategoryViewTo categoryViewTo = baseCategory3Mapper.getCategoryView(skuInfo.getCategory3Id());
        detailTo.setCategoryView(categoryViewTo);


        //实时价格查询
        BigDecimal price = get1010Price(skuId);
        detailTo.setPrice(price);

        //TODO 改写这个了 4、商品（sku）所属的SPU当时定义的所有销售属性名值组合（按固定的排序展示）。
        //          spu_sale_attr、spu_sale_attr_value
        //          并标识出当前sku到底spu的那种组合，页面要有高亮框 sku_sale_attr_value
        //查询当前sku对应的spu定义的所有销售属性名和值（固定好顺序）并且标记好当前sku属于哪一种组合
        List<SpuSaleAttr> saleAttrList = spuSaleAttrService.getSaleAttrAndValueMarkSku(skuInfo.getSpuId(),skuId);
        detailTo.setSpuSaleAttrList(saleAttrList);

        //--------------------暂时没有这些业务----------------------------
        //5、商品（sku）类似推荐    （x）
        //6、商品（sku）介绍[所属的spu的海报]        spu_poster（x）
        //7、商品（sku）的规格参数                  sku_attr_value
        //8、商品（sku）售后、评论...              相关的表 (x)


        return detailTo;
    }

    @Override
    public BigDecimal get1010Price(Long skuId) {
        //查询实时价格
        BigDecimal price = skuInfoMapper.getRealPrice(skuId);
        return price;
    }
}




