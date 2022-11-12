package com.hdy.dbmovie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hdy.dbmovie.dao.HistoryRecordDao;
import com.hdy.dbmovie.pojo.HistoryRecord;
import com.hdy.dbmovie.service.HistoryRecordService;
import org.springframework.stereotype.Service;

/**
 * 历史记录表(HistoryRecord)表服务实现类
 *
 * @author Honetooyoung
 * @since 2022-11-08 15:43:43
 */
@Service("historyRecordService")
public class HistoryRecordServiceImpl extends ServiceImpl<HistoryRecordDao, HistoryRecord> implements HistoryRecordService {

}

