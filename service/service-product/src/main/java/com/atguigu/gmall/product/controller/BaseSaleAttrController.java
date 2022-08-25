package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/8/25 14:08
 * @Version 1.0
 */

@Api(tags = "销售属性")
@RestController
@RequestMapping("/admin/product/")
public class BaseSaleAttrController {

    @Autowired
    BaseSaleAttrService baseSaleAttrService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;


    /**
     * 获取所有销售属性的值
     * @return
     */
    @ApiOperation("获取所有销售属性的值")
    @GetMapping("baseSaleAttrList")
    public Result baseSaleAttrList(){

        List<BaseSaleAttr> list = baseSaleAttrService.list();

        return Result.ok(list);
    }

    /**
     * 根据spuId获取销售属性
     * @param spuId
     * @return
     */
    @ApiOperation("根据spuId获取销售属性")
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable("spuId") Long spuId){

        List<SpuSaleAttr> spuSaleAttr=spuSaleAttrService.saleAttrAndValue(spuId);
        return Result.ok(spuSaleAttr);
    }

}
