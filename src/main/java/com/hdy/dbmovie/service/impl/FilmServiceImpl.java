package com.hdy.dbmovie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hdy.dbmovie.dao.FilmMapper;
import com.hdy.dbmovie.pojo.Film;
import com.hdy.dbmovie.service.FilmService;
import org.springframework.stereotype.Service;

/**
 * 电影表(DbFilm)表服务实现类
 *
 * @author Honetooyoung
 * @since 2022-10-31 16:53:59
 */
@Service("filmService")
public class FilmServiceImpl extends ServiceImpl<FilmMapper, Film> implements FilmService {

}

