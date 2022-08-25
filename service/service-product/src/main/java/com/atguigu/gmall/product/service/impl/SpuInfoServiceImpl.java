package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Xiaoxin
 * @description 针对表【spu_info(商品表)】的数据库操作Service实现
 * @createDate 2022-08-23 20:48:38
 */
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
        implements SpuInfoService {
    @Resource
    SpuInfoMapper spuInfoMapper;

    @Autowired
    SpuImageService spuImageService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    SpuSaleAttrValueService saleAttrValueService;

    //事务
    @Transactional
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //1把spu的基本信息保存到spu_info表中
        spuInfoMapper.insert(spuInfo);

        //拿到Spu保存后得自增id
        Long spuInfoId = spuInfo.getId();


        //2把 spu的图片保存到spu_image
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        spuImageList.forEach(image -> {
            image.setSpuId(spuInfoId);
        });
        spuImageService.saveBatch(spuImageList);

        //3.保存销售属性名
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        spuSaleAttrList.forEach(attr->{
            //回填spuId
            attr.setSpuId(spuInfoId);
            //4.获取指定销售属性名对应的销售属性值
            List<SpuSaleAttrValue> spuSaleAttrValueList = attr.getSpuSaleAttrValueList();

            spuSaleAttrValueList.forEach(value->{
                //回填spu_id
                value.setSpuId(spuInfoId);
                //回填销售属性名
                String saleAttrName = attr.getSaleAttrName();
                value.setSaleAttrName(saleAttrName);
            });
            saleAttrValueService.saveBatch(spuSaleAttrValueList);

        });

        spuSaleAttrService.saveBatch(spuSaleAttrList);

    }
}




