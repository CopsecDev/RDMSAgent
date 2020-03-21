package com.copsec.railway.rms.sigontanPools;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.enums.MonitorTypeEnum;
import com.copsec.railway.rms.handler.MonitorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorHandlerPools {

	private static final Logger logger = LoggerFactory.getLogger(MonitorHandlerPools.class);
	private static MonitorHandlerPools pool = null;

	public static ConcurrentHashMap<MonitorItemEnum,MonitorHandler> handlerMap = new ConcurrentHashMap<>();
	public static synchronized MonitorHandlerPools getInstance() {

		if(pool ==null){

			synchronized(MonitorHandlerPools.class){

				if(pool == null){

					pool = new MonitorHandlerPools();
				}
			}
		}
		return pool;
	}

	private MonitorHandlerPools() {}

	public void registerHandler(MonitorItemEnum type,MonitorHandler handler){

		handlerMap.putIfAbsent(type,handler);
		if(logger.isDebugEnabled()){

			logger.debug("register {} handler success",handler.getClass().getSimpleName());
		}
	}

	public Optional<MonitorHandler> getHandler(MonitorItemEnum type){

		Optional<MonitorHandler> optionalMonitorHandler =
				Optional.ofNullable(handlerMap.get(type));

		return optionalMonitorHandler;
	}
}
