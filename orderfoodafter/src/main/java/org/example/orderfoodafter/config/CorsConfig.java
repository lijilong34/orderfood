package org.example.orderfoodafter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 * 配置跨域资源共享（CORS），允许前端应用跨域访问后端接口
 * 
 * @author 李吉隆
 * @date 2025-11-27
 */
@Configuration
public class CorsConfig {
/**
 * corsFilter方法
 *
 * @author 李吉隆
 */

    @Bean
    public CorsFilter corsFilter() {
            /**
     * CorsConfiguration
     * 
     * @author 李吉隆
     */
        CorsConfiguration config = new CorsConfiguration();
        // 允许的域名
        config.addAllowedOriginPattern("*");
        // 允许的请求头
        config.addAllowedHeader("*");
        // 允许的请求方法
        config.addAllowedMethod("*");
        // 是否允许携带凭证
        config.setAllowCredentials(true);

            /**
     * UrlBasedCorsConfigurationSource
     * 
     * @author 李吉隆
     */
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

            /**
     * CorsFilter
     * 
     * @author 李吉隆
     */
        return new CorsFilter(source);
    }
}
