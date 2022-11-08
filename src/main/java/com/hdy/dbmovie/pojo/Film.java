package com.hdy.dbmovie.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 电影表(Film)表实体类
 *
 * @author Honetooyoung
 * @since 2022-10-31 16:53:59
 */
@Data
@TableName("db_film")
public class Film implements Serializable {
    private static final long serialVersionUID = 37448838038636896L;
    //ID
    @TableId(type = IdType.AUTO)
    private String mid;
    //标签
    private String tags;
    //上映日期
    private Date releaseDate;
    //星级
    private Integer stars;
    //详情
    private String detail;
    //电影名字
    private String filmName;
    //得分
    private String score;
    //故事梗概
    private String plot;
    //图片地址
    private String avatar;
    //主演id
    private String celebrities;

}

