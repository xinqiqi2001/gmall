package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.web.feign.CategoryFeignClient;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/8/26 11:31
 * @Version 1.0
 */
@Controller
@RequestMapping
public class IndexController {
    @Autowired
    CategoryFeignClient categoryFeignClient;

    @RequestMapping({"/","/index"})
    public String indexPage(Model model){

        //远程调用"service-product"服务 的查询三级分类的方法
        Result<List<CategoryTreeTo>> allCategoryWithTree = categoryFeignClient.getAllCategoryWithTree();

        if (allCategoryWithTree.isOk()) {
            //远程调用成功
            //TODO 查询出所有菜单
            List<CategoryTreeTo> data = allCategoryWithTree.getData();
            model.addAttribute("list",data);
        }

        return "index/index";
    }

}
