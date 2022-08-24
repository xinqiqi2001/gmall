package com.atguigu.gmall.product.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



/**
 * @author Xiaoxin
 *
 * @Configuration
 * 声明配置类
 */
@Configuration
public class MybatisPlusConfig {


    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        //插件主题
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        //加入内部小插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        //页码超出之后(例如 共十页  查询十一页)  默认访问最后一页
        paginationInnerInterceptor.setOverflow(true);
        //分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }

}
