package com.ypj.train.business;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.ypj.train.business.mapper")
public class BusinessApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusinessApplication.class, args);
    }
}
