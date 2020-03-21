package com.copsec.railway.rms.beans;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 上报数据
 */
@Data
public class Reports {

	private String deviceId;

	private Date reportTime;

	private List<ReportItem> reports;
}
