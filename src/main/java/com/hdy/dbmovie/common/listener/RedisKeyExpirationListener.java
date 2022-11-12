package com.hdy.dbmovie.common.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hdy.dbmovie.pojo.HistoryRecord;
import com.hdy.dbmovie.service.HistoryRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }
    @Resource
    HistoryRecordService historyRecordService;
    @Override
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

}

