package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.xml.internal.bind.v2.TODO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * date 2022/8/25
 * @since 1.8
 * @author
*/


@Api(tags = "品牌")
@RestController
@RequestMapping("admin/product/")
public class BaseTrademarkController {
    @Autowired
    BaseTrademarkService baseTrademarkService;

//    admin/product/baseTrademark/1/10

    /**
     * 分页查询所有品牌
     * @param pn 页码  第几页
     * @param limit  每页有几条
     * @return
     */
    @ApiOperation("分页查询所有品牌")
    @GetMapping("baseTrademark/{pn}/{limit}")
    public Result baseTrademark(@PathVariable("pn") Long pn, @PathVariable("limit") Long limit){

        Page<BaseTrademark> page=new Page<>(pn,limit);

        //分页查询 (分页信息 查询到的记录的集合)
        Page<BaseTrademark> resultPage = baseTrademarkService.page(page);

        return Result.ok(resultPage);
    }

    /**
     * 根据Id获取品牌
     * @param id
     * @return
     */
    @ApiOperation("根据Id获取品牌")
    @GetMapping("baseTrademark/get/{id}")
    public Result getByIdTrademark(@PathVariable("id") Integer id){

        BaseTrademark trademark = baseTrademarkService.getById(id);
        return Result.ok(trademark);
    }

    /**
     * 修改品牌
     * @param baseTrademark
     * @return
     */
    @ApiOperation("修改品牌")
    @PutMapping("baseTrademark/update")
    public Result getByIdTrademark(@RequestBody BaseTrademark baseTrademark){

        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    /**
     * 保存品牌
     * @param baseTrademark
     * @return
     */
    @ApiOperation("保存品牌")
    @PostMapping("baseTrademark/save")
    public Result save(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    /**
     * 根据id删除品牌
     * @param id
     * @return
     */
    @ApiOperation("根据id删除品牌")
    @DeleteMapping("baseTrademark/remove/{id}")
    public Result delete(@PathVariable("id") Integer id){

        baseTrademarkService.removeById(id);
        return Result.ok();
    }


    //------------------------------------8.25
    /**
     * 获取品牌属性
     * @return
     */
    @GetMapping("baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> list = baseTrademarkService.list();
        return Result.ok(list);
    }

}
