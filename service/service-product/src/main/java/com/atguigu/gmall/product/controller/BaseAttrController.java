package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Xiaoxin
 */
@RestController
@RequestMapping("/admin/product/")
public class BaseAttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    @Autowired
    BaseAttrValueService baseAttrValueService;


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

}
