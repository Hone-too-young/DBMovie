package com.hdy.dbmovie;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hdy.dbmovie.dao")
public class DbMovieApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbMovieApplication.class, args);
    }

}
