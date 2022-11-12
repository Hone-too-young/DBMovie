# DBMovie
豆拌电影评分网站
***
***用户服务模块***
  1. 登录注册
      1. 基于shiro框架的JWT权限认证
          1. 登录发送token到客户端消息头
          2. token过期，Redis存的token未过期，刷新token
          3. Redis存的token过期，被拦截
          4. token生成，解析校验（用户名，登陆时间，更新时间）
      2. 模拟短信验证注册
          1. 发送短信验证码（没有购买短信，Result返回短信信息）
          2. 校验手机号码验证码，用于注册及修改密码
      3. 注册密码加密MD5存储
  2. 保持登录状态
      1. 根据token保持登录状态
      2. 根据最近一次操作刷新token
  3. 退出登录
      1. 销毁token
  4. 上传头像
      1. 头像图片存储于classpath：resources/static/upload
 ***
***主页***
  1. 搜索
      1. 电影演员共同模糊查询
          1. 返回电影信息
          2. 缓存演员信息，以便快速查询
          3. 查看电影详情
              1. 电影详情
              2. 参演演员列表
              3. 查看所有短评
                  1. 点赞（缓存）<br>
                    ●定时更新数据库(5分钟)
                  2. 点踩（缓存）<br>
                    ●定时更新数据库（5分钟）
                  3. 根据点赞数量排序
                  4. 标签，隐藏or公开，想看or看过
              3. 短评下的评论区
                  1. 点赞（缓存）<br>
                    ●定时更新数据库
                  2. 点踩（缓存）<br>
                    ●定时更新数据库
                  3. 根据时间排序
          4. 查看演员详情
              1. 代表作品
      2. 关键字搜索演员
          1. 返回演员列表
          2. 查看演员详情
  2. 电影分类
       1. 根据标签分类电影
  3. 电影排行
       1. 根据电影综合评分排序
       2. 存进Redis缓存快速查询
       3. 获取top50+电影
 ***
***个人页面***
  1. 填写实名信息
      1. 上传头像
      2. 更改昵称等
  2. 记录<br>
      1. 历史观看记录（电影列表）
          1. 存进Redis做缓存，设置七天后过期
          2. 监听器，监听历史记录，过期删除MySQL中的数据
      3. 查看自己所有评论
      4. 查看自己所有短评
      
      ***
      
***遇到的问题以及解决思路***
1.  **用户查看电影，留下浏览记录，七天后过期。**<br><br>
缓存Redis，存入数据库，设置key的过期时间为七天，开启Redis监听事件，当key过期时删除数据库对应记录<br><br>
```java
public void onMessage(Message message, byte[] pattern) {
        try {
            String expiredKey = message.toString();
            String[] strings = expiredKey.split(":");
            if(strings[0].equals("HRecords")){
                log.info("删除历史记录");
                String filmId=strings[2];
                historyRecordService.remove(new LambdaQueryWrapper<HistoryRecord>().eq(HistoryRecord::getFilmId,filmId));
            }
        } catch (Exception e) {
            log.error("key 过期通知处理异常，{?}", e);
        }
    }
```
2.  **用户频繁点赞并取消，大量数据库读写，容易导致数据库崩溃问题。**<br><br>
先将点赞点踩存入数Redis,做定时任务，每隔五分钟将Redis的点赞，点踩数量更新至数据库，读写Redis优先，如遇到Redis崩溃最多丢失五分钟数据（可根据具体情况调整定时任务的时间）<br><br>
```java
@Override
    protected void executeInternal(@NotNull JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info("LikeTask-------- {}", sdf.format(new Date()));

        //将 Redis 里的点赞信息同步到数据库里
        //commentService.transLikedFromRedis2DB();
        commentService.transLikedCountFromRedis2DB();
    }
```
```java
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
```
3.  **影评下的讨论区多级回复问题**<br><br>
在Reply实体类里增加List<Reply>集合。每条影评下首先查询pid（父ID）为0的评论放进集合，遍历并调用checkReply方法根据每条评论的ID与其他评论的pid匹配相同的作为子评论放入List<Reply>集合中，并继续调用checkReply方法匹配该子评论的子评论，一直递归，直到查出所有的子评论。<br><br>
从而实现一张表存储所有评论及回复<br><br>
```java
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
```
4.  **电影排行查询速度过慢问题**<br><br>
第一次查询时根据电影信息里的评分缓存进Redis做排行进行持久化操作，加快查询速度。<br><br>

5.  **模糊查找电影时根据提供的演员ID查询具体演员信息，速度过慢。**<br><br>
设置前面页数的电影查询连表查询演员信息，后面的数据则不查询演员信息，并且再查询演员信息时，顺便缓存进Redis，以便用户在查询电影后查看演员具体信息。<br>

6.  **个人历史记录缓存问题，set集合中的元素无法设置过期时间，只有键值对的key可以设置过期时间**<br><br>
用户id和电影id拼接成key，value存时间，key设置七天过期时间。<br>
先从Redis中查询，再到MySQL中查询
```java
SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Set<String> keys = RedisUtil.keys("HRecords:" + userId + ":".concat("*"));
        List<HistoryRecord> films = new ArrayList<>();
        if (!keys.isEmpty()) {
            AtomicReference<Integer> i = new AtomicReference<>(new Integer(0));
            keys.forEach(key -> {
                try {
                    films.add(new HistoryRecord(i.updateAndGet(v -> v + 1), key.split(":")[2], userId, simpleDateFormat.parse(RedisUtil.get(key).toString())));
                } catch (ParseException e) {
                    log.error("时间转换问题！");
                }
            });
        } else {
            films.addAll(historyRecordService.list(new LambdaQueryWrapper<HistoryRecord>().eq(HistoryRecord::getUserId, userId)));
        }
        return new Result(200, "成功", films);
```

