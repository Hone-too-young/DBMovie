package com.hdy.dbmovie.service.impl;

import com.hdy.dbmovie.pojo.Comment;
import com.hdy.dbmovie.service.CommentService;
import com.hdy.dbmovie.service.GiveLikeService;
import com.hdy.dbmovie.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service("commentGiveLikeService")
@Slf4j
public class CommentGiveLikeServiceImpl implements GiveLikeService {
    @Resource
    private CommentService commentService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 在Redis里查询电影短评 按热度排序
     *
     * @param filmId
     * @return
     */
    public static List<Comment> getCommentMapSortedByLike(Serializable filmId) {
        List<Comment> list;
        log.info("redis查询电影短评");
        //取出电影所有短评
        Map<Object, Object> map = RedisUtil.hmget(filmId + "/comment:all");
        //转换成comment对象
        Map<Object, Comment> commentMap = map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (Comment) e.getValue()));
        //按点赞数排序
        /*linkedHashMap = commentMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(Comment::getLikeNo).reversed()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldVal, newVal) -> oldVal,
                        LinkedHashMap::new));*/
        list=commentMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.comparingInt(Comment::getLikeNo).reversed()))
                .map(Map.Entry::getValue).collect(Collectors.toList());
        return list;
    }

    /**
     * 点赞
     *
     * @param cid
     * @param userId
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public boolean giveLike(String cid, String userId) {
        String filmId = commentService.getById(cid).getFilmId();
        //判断用户有没有点赞
        if (!RedisUtil.sHasKey("comment/like:" + cid, userId)) {
            //没点赞，存Redis
            RedisUtil.sSet("comment/like:" + cid, userId);
                //Redis里获取短评的赞数，
                Comment comment = (Comment) RedisUtil.hget(filmId + "/comment:all", cid);
                //Redis短评+1
                comment.setLikeNo(comment.getLikeNo() + 1);
                RedisUtil.hset(filmId + "/comment:all", cid, comment);

            return true;
        } else {
            //该用户已点赞，移除Redis
            RedisUtil.setRemove("comment/like:" + cid, userId);
                //Redis里获取短评的赞数，
                Comment comment = (Comment) RedisUtil.hget(filmId + "/comment:all", cid);
                //Redis短评-1
                comment.setLikeNo(comment.getLikeNo() - 1);
                RedisUtil.hset(filmId + "/comment:all", cid, comment);

            return false;
        }
    }

    /**
     * 返回点赞数
     *
     * @param cid
     * @return
     */
    @Override
    public Long likedNo(String cid) {
        return RedisUtil.sGetSetSize("comment/like:" + cid);
    }

    /**
     * 返回点赞列表
     *
     * @param cid
     * @return
     */
    @Override
    public Set<Object> likedList(String cid) {
        return RedisUtil.sGet("comment/like:" + cid);
    }

    /**
     * 点踩
     *
     * @return boolean
     */
    @Override
    public boolean giveDisike(String cid, String userId) {
        String filmId = commentService.getById(cid).getFilmId();
        //判断用户有没有点踩
        if (!RedisUtil.sHasKey("comment/dislike:" + cid, userId)) {
            //没点踩，存Redis
            RedisUtil.sSet("comment/dislike:" + cid, userId);
                //Redis里获取短评的踩数，
                Comment comment = (Comment) RedisUtil.hget(filmId + "/comment:all", cid);
                //Redis短评+1
                comment.setDislikeNo(comment.getDislikeNo() + 1);
                RedisUtil.hset(filmId + "/comment:all", cid, comment);
            return true;
        } else {
            //该用户已点踩，移除Redis
            RedisUtil.setRemove("comment/dislike:" + cid, userId);
                //Redis里获取短评的踩数，
                Comment comment = (Comment) RedisUtil.hget(filmId + "/comment:all", cid);
                //Redis短评-1
                comment.setDislikeNo(comment.getDislikeNo() - 1);
                RedisUtil.hset(filmId + "/comment:all", cid, comment);
            return false;
        }
    }

    /**
     * 点踩数
     *
     * @param cid
     * @return
     */
    @Override
    public Long dislikedNo(String cid) {
        return RedisUtil.sGetSetSize("comment/dislike:" + cid);
    }

    /**
     * 点踩列表
     *
     * @param cid
     * @return
     */
    @Override
    public Set<Object> dislikedList(String cid) {
        return RedisUtil.sGet("comment/dislike:" + cid);
    }

    //缓存点赞
    private boolean saveLike(String cid, String userId) {
        SessionCallback<Boolean> sessionCallback = new SessionCallback<Boolean>() {
            List<Object> exec = null;

            @Override
            @SuppressWarnings("unchecked")
            public Boolean execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                RedisUtil.sSet("comment/like:" + cid, userId);
                exec = operations.exec();
                return exec.size() > 0;
            }
        };
        return Boolean.TRUE.equals(redisTemplate.execute(sessionCallback));
    }

    //缓存取消点赞
    private boolean removeLike(String cid, String userId) {
        SessionCallback<Boolean> sessionCallback = new SessionCallback<Boolean>() {
            List<Object> exec = null;

            @Override
            @SuppressWarnings("unchecked")
            public Boolean execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                RedisUtil.setRemove("comment/like:" + cid, userId);
                exec = operations.exec();
                return exec.size() > 0;
            }
        };
        return Boolean.TRUE.equals(redisTemplate.execute(sessionCallback));
    }

    //缓存点踩
    private boolean saveDislike(String cid, String userId) {
        SessionCallback<Boolean> sessionCallback = new SessionCallback<Boolean>() {
            List<Object> exec = null;

            @Override
            @SuppressWarnings("unchecked")
            public Boolean execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                RedisUtil.sSet("comment/dislike:" + cid, userId);
                exec = operations.exec();
                return exec.size() > 0;
            }
        };
        return Boolean.TRUE.equals(redisTemplate.execute(sessionCallback));
    }

    //缓存取消点踩
    private boolean removeDislike(String cid, String userId) {
        SessionCallback<Boolean> sessionCallback = new SessionCallback<Boolean>() {
            List<Object> exec = null;

            @Override
            @SuppressWarnings("unchecked")
            public Boolean execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                RedisUtil.setRemove("comment/dislike:" + cid, userId);
                exec = operations.exec();
                return exec.size() > 0;
            }
        };
        return Boolean.TRUE.equals(redisTemplate.execute(sessionCallback));
    }
}
