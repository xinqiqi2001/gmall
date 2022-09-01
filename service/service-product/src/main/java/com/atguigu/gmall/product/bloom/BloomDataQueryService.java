package com.atguigu.gmall.product.bloom;

import java.util.List;

/**
 * 布隆过滤查询
 * @Author Xiaoxin
 * @Date 2022/9/1 19:29
 * @Version 1.0
 */
public interface BloomDataQueryService {

    //抽取出查数据的模板模式

    /**
     * 父类定义模板 子类实现操作
     * @return
     */
    List queryData();

}
