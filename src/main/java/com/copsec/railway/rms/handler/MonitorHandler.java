package com.copsec.railway.rms.handler;

import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ReportItem;

public interface MonitorHandler {

	public ReportItem handler(MonitorItem monitorItem);
}
