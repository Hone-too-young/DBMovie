package com.hdy.dbmovie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hdy.dbmovie.common.bean.Result;
import com.hdy.dbmovie.common.enums.SmsType;
import com.hdy.dbmovie.exception.dbBindException;
import com.hdy.dbmovie.pojo.SmsLog;

import java.util.Map;

/**
 *
 * @author Honetooyoung 2022-10-29 09:12:28
 */
public interface SmsLogService extends IService<SmsLog> {


    Result sendSms(SmsType smsType, String mobile, Map<String, String> params) throws dbBindException;
}

