package com.hdy.dbmovie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hdy.dbmovie.pojo.Comment;

/**
 * 短评表(Comment)表服务接口
 *
 * @author Honetooyoung
 * @since 2022-11-05 10:27:14
 */
public interface CommentService extends IService<Comment> {

    void transLikedFromRedis2DB();

    void transLikedCountFromRedis2DB();
}

