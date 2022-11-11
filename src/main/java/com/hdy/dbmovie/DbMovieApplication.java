package com.hdy.dbmovie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.hdy.dbmovie.dao")
@ComponentScan(basePackages = {"org.springframework.boot.autoconfigure.jackson","com.hdy.dbmovie"})
public class DbMovieApplication {
    public static void main(String[] args) {
        SpringApplication.run(DbMovieApplication.class, args);
    }
}
