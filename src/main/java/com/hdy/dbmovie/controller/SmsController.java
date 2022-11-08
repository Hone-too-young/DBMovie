package com.hdy.dbmovie.controller;

import com.google.common.collect.Maps;
import com.hdy.dbmovie.common.bean.Result;
import com.hdy.dbmovie.common.bean.SendSmsParam;
import com.hdy.dbmovie.common.enums.SmsType;
import com.hdy.dbmovie.service.SmsLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//发送验证码
@RestController
@RequestMapping("/db/sms")
public class SmsController {

	@Autowired
	private SmsLogService smsLogService;
    /**
     * 发送验证码接口
     */
    @PostMapping("/send")
    //发送验证码
        public Result send(@RequestBody SendSmsParam sendSmsParam) {

        Result result = smsLogService.sendSms(SmsType.VALID, sendSmsParam.getMobile(), Maps.newHashMap());

        return result;
    }

}
