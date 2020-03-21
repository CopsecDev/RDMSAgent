package com.copsec.railway.rms.beans;

import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.enums.MonitorTypeEnum;
import lombok.Data;

/**
 * 监控项定义
 */
@Data
public class MonitorItem {

	private String monitorId;
	private String monitorName;
	private MonitorItemEnum monitorItemType;
	private MonitorTypeEnum monitorType;
	private String item = "N/A";
}
