package com.moxin.moxincreateuser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.moxin.moxincreateuser.mapper")
public class MoxinCreateUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoxinCreateUserApplication.class, args);
    }

}
