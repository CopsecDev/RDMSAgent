package com.copsec.railway.rms.processorUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;

import com.copsec.railway.rms.common.CopsecResult;
import com.copsec.railway.rms.configurations.StatisResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.ObjectUtils;

/**
 * 用于执行系统命令
 */
public class ProcessorUtils {

	private static final Logger logger = LoggerFactory.getLogger(ProcessorUtils.class);

	public static CopsecResult executeCommand(String command,Function<String,CopsecResult> function){

		String[] cmd = {"/bin/sh","-c",command};

		if(logger.isDebugEnabled()){

			logger.debug("command -> {}",command);
		}
		Runtime runtime = Runtime.getRuntime();

		try {
			Process process = runtime.exec(cmd);

			process.waitFor();

			if(process.exitValue() == 0){

				try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))){

					String line = null;
					StringBuilder sb = new StringBuilder();
					while( (line = reader.readLine()) != null){

						if(line.length() > 0){

							sb.append(line.trim()+StatisResources.line_spliter);
						}
					}
					if(logger.isDebugEnabled()){

						logger.debug("response content -> {}",sb.toString());
					}
					return function.apply(sb.toString());
				}catch (IOException e){

					logger.error(e.getMessage(),e);
				}
			}else{

				logger.error("command execute error -> {}",command);
				try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))){

					String line = null;
					StringBuilder sb = new StringBuilder();
					while( (line = reader.readLine()) != null){

						sb.append(line);
					}
					logger.error("error stream content -> {}",sb.toString());
					return CopsecResult.failed(sb.toString());

				}catch(IOException e){

					logger.error(e.getMessage(),e);
				}

			}
		}
		catch (IOException e) {

			logger.error(e.getMessage(),e);
		}
		catch (InterruptedException e) {

			logger.error(e.getMessage(),e);
		}
		return CopsecResult.failed(StatisResources.default_value);
	}

	public static CopsecResult processorIsRunning(String cmd){

		CopsecResult cmdResult = ProcessorUtils.executeCommand(cmd,str -> {

			str = str.replace(StatisResources.line_spliter,"");
			if(!ObjectUtils.isEmpty(str)){

				if(str.toString().equals("1")){

					return CopsecResult.success("实例正在运行",true);
				}else{

					return CopsecResult.success("实例已停止",false);
				}
			}
			return CopsecResult.failed(StatisResources.default_value);
		});

		return cmdResult;
	}
}
