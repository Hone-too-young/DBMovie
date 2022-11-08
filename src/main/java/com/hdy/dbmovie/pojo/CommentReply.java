package com.hdy.dbmovie.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hdy.dbmovie.common.serializer.EmojiJsonSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 讨论区表(CommentReply)表实体类
 *
 * @author Honetooyoung
 * @since 2022-11-05 10:27:15
 */
@Data
@TableName("db_comment_reply")
public class CommentReply implements Serializable {
    private static final long serialVersionUID = 2848480038636896L;
    //评论主键id
    @TableId(type = IdType.AUTO)
    private Integer rid;
    //短评id
    private Integer cid;
    //父级评论mid
    private Integer pid;
    //评论者id
    private String fromId;
    //评论者昵称
    @JsonSerialize(using =  EmojiJsonSerializer.class)
    private String fromNickname;
    //头像
    private String fromPic;
    //点赞的数量
    private Integer likeNo;
    //踩的数量
    private Integer dislikeNo;
    //评论内容
    @JsonSerialize(using =  EmojiJsonSerializer.class)
    private String content;
    //创建时间
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date time;
    //子评论
    @TableField(exist = false)
    private List<CommentReply> commentReplyList;

}

