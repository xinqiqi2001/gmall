package com.atguigu.gmall.search.service;


import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;

/**
 * @Author Xiaoxin
 * @Date 2022/9/5 12:28
 * @Version 1.0
 */
public interface GoodsService {
    /**
     * 保存一个商品到es
     * @param goods
     */
    void saveGoods(Goods goods);

    /**
     * 从es中删除一个商品
     * @param skuId
     */
    void deleteGoods(Long skuId);

    /**
     * 商品检索功能
     * @param paramVo
     * @return
     */
    SearchResponseVo search(SearchParamVo paramVo);
    /**
     * 更新热度分
     * @param skuId
     * @param score 商品最新的得分
     * @return
     */
    void updateHotScore(Long skuId, Long score);
}
