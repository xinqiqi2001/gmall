package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Xiaoxin
* @description 针对表【sku_image(库存单元图片表)】的数据库操作Service实现
* @createDate 2022-08-23 20:48:38
*/
@Service
public class SkuImageServiceImpl extends ServiceImpl<SkuImageMapper, SkuImage>
    implements SkuImageService{

    @Autowired
    SkuImageMapper skuImageMapper;

    @Override
    public List<SkuImage> getSkuImage(Long skuId) {

        //根据skuId查询商品（sku）的照片
        List<SkuImage> imageList = skuImageMapper.selectList(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId,skuId));

        return imageList;
    }
}




