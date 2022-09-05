package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

/**
 * @Author Xiaoxin
 * @Date 2022/9/5 13:41
 * @Version 1.0
 */
@Controller
public class SearchController {

    @Resource
    SearchFeignClient searchFeignClient;

    /**
     * 检索列表
     * 1.按照条件 category1Id，category2Id，category3Id
     * 2.按照关键字 keyword
     * 3.按照属性 props
     *      106:安卓手机:手机一级 可能有多个属性
     * 4.按照品牌 trademark=1:小米
     * 5.按照分页页码 pageNo=1
     * 6.按照排序  order=1:desc
     *      1.代表按照综合排序
     *      2.代表按照价格排序
     * @return
     */
    @GetMapping("/list.html")
    public String search(SearchParamVo searchVo, Model model){

        Result<SearchResponseVo> search =searchFeignClient.search(searchVo);

        SearchResponseVo data = search.getData();

        //将检索出来的数据返回到页面
        //1.将检索页面传来的所有条件 原封不动的返回页面
        model.addAttribute("searchParam",data.getSearchParam());
        //2.品牌面包屑
        model.addAttribute("trademarkParam",data.getTrademarkParam());
        //3.属性面包屑  (是集合 集合里面每一个元素是一个对象 这个对象里必须有attrName，attrValue，attrId数据)
        model.addAttribute("propsParamList",data.getPropsParamList());
        //4.所有品牌 (是集合 集合里面每一个元素是一个对象 这个对象里必须有tmId、tmLogoUrl、tmName)
        model.addAttribute("trademarkList",data.getTrademarkList());
        //5、所有属性，是集合。集合里面每个元素是一个对象，拥有这些数据（attrId，attrName，List<String> attrValueList， ）
        model.addAttribute("attrsList",data.getAttrsList());
        //6、排序信息。是对象。 拥有这些数据（type，sort）
        model.addAttribute("orderMap",data.getOrderMap());
        //7、所有商品列表。是集合。集合里面每个元素是一个对象,拥有这些数据(es中每个商品的详细数据)
        model.addAttribute("goodsList",data.getGoodsList());
        //8、分页信息
        model.addAttribute("pageNo",data.getPageNo());
        model.addAttribute("totalPages",data.getTotalPages());
        //9、url信息
        model.addAttribute("urlParam",data.getUrlParam());
        return "list/index";
    }
}
