package com.copsec.railway.rms.beans.vmInfo;

import lombok.Data;

@Data
public class DomainInfo {

	private String name;
	private String state;
	private String flags;
	private String cons;
	private String ncpu;
	private String mem;
	private String util;
	private String normutil;
	private String uptime;//秒
}
