package com.hdy.dbmovie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hdy.dbmovie.common.bean.Result;
import com.hdy.dbmovie.component.FilmComponent;
import com.hdy.dbmovie.pojo.Film;
import com.hdy.dbmovie.pojo.HistoryRecord;
import com.hdy.dbmovie.service.FilmService;
import com.hdy.dbmovie.service.HistoryRecordService;
import com.hdy.dbmovie.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 电影表(DbFilm)表控制层
 *
 * @author Honetooyoung
 * @since 2022-10-31 16:53:59
 */
@RestController
@Slf4j
@RequestMapping("/db/Film")
public class FilmController{
    /**
     * 服务对象
     */
    @Resource
    private FilmService filmService;
    @Resource
    private FilmComponent filmComponent;
    @Resource
    private HistoryRecordService historyRecordService;

    /**
     * 分页查询所有数据
     *
     * @param page   分页对象
     * @param keyword 查询实体
     * @return 所有数据
     */
    @GetMapping(value = "/search")
    public Result selectAll(Page<Film> page,@RequestParam("keyword") String keyword) {
        Page<Film> filmPage = this.filmService.page(page, new LambdaQueryWrapper<Film>()
                .like(Film::getFilmName, "%" + keyword + "%").or()
                .like(Film::getDetail, "%" + keyword + "%"));
        return filmComponent.showCelebrities(filmPage);
    }

    /**
     * 通过主键查询电影详情
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/{id}/{userId}")
    public Result selectOne(@PathVariable String id,@PathVariable String userId) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Film> list=new ArrayList<>();
        list.add(filmService.getById(id));
        //存入历史记录 过期时间为七天
        RedisUtil.set("HRecords:"+userId+":"+id,simpleDateFormat.format(System.currentTimeMillis()),60*60*24*7);
        //存入数据库
        historyRecordService.saveOrUpdate(new HistoryRecord(id,userId));
        return filmComponent.showCelebrities(new Page<Film>().setRecords(new ArrayList<>(list)));
    }
    /**
     * 分页查询所有数据
     *
     * @param page   分页对象
     * @param tag 查询实体
     * @return 所有数据
     */
    @GetMapping(value = "/tags")
    public Result selectByTag(Page<Film> page,@RequestParam("tag") String tag) {
        Page<Film> filmPage = this.filmService.page(page, new LambdaQueryWrapper<Film>()
                .like(Film::getTags, "%" + tag + "%"));
        return new Result(200,tag+"分类",filmPage);
    }

    @GetMapping("/rank/{No}")
    public Result rank(Page<Film> page,@PathVariable Long No){
        List<Film> filmList;
        if (!RedisUtil.hasKey("Film:")){
            filmList=this.filmService.page(page).getRecords();
            filmList.forEach(film -> {
                StringBuilder stringBuilder=new StringBuilder(film.getScore());
                String s = stringBuilder.substring(stringBuilder.indexOf("score") + 9, stringBuilder.indexOf("score") + 12);
                RedisUtil.zAdd("Film:",film,Double.parseDouble(s));
            });
        }
        Set<Object> films;
        try {
            films = RedisUtil.zReverseRange("Film:", 0, No);
        } catch (Exception e) {
            log.error("Redis崩溃！！");
            return new Result(200,"热门电影排行：",filmService.page(page));
        }
        return new Result(200, "热门电影排行：", films);
    }
}

