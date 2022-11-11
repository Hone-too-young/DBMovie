package com.hdy.dbmovie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hdy.dbmovie.VO.UserVO;
import com.hdy.dbmovie.common.bean.Result;
import com.hdy.dbmovie.pojo.Comment;
import com.hdy.dbmovie.pojo.User;
import com.hdy.dbmovie.service.CommentService;
import com.hdy.dbmovie.service.GiveLikeService;
import com.hdy.dbmovie.service.UserService;
import com.hdy.dbmovie.service.impl.CommentGiveLikeServiceImpl;
import com.hdy.dbmovie.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 短评表(Comment)表控制层
 *
 * @author Honetooyoung
 * @since 2022-11-05 10:27:14
 */
@Slf4j
@RestController
@RequestMapping("/db/comment")
public class CommentController {
    @Resource
    UserService userService;
    @Resource
    MapperFactory mapperFactory;
    @Resource
    DataSourceTransactionManager dataSourceTransactionManager;
    @Resource
    TransactionDefinition transactionDefinition;
    /**
     * 服务对象
     */
    @Resource
    private CommentService commentService;
    @Resource
    private GiveLikeService commentGiveLikeService;

    /**
     * 通过电影id分页查询所有公开短评
     * 按热度排序
     * Redis里有从缓存里查
     * 没有则从MySQL查
     *
     * @param filmId 主键
     * @return List<Comment>
     */
    @GetMapping("/{filmId}")
    public Result selectOne(@PathVariable Serializable filmId, Page<Comment> page) {
        if (RedisUtil.hasKey(filmId + "/comment:all")) {
            //redis里查询
            List<Comment> commentList = CommentGiveLikeServiceImpl.getCommentMapSortedByLike(filmId);
            //返回CommentList
            return new Result(200, "查询成功！", commentList);
        } else {
            log.info("mysql查询电影短评");
            return new Result(200, "查询成功！"
                    , commentService.page(page, new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getFilmId, filmId)
                    //查询公开短评
                    .eq(Comment::getIsPublic, 1)
                    //根据点赞数量排序
                    .orderByDesc(Comment::getLikeNo)));
        }
    }

    /**
     * 添加短评
     * 加入缓存
     *
     * @param comment 实体对象
     * @return 新增结果
     */
    @PostMapping("/comment")
    //@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Result insert(@RequestBody Comment comment) {
        //开启事务
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        //存入数据库
        boolean b = commentService.save(comment);
        //提交事务
        dataSourceTransactionManager.commit(transactionStatus);
        //公开短评存入redis做排序
        Comment newComment = commentService.getOne(new LambdaQueryWrapper<Comment>().orderByDesc(Comment::getCid).last("limit 1"));
        if (newComment.getIsPublic() == 1) {
            RedisUtil.hset(comment.getFilmId() + "/comment:all", comment.getCid(), newComment);
        }
        return new Result(b);
    }

    /**
     * 查看个人短评
     */
    @GetMapping("/mine/{userId}")
    public Result checkMine(@PathVariable String userId, Page<Comment> page) {
        Page<Comment> commentPage = commentService.page(page, new LambdaQueryWrapper<Comment>().eq(Comment::getUserId, userId));
        return new Result(200, "返回个人短评集", commentPage);
    }

    /**
     * 删除短评
     * 删除缓存
     *
     * @param cid 主键结合
     * @return Result
     */
    @DeleteMapping("/delete/{cid}")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Result delete(@PathVariable Serializable cid) {
        Comment comment = commentService.getById(cid);
        //移除redis缓存
        RedisUtil.hdel(comment.getFilmId() + "/comment:all",cid);
        return new Result(commentService.removeById(cid));
    }

    /**
     * 点赞
     *
     * @param cid
     * @param userId
     * @return Result
     */
    @GetMapping("/giveLike/{cid}/{userId}")
    public Result giveLike(@PathVariable String cid, @PathVariable String userId) {
        if (commentGiveLikeService.giveLike(cid, userId))
            return new Result(200, "已点赞！");
        else
            return new Result(200, "取消点赞！");
    }

    /**
     * 点踩
     *
     * @param cid
     * @param userId
     * @return Result
     */
    @GetMapping("/giveDislike/{cid}/{userId}")
    public Result giveDislike(@PathVariable String cid, @PathVariable String userId) {
        if (commentGiveLikeService.giveDisike(cid, userId))
            return new Result(200, "已踩！");
        else
            return new Result(200, "取消踩！");
    }

    /**
     * 返回点赞列表
     *
     * @param cid
     * @return Result
     */
    @GetMapping("/likedList/{cid}")
    public Result likedList(@PathVariable String cid) {
        List<User> userList = new ArrayList<>();
        commentGiveLikeService.likedList(cid).forEach(userId -> {
            userList.add(userService.getById((Serializable) userId));
        });
        mapperFactory.classMap(User.class, UserVO.class)
                .exclude("realName")
                .exclude("userMail")
                .exclude("loginPassword")
                .exclude("userMobile")
                .exclude("gender")
                .exclude("birthDate")
                .exclude("userRegisterTime").byDefault().register();
        List<UserVO> userVOList = mapperFactory.getMapperFacade().mapAsList(userList, UserVO.class);
        return new Result(200, "点赞用户列表", userVOList);
    }

    /**
     * 返回点赞人数
     *
     * @param cid
     * @return 点赞人数
     */
    @GetMapping("/likedNo/{cid}")
    public Result likedNo(@PathVariable String cid) {
        return new Result(200, "点赞人数", commentGiveLikeService.likedNo(cid));
    }

    /**
     * 返回点踩人数
     *
     * @param cid
     * @return 点踩人数
     */
    @GetMapping("/dislikedNo/{cid}")
    public Result dislikedNo(@PathVariable String cid) {
        return new Result(200, "点踩人数", commentGiveLikeService.dislikedNo(cid));
    }
}