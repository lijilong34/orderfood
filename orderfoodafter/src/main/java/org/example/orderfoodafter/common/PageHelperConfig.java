package org.example.orderfoodafter.common;

import com.github.pagehelper.PageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 分页配置类
 * 配置PageHelper分页插件，支持数据库分页查询功能
 * 
 * @author 李吉隆
 * @date 2025-11-21
 */
@Configuration
public class PageHelperConfig {
/**
 * pageHelper方法
 *
 * @author 李吉隆
 */
    @Bean
    public PageInterceptor pageHelper() {
            /**
     * PageInterceptor
     * 
     * @author 李吉隆
     */
        PageInterceptor pageInterceptor = new PageInterceptor();
            /**
     * Properties
     * 
     * @author 李吉隆
     */
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum","true");
        properties.setProperty("rowBoundsWithCount","true");
        properties.setProperty("reasonable","true");
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("supportMethodsArguments","true");
        properties.setProperty("params","count=countSql");
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }
}
