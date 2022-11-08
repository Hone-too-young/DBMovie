package com.hdy.dbmovie.service.impl;

import com.hdy.dbmovie.service.GiveLikeService;
import com.hdy.dbmovie.utils.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service("replyGiveLikeService")
public class ReplyGiveLikeServiceImpl implements GiveLikeService {
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public boolean giveLike(String rid, String userId) {
        if (!RedisUtil.sHasKey("reply/like:" + rid, userId)) {
            RedisUtil.sSet("reply/like:" + rid, userId);
            return true;
        } else {
            RedisUtil.setRemove("reply/like:" + rid, userId);
            return false;
        }
    }

    @Override
    public Long likedNo(String rid) {
        return RedisUtil.sGetSetSize("reply/like:" + rid);
    }

    @Override
    public Set<Object> likedList(String id) {
        return null;
    }

    @Override
    public boolean giveDisike(String rid, String userId) {
        //判断用户有没有点赞
        if (!RedisUtil.sHasKey("reply/dislike:" + rid, userId)) {
            //没点赞，存Redis
            RedisUtil.sSet("reply/dislike:" + rid, userId);
            return true;
        } else {
            //该用户已点赞，移除Redis
            RedisUtil.setRemove("comment/dislike:" + rid, userId);
            //Redis里获取短评的赞数，
            return false;
        }
    }

    @Override
    public Long dislikedNo(String rid) {
        return RedisUtil.sGetSetSize("reply/dislike:" + rid);
    }

    @Override
    public Set<Object> dislikedList(String id) {
        return null;
    }
}
