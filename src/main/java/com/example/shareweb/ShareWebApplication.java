package com.example.shareweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.shareweb.mapper.**")
public class ShareWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShareWebApplication.class, args);
    }

}
