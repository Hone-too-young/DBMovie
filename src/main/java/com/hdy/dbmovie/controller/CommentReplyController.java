package com.hdy.dbmovie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hdy.dbmovie.common.bean.Result;
import com.hdy.dbmovie.pojo.CommentReply;
import com.hdy.dbmovie.service.CommentReplyService;
import com.hdy.dbmovie.service.GiveLikeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 讨论区表(CommentReply)表控制层
 *
 * @author Honetooyoung
 * @since 2022-11-05 10:27:14
 */
@RestController
@RequestMapping("/db/commentReply")
public class CommentReplyController {
    /**
     * 服务对象
     */
    @Resource
    private CommentReplyService commentReplyService;
    @Resource
    private GiveLikeService replyGiveLikeService;

    /**
     * 分页查询短评下的所有评论以及评论的回复
     *
     * @param page         分页对象
     * @param cid 查询实体
     * @return 所有数据
     */
    @GetMapping("/community/{cid}")
    public Result selectAll(Page<CommentReply> page,@PathVariable String cid) {
        Page<CommentReply> replyPage = commentReplyService.page(page, new LambdaQueryWrapper<CommentReply>().eq(CommentReply::getCid, cid).eq(CommentReply::getPid,0));
        List<CommentReply> commentReplies = commentReplyService.checkReply(replyPage.getRecords());
        replyPage.setRecords(commentReplies);
        return new Result(200,"讨论区",replyPage);
    }

    /**
     * 通过userId查询个人所有评论
     *
     * @param userId 主键
     * @return List<CommentReply>
     */
    @GetMapping("/{userId}")
    public Result selectMine(@PathVariable Serializable userId) {
        return new Result(200,"我的评论",commentReplyService.list(new LambdaQueryWrapper<CommentReply>().eq(CommentReply::getFromId,userId)));
    }

    /**
     * 添加评论
     *
     * @param commentReply 实体对象
     * @return 新增结果
     */
    @PostMapping("/comment")
    public Result insert(@RequestBody CommentReply commentReply) {
        return new Result(200,"评论成功！",this.commentReplyService.save(commentReply));
    }


    /**
     * 删除评论
     *
     * @param rid 主键结合
     * @return 删除结果
     */
    @DeleteMapping("/delete/{rid}")
    public Result delete(@PathVariable String rid) {
        return new Result(200,"删除成功！",this.commentReplyService.removeById(rid));
    }
    /**
     * 点赞
     *
     * @param rid
     * @param userId
     * @return Result
     */
    @GetMapping("/giveLike/{rid}/{userId}")
    public Result giveLike(@PathVariable String rid, @PathVariable String userId){
        if(replyGiveLikeService.giveLike(rid, userId))
            return new Result(200,"已点赞！");
        else
            return new Result(200,"取消点赞！");
    }
    /**
     * 点踩
     *
     * @param rid
     * @param userId
     * @return Result
     */
    @GetMapping("/giveDislike/{rid}/{userId}")
    public Result giveDislike(@PathVariable String rid,@PathVariable String userId ){
        if(replyGiveLikeService.giveDisike(rid, userId))
            return new Result(200,"已踩！");
        else
            return new Result(200,"取消踩！");
    }
    /**
     * 返回踩人数
     * @param rid
     * @return 点赞人数
     */
    @GetMapping("/likedNo/{rid}")
    public Result likedNo(@PathVariable String rid){
        return new Result(200,"点赞人数",replyGiveLikeService.likedNo(rid));
    }

    /**
     * 返回点踩人数
     * @param rid
     * @return 点踩人数
     */
    @GetMapping("/dislikedNo/{rid}")
    public Result dislikedNo(@PathVariable String rid){
        return new Result(200,"点踩人数",replyGiveLikeService.dislikedNo(rid));
    }
}

