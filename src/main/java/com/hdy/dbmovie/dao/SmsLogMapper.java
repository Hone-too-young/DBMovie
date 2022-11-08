package com.hdy.dbmovie.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hdy.dbmovie.pojo.SmsLog;
import org.apache.ibatis.annotations.Param;


public interface SmsLogMapper extends BaseMapper<SmsLog> {
	void invalidSmsByMobileAndType(@Param("mobile") String mobile, @Param("type") Integer type);
}