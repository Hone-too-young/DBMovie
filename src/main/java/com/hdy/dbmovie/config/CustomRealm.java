package com.hdy.dbmovie.config;

import com.hdy.dbmovie.common.bean.JWTToken;
import com.hdy.dbmovie.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @ Program       :  com.hdy.dbmovie.config.CustomRealm
 * @ Description   :  自定义Realm，实现Shiro认证
 * @ Author        :  Honetooyoung
 * @ CreateDate    :  2022-10-29 15:48:11
 */

@Component
@Slf4j
@Lazy
public class CustomRealm extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

/**
     * 用户授权
     * @param principalCollection
     * @return
     */

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("用户授权");
        String username= TokenUtil.getAccount(principalCollection.toString());
        SimpleAuthorizationInfo info= new SimpleAuthorizationInfo();
        //正确的业务流程是到数据库拿该用户的权限再去进行授权的，这里只是简单的直接授权
        Set<String> role=new HashSet<>();
        if ("admin".equals(username)){
            role.add("admin");
        }else {
            role.add("user");
        }
        info.setRoles(role);
        return info;
    }

/**
     * 用户身份认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("身份认证");
        String token= (String) authenticationToken.getCredentials();
        String username= TokenUtil.getAccount(token);
        log.info(username);
        //这里要去数据库查找是否存在该用户，这里直接放行
        if (username==null){
            throw new AuthenticationException("认证失败！");
        }
        return new SimpleAuthenticationInfo(token,token,"MyRealm");
    }
}
