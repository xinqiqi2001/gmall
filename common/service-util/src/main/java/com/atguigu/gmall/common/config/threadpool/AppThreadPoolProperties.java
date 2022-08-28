package com.atguigu.gmall.common.config.threadpool;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Xiaoxin
 */
@ConfigurationProperties(prefix = "app.thread-pool")
@Data
public class AppThreadPoolProperties {

    Integer core = 2;
    Integer max = 4;
    Integer queueSize = 200;
    Long keepAliveTime = 300L;


}
