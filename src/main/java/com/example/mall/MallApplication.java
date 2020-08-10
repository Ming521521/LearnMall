package com.example.mall;

import com.example.mall.dao.CategoryMapper;
import com.example.mall.dao.OrderMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.example.mall.dao")
public class MallApplication{

    public static void main(String[] args) {
        SpringApplication.run(MallApplication.class, args);
    }

}
