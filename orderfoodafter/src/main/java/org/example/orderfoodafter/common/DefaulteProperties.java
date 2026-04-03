package org.example.orderfoodafter.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 默认属性配置类
 * 自动注入配置文件中的自定义属性，包括文件上传路径和缓存刷新设置
 * 
 * @author 李吉隆
 * @date 2025-11-18
 */
@Component
@ConfigurationProperties(prefix = "my")
public class DefaulteProperties {
    private String uploadfilepath;
    private int isflush;
/**
 * getUploadfilepath方法
 *
 * @author 李吉隆
 */

public String getUploadfilepath() {
        return uploadfilepath;
    }
/**
 * setUploadfilepath方法
 *
 * @author 李吉隆
 */

    public void setUploadfilepath(String uploadfilepath) {
        this.uploadfilepath = uploadfilepath;
    }
/**
 * getIsflush方法
 *
 * @author 李吉隆
 */

    public int getIsflush() {
        return isflush;
    }
/**
 * setIsflush方法
 *
 * @author 李吉隆
 */

    public void setIsflush(int isflush) {
        this.isflush = isflush;
    }
}
