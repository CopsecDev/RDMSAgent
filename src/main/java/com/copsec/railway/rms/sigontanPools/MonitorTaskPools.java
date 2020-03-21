package com.copsec.railway.rms.sigontanPools;

import java.util.Collection;
import java.util.List;

import com.copsec.railway.rms.beans.MonitorItem;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * 存放设备监控项信息
 */
public class MonitorTaskPools {

	private static MonitorTaskPools pools = null;

	private Multimap<String ,MonitorItem> map = LinkedHashMultimap.create();

	public synchronized  static MonitorTaskPools getInstances(){

		if(pools == null){

			synchronized (MonitorTaskPools.class){

				if(pools == null){

					pools = new MonitorTaskPools();
				}
			}
		}
		return pools;
	}

	public void setMonitorItems(String deviceId,List<MonitorItem> monitorItems){

		map.clear();
		map.putAll(deviceId,monitorItems);
	}

	public Collection<MonitorItem> getMonitorItems(String deviceId){

		return map.asMap().get(deviceId);
	}
}
