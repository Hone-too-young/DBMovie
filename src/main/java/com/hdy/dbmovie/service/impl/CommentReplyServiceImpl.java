package com.hdy.dbmovie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hdy.dbmovie.dao.CommentReplyMapper;
import com.hdy.dbmovie.pojo.CommentReply;
import com.hdy.dbmovie.service.CommentReplyService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 讨论区表(CommentReply)表服务实现类
 *
 * @author Honetooyoung
 * @since 2022-11-05 10:27:15
 */
@Service("commentReplyService")
public class CommentReplyServiceImpl extends ServiceImpl<CommentReplyMapper, CommentReply> implements CommentReplyService {
    /**
     * 查询子回复
     * @param commentReplyList
     * @return List<CommentReply>
     */
    @Override
    public List<CommentReply> checkReply(List<CommentReply> commentReplyList) {
        //遍历评论表
        commentReplyList.forEach(commentReply -> {
            //查询评论的子评论列表
            List<CommentReply> replyList = this.list(new LambdaQueryWrapper<CommentReply>().eq(CommentReply::getPid, commentReply.getRid()));
            if (replyList!=null){
                //递归查询子评论
                checkReply(replyList);
            }
            //封装到评论对象里
            commentReply.setCommentReplyList(replyList);
        });
        return commentReplyList;
    }
}

