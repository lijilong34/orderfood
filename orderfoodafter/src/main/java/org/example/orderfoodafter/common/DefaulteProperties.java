package org.example.orderfoodafter.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
自动注入默认属性文件的自定义属性
 */
@Component
@ConfigurationProperties(prefix = "my")
public class DefaulteProperties {
    private String uploadfilepath;
    private int isflush;

public String getUploadfilepath() {
        return uploadfilepath;
    }

    public void setUploadfilepath(String uploadfilepath) {
        this.uploadfilepath = uploadfilepath;
    }

    public int getIsflush() {
        return isflush;
    }

    public void setIsflush(int isflush) {
        this.isflush = isflush;
    }
}
