package com.hdy.dbmovie.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hdy.dbmovie.common.bean.Result;
import com.hdy.dbmovie.common.bean.UserRegisterParam;
import com.hdy.dbmovie.pojo.User;
import com.hdy.dbmovie.service.UserService;
import com.hdy.dbmovie.utils.*;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 用户注册
 *
 * @author SJL
 */
@RestController
@RequestMapping("/db/un")
@AllArgsConstructor
public class RegisterController {
    private final UserService userService;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisUtil redisUtil;
    /**
     * 注册
     * */
    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterParam userRegisterParam, HttpServletResponse response) {
        //判断是否手机号
        String mobile = userRegisterParam.getMobile();
        if(PrincipalUtil.isMobile(mobile))
            return new Result(400,"手机号有误");
        int count = (int) userService.count(new LambdaQueryWrapper<User>().eq(User::getUserMobile, mobile));
        //判断是否重复注册
        if (count>0)
            return new Result(400,"该手机号已注册！");
        // 校验短信验证码
        String userCode = redisTemplate.opsForValue().get(mobile);
        if (StrUtil.isBlank(userCode)) {
            return new Result(400,"验证码已过期！");
        }
        if (!userRegisterParam.getCode().equals(userCode)) {
            return new Result(400,"验证码错误！");
        }
        // 添加用户
        User user = SetParam( userRegisterParam);
        userService.save(user);
        // 2. 登录
        Long currentTimeMillis = System.currentTimeMillis();
        String token= TokenUtil.sign(user.getNickName(),currentTimeMillis);
        redisUtil.set(user.getNickName(),currentTimeMillis,TokenUtil.REFRESH_EXPIRE_TIME);
        response.setHeader("Authorization", token);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        //清空验证码
        RedisUtil.del(mobile);
        return new Result(200,"注册成功!","NickName:"+user.getNickName());
    }

    /**
     * 修改密码
     * */
    @PutMapping("/updatePwd")
    public Result updatePwd(@RequestBody UserRegisterParam userPwdUpdateParam) {
        // 是否可以通过手机号获取用户信息
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUserMobile, userPwdUpdateParam.getMobile()));
        if (user == null) {
            // 无法获取用户信息
            return new Result(400,"无法获取用户信息！");
        }
        // 验证短信验证码是否一致，一致才可以修改用户密码
        String updateCode = RedisUtil.get(userPwdUpdateParam.getMobile()).toString();
        if (StrUtil.isBlank(userPwdUpdateParam.getCode()) || !StrUtil.equals(updateCode, userPwdUpdateParam.getCode())) {
            return new Result(400,"验证码错误！");
        }
        if (StrUtil.isBlank(userPwdUpdateParam.getPassWord())) {
            // 新密码不能为空
            return new Result(400,"密码不能为空！");
        }
        String encryptPwd = MD5Util.Encrypt(userPwdUpdateParam.getPassWord(), user.getUserRegisterTime().toString());
        if (StrUtil.equals(encryptPwd, user.getLoginPassword())) {
            // 新密码不能与原密码相同
            return new Result(400,"新密码不能与原密码相同!");
        }
        user.setLoginPassword(encryptPwd);
        userService.updateById(user);
        //清空验证码
        RedisUtil.del(userPwdUpdateParam.getMobile());
        return new Result(200,"修改成功！");
    }
    private User SetParam(UserRegisterParam userRegisterParam) {
        User user = new User();
        Date now = new Date();
        String name = RandomNameUtil.randomName(6);
        user.setNickName(name);
        user.setUserMobile(userRegisterParam.getMobile());
        String userId = IdUtil.simpleUUID();
        user.setUserId(userId);
        user.setUserRegisterTime(now);
        // 设置加密密码
        String encryptPwd = MD5Util.Encrypt(userRegisterParam.getPassWord(), user.getUserRegisterTime().toString());
        user.setLoginPassword(encryptPwd);
        return user;
    }


}