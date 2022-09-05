package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.model.list.SearchAttr;
import com.google.common.collect.Lists;
import com.atguigu.gmall.model.vo.search.OrderMapVo;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Xiaoxin
 * @Date 2022/9/5 12:29
 * @Version 1.0
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsRepository repository;

    @Autowired
    ElasticsearchRestTemplate esRestTemplate;

    /**
     * 保存一个商品到es
     *
     * @param goods
     */
    @Override
    public void saveGoods(Goods goods) {
        repository.save(goods);
    }

    /**
     * 从es中删除商品
     */
    @Override
    public void deleteGoods(Long skuId) {
        repository.deleteById(skuId);
    }


    /**
     * 商品检索功能
     *
     * @param paramVo
     * @return
     */
    @Override
    public SearchResponseVo search(SearchParamVo paramVo) {

        //1、动态构建出搜索条件
        Query query = buildQueryDsl(paramVo);

        //2、根据上面的搜索条件搜索并返回一个SearchHits
        SearchHits<Goods> goods = esRestTemplate.search(query,Goods.class,IndexCoordinates.of("goods"));

        //3、将搜索结果进行转换为SearchResponseVo
        SearchResponseVo responseVo = buildSearchResponseResult(goods, paramVo);

        return responseVo;
    }


    /**
     * 根据检索到的记录，构建响应结果
     *
     * @param goods
     * @return
     */
    private SearchResponseVo buildSearchResponseResult(SearchHits<Goods> goods, SearchParamVo paramVo) {

        SearchResponseVo vo = new SearchResponseVo();
        //1、当时检索前端传来的所有参数
        vo.setSearchParam(paramVo);
        //2、构建品牌面包屑 trademark=1:小米
        if (!StringUtils.isEmpty(paramVo.getTrademark())) {
            vo.setTrademarkParam("品牌：" + paramVo.getTrademark().split(":")[1]);
        }
        //3、平台属性面包屑
        if (paramVo.getProps() != null && paramVo.getProps().length > 0) {
            List<SearchAttr> propsParamList = new ArrayList<>();
            for (String prop : paramVo.getProps()) {
                //23:8G:运行内存 分割
                String[] split = prop.split(":");
                //一个SearchAttr 代表一个属性面包屑
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(Long.valueOf(split[0]));
                searchAttr.setAttrName(split[1]);
                searchAttr.setAttrValue(split[2]);
                //因为面包屑需要一个list集合 所以需要将它放入一个list集合
                propsParamList.add(searchAttr);
            }

            vo.setPropsParamList(propsParamList);
        }
        //未写
        //TODO 4、所有品牌列表 。需要ES聚合分析
        vo.setTrademarkList(Lists.newArrayList());
        //TODO 5、所有属性列表 。需要ES聚合分析
        vo.setAttrsList(Lists.newArrayList());

        //6、返回排序信息  order=1:desc
        if (!StringUtils.isEmpty(paramVo.getOrder())) {
            String order = paramVo.getOrder();
            OrderMapVo mapVo = new OrderMapVo();
            //根据什么类型排序
            mapVo.setType(order.split(":")[0]);
            //升序或降序
            mapVo.setSort(order.split(":")[1]);
            vo.setOrderMap(mapVo);
        }
        //7、所有搜索到的商品列表
        List<Goods> goodsList = new ArrayList<>();
        //获取命中的商品信息
        List<SearchHit<Goods>> hits = goods.getSearchHits();
        for (SearchHit<Goods> hit : hits) {
            //这条命中记录的商品
            //返回搜索的对象数据
            Goods content = hit.getContent();
            //如果是模糊检索,会有高亮标题
            if (!StringUtils.isEmpty(paramVo.getKeyword())) {
                String highlightTitle = hit.getHighlightField("title").get(0);
                //设置高亮标题
                content.setTitle(highlightTitle);
            }
            goodsList.add(content);
        }
        vo.setGoodsList(goodsList);

        //8、页码
        vo.setPageNo(paramVo.getPageNo());
        //9、总页码
        //总命中数
        long totalHits = goods.getTotalHits();
        //总条数取余自定义的每页8条 获得一共有几页
        long ps = totalHits%SysRedisConst.SEARCH_PAGE_SIZE == 0?totalHits/SysRedisConst.SEARCH_PAGE_SIZE:(totalHits/SysRedisConst.SEARCH_PAGE_SIZE+1);
        vo.setTotalPages(new Integer(ps+""));

        //10、老连接   /list.html?category2Id=13  用来对应之后指定的url添加数据
        String url = makeUrlParam(paramVo);
        vo.setUrlParam(url);


        return vo;
    }


    /**
     * 根据前端传递来的所有请求参数构建检索条件
     * DSL：
     * 1、查询条件【分类、关键字、品牌、属性】
     * 2、排序分页【排序、分页】
     * 3、高亮
     *
     * @param paramVo
     * @return
     */
    private Query buildQueryDsl(SearchParamVo paramVo) {
        //1.准备bool
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //2.给bool中准备符合must的各种条件
        //2.1前端传来了分类条件
        if(paramVo.getCategory1Id()!= null){
            //传来的不为空
            boolQuery.must(QueryBuilders.termQuery("category1Id",paramVo.getCategory1Id()));
        }
        if (paramVo.getCategory2Id() != null) {
            //传来的不为空
            boolQuery.must(QueryBuilders.termQuery("category2Id",paramVo.getCategory2Id()));
        }
        if (paramVo.getCategory3Id() != null) {
            //传来的不为空
            boolQuery.must(QueryBuilders.termQuery("category3Id",paramVo.getCategory3Id()));
        }

        //2.2前端传了 keyword。要进行全文检索
        if(!StringUtils.isEmpty(paramVo.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("title",paramVo.getKeyword()));
        }

        //2.3前端传了品牌 trademark=4:小米
        if(!StringUtils.isEmpty(paramVo.getTrademark())){
            long tmId = Long.parseLong(paramVo.getTrademark().split(":")[0]);
            boolQuery.must(QueryBuilders.termQuery("tmId",tmId));
        }


        //2.4前端传来了属性 可能会传多个属性 (筛选属性 props=4:128GB:机身存储&props=5:骁龙730:cpu型号...)
        String[] props = paramVo.getProps();
        if(props!=null && props.length > 0){
            //传来的属性不为空且可能是多个
            for (String prop : props) {
                //获取传来的可能的多个属性
                //构造嵌入式
                //用:分割  可能是4:128:机身存储 这种类型
                String[] split = prop.split(":");
                long attrId = Long.parseLong(split[0]);
                String attrValue = split[1];
                //获得一个嵌入式布尔
                BoolQueryBuilder nestedBool = QueryBuilders.boolQuery();
                nestedBool.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedBool.must(QueryBuilders.termQuery("attrs.attrValue",attrValue));


                //ScoreMode.None不评分
                //给最大的boolQuery的嵌入式Query里面放嵌入式查询nestQuery
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", nestedBool, ScoreMode.None);
                boolQuery.must(nestedQueryBuilder);
            }

        }
        //0、准备一个原生检索条件【原生的dsl】
        NativeSearchQuery query = new NativeSearchQuery(boolQuery);
        //2.5前端传来了排序条件 order=2:asc
        if(!StringUtils.isEmpty(paramVo.getOrder())){
            //切分
            String[] split = paramVo.getOrder().split(":");
            //是1代表用热度分排序  否则就是价格排序
            //分析排序用哪个字段
            String orderField = null;
            switch (split[0]) {
                case "1":
                    //根据热度分排序
                    orderField = "hotScore";
                    break;
                case "2":
                    //根据价格排序
                    orderField = "price";
                    break;
                case "3":
                    //根据创建时间排序
                    orderField = "createTime";
                    break;
                default:
                    //默认热度分排序
                    orderField = "hotScore";
            }
            Sort sort = Sort.by(orderField);
            //判断是升序还是降序

            if(split[1].equals("asc")) {
                sort = sort.ascending();
            }else {
                sort = sort.descending();
            }
            query.addSort(sort);
        }
        //2.6前端传了页码
        //页码在Spring底层是从0开始，自己要计算 前端页码传的是1所以要-1 后的结果
        PageRequest request = PageRequest.of(paramVo.getPageNo() - 1, SysRedisConst.SEARCH_PAGE_SIZE);
        query.setPageable(request);
        //=============排序分页结束=====================


        //2.7高亮
        if (!StringUtils.isEmpty(paramVo.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            //给title加高亮显示
            highlightBuilder.field("title").preTags("<span style='color:red'>").postTags("</span>");

            HighlightQuery highlightQuery = new HighlightQuery(highlightBuilder);
            query.setHighlightQuery(highlightQuery);
        }


        return query;
    }
    /**
     * 制造老连接
     * @param paramVo
     * @return
     */
    private String makeUrlParam(SearchParamVo paramVo) {
        // list.html?&k=v
        StringBuilder builder = new StringBuilder("list.html?");
        //1、拼三级分类所有参数
        if(paramVo.getCategory1Id()!=null){
            builder.append("&category1Id="+paramVo.getCategory1Id());
        }
        if(paramVo.getCategory2Id()!=null){
            builder.append("&category2Id="+paramVo.getCategory2Id());
        }
        if(paramVo.getCategory3Id()!=null){
            builder.append("&category3Id="+paramVo.getCategory3Id());
        }

        //2、拼关键字
        if(!StringUtils.isEmpty(paramVo.getKeyword())){
            builder.append("&keyword="+paramVo.getKeyword());
        }

        //3、拼品牌
        if(!StringUtils.isEmpty(paramVo.getTrademark())){
            builder.append("&trademark="+paramVo.getTrademark());
        }

        //4、拼属性
        if(paramVo.getProps()!=null && paramVo.getProps().length >0){
            for (String prop : paramVo.getProps()) {
                //props=23:8G:运行内存
                builder.append("&props="+prop);
            }
        }

//        //5、拼排序
//        builder.append("&order="+paramVo.getOrder());
//
//        //6、拼页码
//        builder.append("&pageNo="+paramVo.getPageNo());

        //拿到最终字符串
        String url = builder.toString();
        return url;
    }
}
