package org.example.orderfoodafter.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // 本地图片存储根路径（根据你的实际路径修改）
    private static final String LOCAL_IMAGE_PATH = "file:C:\\Users\\lijil\\Pictures\\imgdata\\";

    // URL映射路径（对应前端访问的 /imeageserver/ 路径）
    private static final String URL_MAPPING = "/imeageserver/**";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加资源映射规则：访问 /imeageserver/xxx.jpg 会映射到 D:/images/xxx.jpg
        registry.addResourceHandler(URL_MAPPING)
                .addResourceLocations(LOCAL_IMAGE_PATH);
    }
}
