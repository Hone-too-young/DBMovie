package com.hdy.dbmovie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hdy.dbmovie.common.bean.Result;
import com.hdy.dbmovie.pojo.User;
import com.hdy.dbmovie.service.UserService;
import com.hdy.dbmovie.utils.MD5Util;
import com.hdy.dbmovie.utils.RedisUtil;
import com.hdy.dbmovie.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ Program       :  com.hdy.dbmovie.controller.LoginController
 * @ Description   :
 * @ Author        :  Honetooyoung
 * @ CreateDate    :  2022-10-28 18:06:12
 */
@RestController
@RequestMapping("/db/un")
public class LoginController {
    @Autowired
    private  RedisUtil redisUtil;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        //获取用户信息
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getNickName, username));
        //解密校验密码
        if(user==null){
                return new Result(400,"用户不存在");
        }else if(!user.getLoginPassword().equals(MD5Util.Encrypt(password, user.getUserRegisterTime().toString()))){
                return new Result(400,"密码错误");
        }
        Long currentTimeMillis = System.currentTimeMillis();
        String token= TokenUtil.sign(username,currentTimeMillis);
        redisUtil.set(username,currentTimeMillis,TokenUtil.REFRESH_EXPIRE_TIME);
        response.setHeader("Authorization", token);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        return new Result(200,"success","Authorization:"+token);
    }
    @GetMapping("/logout")
    public  Result logout(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        redisUtil.del(TokenUtil.getAccount(httpServletRequest.getHeader("token")));
        httpServletResponse.setHeader("Authorization",null);
        httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
        return new Result(200,"退出登录！");
    }



}
