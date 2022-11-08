package com.hdy.dbmovie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hdy.dbmovie.common.bean.Result;
import com.hdy.dbmovie.component.FilmComponent;
import com.hdy.dbmovie.pojo.Film;
import com.hdy.dbmovie.service.FilmService;
import com.hdy.dbmovie.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    @Autowired FilmController filmController;
    @Resource
    private FilmComponent filmComponent;

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
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/{id}")
    public Result selectOne(@PathVariable Serializable id) {
        List<Film> list=new ArrayList<>();
        list.add(filmService.getById(id));
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
        List<Film> filmList = this.filmService.page(page).getRecords();
        if (!RedisUtil.hasKey("Film:")){
            filmList.forEach(film -> {
                StringBuilder stringBuilder=new StringBuilder(film.getScore());
                String s = stringBuilder.substring(stringBuilder.indexOf("score") + 9, stringBuilder.indexOf("score") + 12);
                RedisUtil.zAdd("Film:",film,Double.parseDouble(s));
            });
        }
        Result result = new Result(200, "热门电影排行：", RedisUtil.zReverseRange("Film:", 0, No));
        return result;
    }
}

