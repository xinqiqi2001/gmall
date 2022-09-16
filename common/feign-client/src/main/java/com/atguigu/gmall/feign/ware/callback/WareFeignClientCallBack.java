package com.atguigu.gmall.feign.ware.callback;

import com.atguigu.gmall.feign.ware.WareFeignClient;
import org.springframework.stereotype.Service;

/**
 * @Author Xiaoxin
 * @Date 2022/9/16 11:50
 * @Version 1.0
 */
@Service
public class WareFeignClientCallBack implements WareFeignClient {

    /**
     * 错误熔断
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public String hasStock(Long skuId, Integer num) {

        System.out.println("熔断降级方法启动{默认返回有货}");
        //返回有货
        return "1";
    }
}
