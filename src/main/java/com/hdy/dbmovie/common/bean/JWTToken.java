package com.hdy.dbmovie.common.bean;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @ Program       :  com.hdy.dbmovie.common.bean.JWTToken
 * @ Description   :  配置token实体bean进行拓展，使其适应shiro框架
 * @ Author        :  Honetooyoung
 * @ CreateDate    :  2022-10-28 17:14:11
 */
public class JWTToken implements AuthenticationToken {
    private String token;

    public JWTToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}

