package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Xiaoxin
 */
@Api(tags = "Spu")
@RestController
@RequestMapping("/admin/product/")
public class SpuController {

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    SpuImageService spuImageService;




    /**
     * 获取spu分页列表
     * @param pn
     * @param limit
     * @param category3Id
     * @return
     */
    @ApiOperation("获取spu分页列表")
    @GetMapping("{pn}/{limit}")
    public Result page(@PathVariable("pn") Long pn, @PathVariable Long limit, @RequestParam("category3Id")Long category3Id ){

        Page<SpuInfo> pages=new Page<>(pn,limit);

        //分页查询 (分页信息 查询到的记录的集合)
        Page<SpuInfo> resultPage = spuInfoService.page(pages, new LambdaQueryWrapper<SpuInfo>()
                .eq(SpuInfo::getCategory3Id,category3Id));

        return Result.ok(resultPage);


    }

    /**
     * 保存Spu
     * @param spuInfo
     * @return
     */

    @ApiOperation("保存Spu")
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){

        spuInfoService.saveSpuInfo(spuInfo);

        return Result.ok();
    }



    /**
     *根据spuId获取图片列表
     */
    @ApiOperation("根据spuId获取图片列表")
    @GetMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable("spuId") Long spuId){

        List<SpuImage> list = spuImageService.list(new LambdaQueryWrapper<SpuImage>().eq(SpuImage::getSpuId, spuId));
        return Result.ok(list);
    }



}
