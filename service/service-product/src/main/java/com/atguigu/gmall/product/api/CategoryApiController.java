package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 远程调用内部接口  命名规范：/api/inner/rpc/模块名
 * @Author Xiaoxin
 * @Date 2022/8/26 18:53
 * @Version 1.0
 */
@Api("三级分类的RPC接口")
@RestController()
@RequestMapping("/api/inner/rpc/product")
public class CategoryApiController {

    @Autowired
    BaseCategory2Service baseCategory2Service;
    /**
     * 查询所有的分类 并封装成树形菜单结构
     * @return
     */
    @ApiOperation("三级分类树结构查询")
    @GetMapping("/category/tree")
    public Result getAllCategoryWithTree(){

        //查询全部分类 以及分类里的子分类
        List<CategoryTreeTo> categoryTreeTos =baseCategory2Service.getAllCategoryWithTree();

        return Result.ok(categoryTreeTos);
    }

}
