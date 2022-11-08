package com.hdy.dbmovie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hdy.dbmovie.dao.CelebritiesMapper;
import com.hdy.dbmovie.pojo.Celebrities;
import com.hdy.dbmovie.service.CelebritiesService;
import org.springframework.stereotype.Service;

/**
 * 演员表(Celebrities)表服务实现类
 *
 * @author Honetooyoung
 * @since 2022-11-02 21:26:23
 */
@Service("celebritiesService")
public class CelebritiesServiceImpl extends ServiceImpl<CelebritiesMapper, Celebrities> implements CelebritiesService {

}

