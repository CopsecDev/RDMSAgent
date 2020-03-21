package com.copsec.railway.rms.listeners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import com.copsec.railway.rms.configurations.CopsecConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationReadyListener.class);

	@Autowired
	private CopsecConfigurations config;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

		String temp = ManagementFactory.getRuntimeMXBean().getName();
		File pid = new File(config.getPidPath());
		if(!pid.exists()){

			try {
				pid.createNewFile();
			}
			catch (IOException e) {

				logger.error(e.getMessage(),e);
			}
		}
		try(FileWriter writer = new FileWriter(pid)){

			writer.write(temp.substring(0,temp.indexOf("@")));
			writer.flush();
		}
		catch (IOException e) {

			logger.error(e.getMessage(),e);
		}
		if(logger.isDebugEnabled()){

			logger.debug("Application start success with {}",temp);
		}
	}
}
