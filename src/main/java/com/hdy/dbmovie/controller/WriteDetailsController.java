package com.hdy.dbmovie.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hdy.dbmovie.common.bean.Result;
import com.hdy.dbmovie.pojo.User;
import com.hdy.dbmovie.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/db/user")
public class WriteDetailsController {
    @Resource
    private UserService userService;

    @PostMapping("/details")
    public Result insert(@RequestBody User user){
        userService.update(new LambdaUpdateWrapper<User>()
                .eq(User::getUserId,user.getUserId())
                .set(User::getNickName,user.getNickName())
                .set(User::getRealName,user.getRealName())
                .set(User::getUserMail,user.getUserMail())
                .set(User::getGender,user.getGender())
                .set(User::getBirthDate,user.getBirthDate()));
        return new Result(200,"填写成功！");
    }
    @GetMapping("/check/{userId}")
    public Result check(@PathVariable String userId){
        return new Result(200,"成功！",userService.getById(userId));
    }
}
