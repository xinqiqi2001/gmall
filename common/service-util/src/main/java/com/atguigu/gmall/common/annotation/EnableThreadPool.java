package com.atguigu.gmall.common.annotation;


import com.atguigu.gmall.common.config.threadpool.AppThreadPoolAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(AppThreadPoolAutoConfiguration.class)
/**
 * 当启动类上加上@EnableThreadPool时 会自动导入@Import(AppThreadPoolAutoConfiguration.class)
 * 这样就能扫描到线程池的配置类了
 */
public @interface EnableThreadPool {
}
