package org.example.orderfoodafter.common;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 默认应用属性配置类
 * 配置MinIO文件存储服务的连接参数和认证信息
 * 
 * @author 李吉隆
 * @date 2025-11-17
 */
@Component
@ConfigurationProperties()
public class DefaultApplicationProperties {
/**
 * getServer方法
 *
 * @author 李吉隆
 */
    public String getServer() {
        return server;
    }
/**
 * setServer方法
 *
 * @author 李吉隆
 */

    public void setServer(String server) {
        this.server = server;
    }
/**
 * getPort方法
 *
 * @author 李吉隆
 */

    public int getPort() {
        return port;
    }
/**
 * setPort方法
 *
 * @author 李吉隆
 */

    public void setPort(int port) {
        this.port = port;
    }
/**
 * getEndpoint方法
 *
 * @author 李吉隆
 */

    public String getEndpoint() {
        return endpoint;
    }
/**
 * setEndpoint方法
 *
 * @author 李吉隆
 */

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
/**
 * getAccessKey方法
 *
 * @author 李吉隆
 */

    public String getAccessKey() {
        return accessKey;
    }
/**
 * setAccessKey方法
 *
 * @author 李吉隆
 */

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
/**
 * getSecretKey方法
 *
 * @author 李吉隆
 */

    public String getSecretKey() {
        return secretKey;
    }
/**
 * setSecretKey方法
 *
 * @author 李吉隆
 */

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
/**
 * getBucket方法
 *
 * @author 李吉隆
 */

    public String getBucket() {
        return bucket;
    }
/**
 * setBucket方法
 *
 * @author 李吉隆
 */

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @Value("${minio.server}")
    private String server;
    @Value("${minio.port}")
    private int port;
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;
    @Value("${minio.bucket}")
    private String bucket;

    /**
     * 创建minio连接对象
     *
     * @return
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(server, port, false)
                .credentials(accessKey, secretKey)
                .build();
    }
}
