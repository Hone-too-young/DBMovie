
package com.hdy.dbmovie.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hdy.dbmovie.common.bean.Result;
import com.hdy.dbmovie.common.enums.SmsType;
import com.hdy.dbmovie.dao.SmsLogMapper;
import com.hdy.dbmovie.exception.dbBindException;
import com.hdy.dbmovie.pojo.SmsLog;
import com.hdy.dbmovie.service.SmsLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;


/**
 * @author Honetooyoung on 2022-11-1 12:49:58
 */
@Service
@Slf4j
@AllArgsConstructor
public class SmsLogServiceImpl extends ServiceImpl<SmsLogMapper, SmsLog> implements SmsLogService {

    private final SmsLogMapper smsLogMapper;

    /**
     * 当天最大验证码短信发送量
     */
    private static final int TODAY_MAX_SEND_VALID_SMS_NUMBER = 10;

    /**
     * 一段时间内短信验证码的最大验证次数
     */
    private static final int TIMES_CHECK_VALID_CODE_NUM = 10;

    /**
     * 短信验证码的前缀
     */
    private static final String CHECK_VALID_CODE_NUM_PREFIX = "checkValidCodeNum_";

    /**
     * 短信发送成功的标志
     */
    private static final String SEND_SMS_SUCCESS_FLAG = "OK";

    private RedisTemplate<String,String> redisTemplate;


    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Result sendSms(SmsType smsType, String mobile, Map<String, String> params) throws dbBindException {

        SmsLog smsLog = new SmsLog();
        // 判断是否是发送验证码的枚举属性
        if (smsType.equals(SmsType.VALID)) {
            // 获取此手机号的一天的验证码次数
            int todaySendSmsNumber = Math.toIntExact(smsLogMapper.selectCount(new LambdaQueryWrapper<SmsLog>()
                    .gt(SmsLog::getRecDate, DateUtil.beginOfDay(new Date()))
                    .lt(SmsLog::getRecDate, DateUtil.endOfDay(new Date()))
                    .eq(SmsLog::getUserPhone, mobile)
                    .eq(SmsLog::getType, smsType.value())));
            if (todaySendSmsNumber >= TODAY_MAX_SEND_VALID_SMS_NUMBER) {
                return new Result(200,"今日发送短信验证码次数已达到上限");
            }
            //从redis获取验证码，如果能获取，直接返回未过期
            String lastCode = redisTemplate.opsForValue().get(mobile);
            if (StringUtils.hasText(lastCode)){
                return new Result(200,"验证码未过期");
            }

            // 将上一条验证码失效
            smsLogMapper.invalidSmsByMobileAndType(mobile, smsType.value());
            redisTemplate.delete(mobile);
            String code = RandomUtil.randomNumbers(6);
            params.put("code", code);
        }
        smsLog.setType(smsType.value());
        smsLog.setMobileCode(params.get("code"));
        smsLog.setRecDate(new Date());
        smsLog.setStatus(1);
        smsLog.setUserPhone(mobile);
        smsLog.setContent(formatContent(smsType, params));
        smsLogMapper.insert(smsLog);

            //this.sendSms(mobile, smsType.getTemplateCode(), params);
            // 发送成功，存入redis,5分钟
            redisTemplate.opsForValue().set(mobile,params.get("code"),5, TimeUnit.MINUTES);
            String contents="【豆拌电影】"+smsLog.getContent();
        return new Result(200,"发送成功",contents);
    }

    private String formatContent(SmsType smsType, Map<String, String> params) {
        if (CollectionUtil.isEmpty(params)) {
            return smsType.getContent();
        }
        String content = smsType.getContent();
        for (Entry<String, String> element : params.entrySet()) {
            content = content.replace("${" + element.getKey() + "}", element.getValue());
        }
        return content;
    }
}
