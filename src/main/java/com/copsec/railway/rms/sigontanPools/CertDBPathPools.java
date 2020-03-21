package com.copsec.railway.rms.sigontanPools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

public class CertDBPathPools {

	private static CertDBPathPools pool = null;

	private CertDBPathPools(){}

	private ConcurrentMap<String,String> map = Maps.newConcurrentMap();

	public static synchronized  CertDBPathPools getInstances(){

		if( pool == null){

			synchronized (CertDBPathPools.class){

				if(pool == null){

					pool = new CertDBPathPools();
				}
			}
		}
		return pool;
	}

	public void addPath(String instanceName,String path){

		map.putIfAbsent(instanceName,path);
	}

	public String getPath(String instanceName){

		return map.get(instanceName);
	}

	public boolean isEmpty(){

		return map.isEmpty();
	}

	public Map<String,String> getAll(){

		return map;
	}

}
