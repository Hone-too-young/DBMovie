package com.hdy.dbmovie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hdy.dbmovie.common.bean.Result;
import com.hdy.dbmovie.pojo.HistoryRecord;
import com.hdy.dbmovie.service.HistoryRecordService;
import com.hdy.dbmovie.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


/**
 * 历史记录表(HistoryRecord)表控制层
 *
 * @author makejava
 * @since 2022-11-08 15:43:42
 */
@Slf4j
@RestController
@RequestMapping("/db/historyRecord")
public class HistoryRecordController {
    /**
     * 服务对象
     */
    @Resource
    private HistoryRecordService historyRecordService;

    /**
     * 查询个人历史纪录
     *
     * @param userId 查询实体
     * @return 所有数据
     */
    @GetMapping("/check/{userId}")
    public Result selectAll(@PathVariable String userId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Set<String> keys = RedisUtil.keys("HRecords:" + userId + ":".concat("*"));
        List<HistoryRecord> films = new ArrayList<>();
        if (!keys.isEmpty()) {
            //我只是想让rid从开始自增
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
    }

    /**
     * 删除历史记录
     *
     * @param filmId 主键结合
     * @return 删除结果
     */
    @DeleteMapping("/delete/{userId}/{filmId}")
    public Result delete(@PathVariable String filmId, @PathVariable String userId) {
        //清楚Redis缓存
        RedisUtil.del("HRecords:" + userId + ":" + filmId);
        //移除数据库
        return new Result(200, "成功", historyRecordService.removeById(filmId));
    }
}

