package com.hdy.dbmovie.common.bean;

import javax.validation.constraints.Pattern;


//发送验证码参数
public class SendSmsParam {
	
	//手机号
	@Pattern(regexp="1[0-9]{10}",message = "请输入正确的手机号")
	private String mobile;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}