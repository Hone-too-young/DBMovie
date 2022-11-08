package com.hdy.dbmovie.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hdy.dbmovie.common.bean.Result;
import com.hdy.dbmovie.pojo.Celebrities;
import com.hdy.dbmovie.pojo.Film;
import com.hdy.dbmovie.service.CelebritiesService;
import com.hdy.dbmovie.service.FilmService;
import com.hdy.dbmovie.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class FilmComponent {
    @Autowired
    private CelebritiesService celebritiesService;
    @Autowired
    private FilmService filmService;

    public Result showCelebrities(Page<Film> page) {
        //从page对象中拿到电影列表
        List<Film> records = page.getRecords();
        //新的电影列表
        List<Film> filmList = new ArrayList<>();
        //遍历电影列表
        ListIterator<Film> filmListIterator = records.listIterator();
        while (filmListIterator.hasNext()) {
            Film film = filmListIterator.next();
            //取出演员列表（字符串）
            String celebrities = film.getCelebrities().substring(1, film.getCelebrities().length() - 1).replace(" ", "");
            //遍历查询演员（返回演员列表）
            int index = 0;
            String[] strings = celebrities.split(",");
            List<Celebrities> celebritiesList = new ArrayList<>();
            while (index++ < strings.length - 1) {
                Celebrities actor = celebritiesService.getOne(new LambdaQueryWrapper<Celebrities>().eq(Celebrities::getId, strings[index]));
                if (actor == null)
                    log.info("未知演员");
                else
                    RedisUtil.set("/db/celebrities/" + actor.getId() + ":", actor, 3000);
                celebritiesList.add(actor);
            }
            //获取演员基本信息
            StringBuilder stringBuilder=new StringBuilder();
            celebritiesList.forEach(celebrities1 ->{
                if (celebrities1!=null){
                    stringBuilder.append("Celebrities{" +
                            "id=" + celebrities1.getId() +
                            ", name='" + celebrities1.getName() + '\'' +
                            ", englishName='" + celebrities1.getEnglishName() + '\'' +
                            ", gender='" + celebrities1.getGender() + '\'' +
                            ", job='" + celebrities1.getJob() + '\'' +
                            '}') ;
                }

            } );
            //存入电影表
            film.setCelebrities(stringBuilder.toString());
            filmList.add(film);
        }
        page.setRecords(filmList);
        return new Result(200, "查询成功！", page);
    }

    public Result showWorks(Celebrities celebrity,Page<Film> page) {
        Page<Film> filmPage = this.filmService.page(page, new LambdaQueryWrapper<Film>()
                .like(Film::getCelebrities, "%" + celebrity.getId() + "%"));
        Map map=new HashMap();
        filmPage.getRecords().forEach(film->{map.put(film.getMid(),film.getFilmName());});
        Map result=new HashMap();
        result.put("演员:",celebrity);
        result.put("代表作品:",map);
        return new Result(200,"查询成功！",result);
    }
}
