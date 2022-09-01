package com.atguigu.gmall.product.schedule;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 重建布隆任务
 * @Author Xiaoxin
 * @Date 2022/9/1 19:16
 * @Version 1.0
 */
@Service
public class RebuildBloomTask {
    @Autowired
    BloomOpsService bloomOpsService;

    @Autowired
    BloomDataQueryService bloomDataQueryService;

    //定时重建布隆 每个月的周三
    @Scheduled(cron = "0 0 3 ? * 3")
    public void rebuild(){
        bloomOpsService.rebuildBloom(SysRedisConst.BLOOM_SKUID,bloomDataQueryService);
//        System.out.println("定时任务生效");
    }

}
