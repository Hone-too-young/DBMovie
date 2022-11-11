package com.hdy.dbmovie.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hdy.dbmovie.dao.CommentMapper;
import com.hdy.dbmovie.pojo.Comment;
import com.hdy.dbmovie.pojo.CommentReply;
import com.hdy.dbmovie.service.CommentReplyService;
import com.hdy.dbmovie.service.CommentService;
import com.hdy.dbmovie.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 短评表(Comment)表服务实现类
 *
 * @author Honetooyoung
 * @since 2022-11-05 10:27:14
 */
@Service("commentService")
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    private CommentService commentService;
    @Resource
    private CommentReplyService commentReplyService;

    @Override
    public void transLikedFromRedis2DB() {

    }

    @Override
    public void transLikedCountFromRedis2DB() {
        List<Comment> commentList = commentService.list();
        commentList.forEach(comment -> {
                    //点赞数
                    long likeNo = RedisUtil.sGetSetSize("comment/like:" + comment.getCid());
                    //点踩数
                    long dislikeNo = RedisUtil.sGetSetSize("comment/dislike:" + comment.getCid());
                    //更新点赞数并存入数据库（短评表）
                    commentService.update(new LambdaUpdateWrapper<Comment>().eq(Comment::getCid, comment.getCid()).set(Comment::getLikeNo, likeNo));
                    commentService.update(new LambdaUpdateWrapper<Comment>().eq(Comment::getCid, comment.getCid()).set(Comment::getDislikeNo, dislikeNo));
                }
        );
        commentReplyService.list().forEach(reply -> {
            //点赞数
            long likeNo = RedisUtil.sGetSetSize("reply/like:" + reply.getRid());
            //点踩数
            long dislikeNo = RedisUtil.sGetSetSize("reply/dislike:" + reply.getRid());
            //更新点赞数并存入数据库（短评表）
            commentReplyService.update(new LambdaUpdateWrapper<CommentReply>().eq(CommentReply::getRid, reply.getRid()).set(CommentReply::getLikeNo, likeNo));
            commentReplyService.update(new LambdaUpdateWrapper<CommentReply>().eq(CommentReply::getRid, reply.getRid()).set(CommentReply::getDislikeNo, dislikeNo));
        });
    }
}

