package com.hdy.dbmovie.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hdy.dbmovie.common.serializer.EmojiJsonSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 短评表(Comment)表实体类
 *
 * @author Honetooyoung
 * @since 2022-11-05 10:27:14
 */
@TableName("db_comment")
@Data
public class Comment implements Serializable {
    private static final long serialVersionUID = 45667757038636896L;
    //ID
    @TableId(type = IdType.AUTO)
    private String cid;
    //电影id
    private String filmId;
    //用户id
    private String userId;
    //用户昵称
    @JsonSerialize(using =  EmojiJsonSerializer.class)
    private String userNickname;
    //头像
    private String userPic;
    //想看，看过，在看
    private String status;
    //0 仅自己可看 1 公开
    private Integer isPublic;
    //点赞数
    private Integer likeNo;
    //踩数
    private Integer dislikeNo;
    //星数
    private Integer stars;
    //标签
    private String tag;
    //评论内容
    @JsonSerialize(using =  EmojiJsonSerializer.class)
    private String content;
    //创建时间
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date time;

}

