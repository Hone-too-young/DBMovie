package com.hdy.dbmovie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hdy.dbmovie.pojo.CommentReply;

import java.util.List;

/**
 * 讨论区表(CommentReply)表服务接口
 *
 * @author Honetooyoung
 * @since 2022-11-05 10:27:15
 */
public interface CommentReplyService extends IService<CommentReply> {
 List<CommentReply> checkReply (List<CommentReply> commentReplyList);
}

