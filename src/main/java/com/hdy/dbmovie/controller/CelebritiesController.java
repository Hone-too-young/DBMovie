package com.hdy.dbmovie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hdy.dbmovie.common.bean.Result;
import com.hdy.dbmovie.component.FilmComponent;
import com.hdy.dbmovie.pojo.Celebrities;
import com.hdy.dbmovie.service.CelebritiesService;
import com.hdy.dbmovie.utils.RedisUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * 演员表(Celebrities)表控制层
 *
 * @author Honetooyoung
 * @since 2022-11-02 21:26:23
 */

@RestController
@RequestMapping("/db/celebrities")
public class CelebritiesController{
/**
     * 服务对象
*/

    @Resource
    private CelebritiesService celebritiesService;
    @Resource
    private FilmComponent filmComponent;
/**
     * 分页模糊查询演员信息
     *
     * @param page        分页对象
     * @param name 查询实体
     * @return 所有数据
     */

    @GetMapping("/page")
    public Result selectAll(Page<Celebrities> page, @RequestParam String name) {
        return new Result(200,"查询成功！"
                ,this.celebritiesService.page(page, new LambdaQueryWrapper<Celebrities>()
                .like(Celebrities::getName,name).or()
                .like(Celebrities::getEnglishName,name)));
    }

/**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
*/

    @GetMapping("/{id}")
    public Result selectOne(@PathVariable Serializable id) {
        Celebrities celebrity;
        if (RedisUtil.getExpire("/db/celebrities/" + id + ":")>0) {
            celebrity = (Celebrities) RedisUtil.get("/db/celebrities/" + id + ":");
        }else
            celebrity=celebritiesService.getById(id);
        Result result=filmComponent.showWorks(celebrity, new Page<>());
        return result;    }

}

