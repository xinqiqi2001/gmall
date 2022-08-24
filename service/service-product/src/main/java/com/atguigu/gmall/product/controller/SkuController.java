package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;



@RestController
@RequestMapping("admin/product/")
public class SkuController {
    @Autowired
    SpuImageService spuImageService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    SkuInfoService skuInfoService;


    /**
     *根据spuId获取图片列表
     */
    @GetMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") Long spuId){

        List<SpuImage> list = spuImageService.list(new LambdaQueryWrapper<SpuImage>().eq(SpuImage::getSpuId, spuId));
        return Result.ok(list);
    }

    /**
     * 根据spuId获取销售属性
     * @param spuId
     * @return
     */
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId") Long spuId){

        List<SpuSaleAttr> spuSaleAttr=spuSaleAttrService.saleAttrAndValue(spuId);
        return Result.ok(spuSaleAttr);
    }
    /**
     * 获取sku分页列表
     */
    @GetMapping("list/{page}/{limit}")
    public Result list(@PathVariable Long page, @PathVariable Long limit){
        Page<SkuInfo> pages=new Page<>(page,limit);

        //分页查询 (分页信息 查询到的记录的集合)
        Page<SkuInfo> resultPage = skuInfoService.page(pages);

        return Result.ok(resultPage);
    }

    /**
     * 1为上架 0为下架
     * 上架
     * @param skuId
     * @return
     */
    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){
        //通过skuId获取SkuInfo 将SkuInfo的IsSale属性设置为1
        SkuInfo skuInfo = skuInfoService.getOne(new LambdaQueryWrapper<SkuInfo>().eq(SkuInfo::getId, skuId));
        skuInfo.setIsSale(1);
        skuInfoService.updateById(skuInfo);
        return Result.ok();
    }

    /**
     * 1为上架 0为下架
     * 下架
     * @param skuId
     * @return
     */
    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId){
        //通过skuId获取SkuInfo 将SkuInfo的IsSale属性设置为0
        SkuInfo skuInfo = skuInfoService.getOne(new LambdaQueryWrapper<SkuInfo>().eq(SkuInfo::getId, skuId));
        skuInfo.setIsSale(0);
        skuInfoService.updateById(skuInfo);
        return Result.ok();
    }
}
