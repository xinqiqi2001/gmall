package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.item.SkuDetailFeignClient;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

/**
 * @Author Xiaoxin
 * @Date 2022/8/26 21:14
 * @Version 1.0
 */
@Controller
public class ItemController {

    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    /**
     * 商品详情页
     *
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId,
                       Model model) {

        //远程查询出商品的详细信息

        Result<SkuDetailTo> result = skuDetailFeignClient.getSkuDetail(skuId);

        if (result.isOk()) {
            SkuDetailTo skuDetailTo = result.getData();

            if (skuDetailTo!=null || skuDetailTo.getSkuInfo()!=null){
                model.addAttribute("categoryView", skuDetailTo.getCategoryView());
                model.addAttribute("skuInfo", skuDetailTo.getSkuInfo());
                BigDecimal price = skuDetailTo.getPrice();
                model.addAttribute("price",price);
                model.addAttribute("spuSaleAttrList",skuDetailTo.getSpuSaleAttrList());//spu的销售属性列表
                model.addAttribute("valuesSkuJson",skuDetailTo.getValuesSkuJson());//json
            }else {
                //说明远程没有查到商品
                return "item/404";
            }

        }


        return "item/index";
    }
}
