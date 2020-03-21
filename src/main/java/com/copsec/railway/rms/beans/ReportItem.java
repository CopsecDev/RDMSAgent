package com.copsec.railway.rms.beans;

import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.enums.MonitorTypeEnum;
import lombok.Data;

/**
 * 上报数据项
 */
@Data
public class ReportItem {

	private String monitorId;
	private MonitorItemEnum monitorItemType;
	private MonitorTypeEnum monitorType;
	private String item;
	private Object result;
	private int status = 1;
}
