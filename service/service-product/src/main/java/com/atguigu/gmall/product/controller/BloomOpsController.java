package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 重建布隆过滤器
 * @Author Xiaoxin
 * @Date 2022/9/1 18:41
 * @Version 1.0
 */
@RequestMapping("/admin/product")
@RestController
public class BloomOpsController {

    @Autowired
    BloomOpsService bloomOpsService;
    @Autowired
    BloomDataQueryService bloomDataQueryService;

    /**
     * 重建布隆过滤器
     * 因为布隆过滤器不能删除  所以需要一段时间之后重新构建
     * @return
     */
    @GetMapping("/rebuild/now")
    public Result rebuildBloom(){
        String bloomName = SysRedisConst.BLOOM_SKUID;
        //指定需要重建的布隆的名字
        bloomOpsService.rebuildBloom(bloomName,bloomDataQueryService);


        return Result.ok();
    }

}
