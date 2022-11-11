package com.hdy.dbmovie.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hdy.dbmovie.common.serializer.EmojiJsonSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_user")
@Builder(toBuilder = true)
public class User implements Serializable {
    private static final long serialVersionUID = 2090714647038636896L;
    /**
     * ID
     */
    @TableId(type = IdType.INPUT)
    private String userId;
    /**
     * 用户昵称
     */
    @JsonSerialize(using =  EmojiJsonSerializer.class)
    private String nickName;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 用户邮箱
     */
    private String userMail;
    /**
     * 登录密码
     */
    private String loginPassword;
    /**
     * 手机号码
     */
    private String userMobile;
    /**
     * M(男) or F(女)
     */
    private String gender;

    /**
     * 生日
     */
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private String birthDate;
    /**
     * 例如：注册时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date userRegisterTime;
    /**
     * 头像图片路径
     */
    private String pic;

}
