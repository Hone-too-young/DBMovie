package com.hdy.dbmovie.pojo;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 演员表(Celebrities)表实体类
 *
 * @author Honetooyoung
 * @since 2022-11-02 21:26:23
 */
@Data
@TableName("db_celebrities")
public class Celebrities implements Serializable {
    private static final long serialVersionUID = 3744838038636896L;
    //ID
    private Long id;
    //名字
    private String name;
    //英文名字
    private String englishName;
    //性别
    private String gender;
    //星座
    private String sign;
    //生日
    private String birth;
    //家乡
    private String hometown;
    //职业
    private String job;

    private String imdb;
    //简介
    private String brief;
    //图片地址
    private String avatar;

}

