package com.hdy.dbmovie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hdy.dbmovie.dao.UserMapper;
import com.hdy.dbmovie.pojo.User;
import com.hdy.dbmovie.service.UserService;
import org.springframework.stereotype.Service;


/**
 * 用户表(User)表服务实现类
 *
 * @author Honetooyoung
 * @since 2022-10-28 17:47:20
 */

@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}

