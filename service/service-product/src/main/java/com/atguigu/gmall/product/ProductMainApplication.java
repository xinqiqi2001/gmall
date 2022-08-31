package com.atguigu.gmall.product;



import com.atguigu.gmall.common.annotation.EnableThreadPool;
import com.atguigu.gmall.common.config.RedissonAutoConfiguration;
import com.atguigu.gmall.common.config.Swagger2Config;
import lombok.Data;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Import;


/**
 * @author Xiaoxin
 */

@Import({Swagger2Config.class, RedissonAutoConfiguration.class}) //扫描到Swagger的配置类
@MapperScan("com.atguigu.gmall.product.mapper") //自动扫描这个包下的所有Mapper接口
@SpringCloudApplication
@EnableThreadPool //
public class ProductMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class,args);
    }
}
