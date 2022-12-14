package com.hdy.dbmovie.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_sms_log")
public class SmsLog {
    /**
     * ID
     */
    private Long id;


    /**
     * 手机号码
     */

    private String userPhone;

    /**
     * 短信内容
     */
    private String content;

    /**
     * 手机验证码
     */

    private String mobileCode;

    /**
     * 短信类型  1:注册  2:验证
     */
    private Integer type;

    /**
     * 发送时间
     */

    private Date recDate;

    /**
     * 状态  1:有效  0：失效
     */
    private Integer status;

}