package com.atguigu.gmall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @Author Xiaoxin
 * @Date 2022/9/3 22:08
 * @Version 1.0
 */
@EnableElasticsearchRepositories //开启es自动仓库 写Bean 写接口 自动创好索引并设置好Mapping类型
@SpringCloudApplication
public class SearchMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchMainApplication.class,args);
    }
}
