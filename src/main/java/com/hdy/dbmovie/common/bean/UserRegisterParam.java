package com.hdy.dbmovie.common.bean;

import lombok.Data;

@Data
public class UserRegisterParam {
    //密码
    private String passWord;
    //验证码
    private String code;
    //手机号
    private String mobile;
    //校验登陆注册验证码成功的标识
    private String checkRegisterSmsFlag;
}
