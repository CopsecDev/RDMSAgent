package com.copsec.railway.rms.beans;

import lombok.Data;

@Data
public class LogPolicyBean {

	private String logPath;

	private int threadHold;
}
