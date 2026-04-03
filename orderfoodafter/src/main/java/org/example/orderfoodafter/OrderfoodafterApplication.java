package org.example.orderfoodafter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot应用启动类
 * 点餐系统后端服务的主入口类
 * 
 * @author 李吉隆
 * @date 2025-11-15
 */
@SpringBootApplication
@MapperScan("org.example.orderfoodafter.mapper")
public class OrderfoodafterApplication {
/**
 * main方法
 *
 * @author 李吉隆
 */

    public static void main(String[] args) {
        SpringApplication.run(OrderfoodafterApplication.class, args);
    }

}
