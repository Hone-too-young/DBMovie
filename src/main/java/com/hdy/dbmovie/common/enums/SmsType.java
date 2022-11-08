package com.hdy.dbmovie.common.enums;

public enum SmsType {

	/**
	 * 发送验证码
	 */
	VALID(0, "SMS_254815807", "您的验证码为 ${code} ，该验证码5分钟内有效，请勿泄露于他人。");
	private Integer num;

	private String templateCode;

	private String content;
	public Integer value() {
		return num;
	}

	SmsType(Integer num,String templateCode,String content){
		this.num = num;
		this.templateCode = templateCode;
		this.content = content;
	}

	public static SmsType instance(Integer value) {
		SmsType[] enums = values();
		for (SmsType statusEnum : enums) {
			if (statusEnum.value().equals(value)) {
				return statusEnum;
			}
		}
		return null;
	}

	public String getTemplateCode() {
		return this.templateCode;
	}

	public String getContent() {
		return this.content;
	}
}