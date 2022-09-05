package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author Xiaoxin
 * @Date 2022/9/4 21:56
 * @Version 1.0
 */
//创建goods的映射
@Repository
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
