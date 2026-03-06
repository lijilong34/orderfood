package org.example.orderfoodafter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.orderfoodafter.mapper")
public class OrderfoodafterApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderfoodafterApplication.class, args);
    }

}
