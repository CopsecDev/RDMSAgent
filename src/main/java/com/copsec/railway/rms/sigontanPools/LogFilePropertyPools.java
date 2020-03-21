package com.copsec.railway.rms.sigontanPools;

import java.util.concurrent.ConcurrentHashMap;

import com.copsec.railway.rms.beans.FilePropertyBean;
import com.google.common.collect.Maps;

public class LogFilePropertyPools {
	private static LogFilePropertyPools pools = null;

	private static ConcurrentHashMap<String,FilePropertyBean> map = new ConcurrentHashMap<>();

	public static synchronized LogFilePropertyPools getInstance() {

		if(pools == null){

			synchronized (LogFilePropertyPools.class){

				if(pools == null){

					pools = new LogFilePropertyPools();
				}
			}
		}
		return pools;
	}

	private LogFilePropertyPools() {
	}

	public void add(FilePropertyBean bean){

		map.putIfAbsent(bean.getMonitorId(),bean);
	}

	public FilePropertyBean get(String monitorId){

		return map.get(monitorId);
	}

	public void update(FilePropertyBean bean){

		map.replace(bean.getMonitorId(),bean);
	}
}
