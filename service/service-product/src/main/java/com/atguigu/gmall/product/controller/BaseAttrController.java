package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Xiaoxin
 *
 */
@RestController
@RequestMapping("/admin/product/")
public class BaseAttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    @Autowired
    BaseAttrValueService baseAttrValueService;

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    BaseSaleAttrService baseSaleAttrService;


    /**
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return//查询某个平台下的所有平台属性
     */
    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result getAttrInfoList(@PathVariable("category1Id") Long category1Id,
                                  @PathVariable("category2Id") Long category2Id,
                                  @PathVariable("category3Id") Long category3Id) {

        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoService.getAttrInfoAndValueByCategoryId(category1Id, category2Id, category3Id);
        return Result.ok(baseAttrInfos);
    }

    /**
     * 保存属性信息
     * @return
     */
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {

        baseAttrInfoService.saveAttrInfo(baseAttrInfo);

        return Result.ok();
    }

    /** getAttrValueList/11
     * 根据平台属性id获取属性所有信息
     * 根据属性id获取该属性的所有属性值
     */
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId){

        List<BaseAttrValue> values = baseAttrValueService.getAttrValueList(attrId);

        return Result.ok(values);
    }

    //------------------------------------8.25
    /**
     * 获取spu分页列表
     * @param pn
     * @param limit
     * @param category3Id
     * @return
     */
    @GetMapping("{pn}/{limit}")
    public Result page(@PathVariable Long pn, @PathVariable Long limit, @Param("category3Id")Long category3Id ){

        Page<SpuInfo> pages=new Page<>(pn,limit);

        //分页查询 (分页信息 查询到的记录的集合)
        Page<SpuInfo> resultPage = spuInfoService.page(pages, new LambdaQueryWrapper<SpuInfo>()
                .eq(SpuInfo::getCategory3Id,category3Id));

        return Result.ok(resultPage);


    }

    /**
     * 获取销售属性
     * @return
     */
    @GetMapping("baseSaleAttrList")
    public Result baseSaleAttrList(){

        List<BaseSaleAttr> list = baseSaleAttrService.list();

        return Result.ok(list);
    }





}
