package com.atguigu.gmall.product.bloom;

/**
 * @Author Xiaoxin
 * @Date 2022/9/1 18:44
 * @Version 1.0
 */

public interface BloomOpsService {

    /**
     *  重建布隆过滤器
     * @param bloomName 需要重建的布隆的名字
     * @param dataQueryService  抽取了需要在布隆里存储数据的方法
     */
    void rebuildBloom(String bloomName,BloomDataQueryService dataQueryService);
}
