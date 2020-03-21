package com.copsec.railway.rms.beans;

import java.util.Date;

import lombok.Data;

@Data
public class CertInfoBean {

	private String nickname;

	private String subject;

	private String issuer;

	private Date startTime;

	private Date endTime;

	private String message;

	private int status;
}
