package com.copsec.railway.rms.syslogReports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.copsec.railway.rms.configurations.CopsecConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogParseUtils {

	private static final Logger logger = LoggerFactory.getLogger(LogParseUtils.class);

	public static String parseAMLogs(String line,String format,CopsecConfigurations config){

		StringBuffer sb = new StringBuffer();
		String[] logItems = line.split("\t");
		String[] logFormatItems = format.split("-");
		for(int i=0;i<logFormatItems.length;i++){

			String[] keys = logFormatItems[i].split(":");
			String tmp = logItems[Integer.valueOf(keys[1])];
			if(tmp.contains("id"))
			{
				tmp = tmp.substring(tmp.indexOf("id")+3,tmp.indexOf(","));
			}
			//基于密码，用户角色，service ，module，
			if((line.contains("AUTHENTICATION-100") ||
					line.contains("AUTHENTICATION-101") ||
					line.contains("AUTHENTICATION-102") ||
					line.contains("AUTHENTICATION-103") ||
					line.contains("AUTHENTICATION-104") ||
					line.contains("AUTHENTICATION-105"))&& tmp.contains("|") ){

				tmp = "Login";
			}
			//对应于以上登陆
			if((line.contains("AUTHENTICATION-300") ||
					line.contains("AUTHENTICATION-301") ||
					line.contains("AUTHENTICATION-302") ||
					line.contains("AUTHENTICATION-303") ||
					line.contains("AUTHENTICATION-304") ||
					line.contains("AUTHENTICATION-305"))&& tmp.contains("|")){

				tmp = "Logout";
			}
			sb.append(keys[0]+"="+tmp+",");
		}
		sb.append("deviceAddress="+config.getDeviceIp()+",");
		sb.append("deviceHostname="+config.getDeviceId()+",");
		sb.append("deviceProductType="+config.getProductionType()+",");
		sb.append("deviceSendProductName="+config.getProductionNameOfAm()+",");
		sb.append("deviceProtocol=syslog,");
		sb.append("deviceVendor="+config.getVendor());
		return sb.toString();
	}

	public static String parseProxyLogs(String line,CopsecConfigurations config){

		StringBuffer sb = new StringBuffer();
		String[] attrs = line.split(" ");
		if(attrs.length == 11){

			if(attrs[9].equals("403") || attrs[9].equals("500")){

				sb.append("collectorReceiptTime="+parserDate(attrs[4])+",");
				sb.append("srcAddress="+parserDate(attrs[0])+",");
				sb.append("srcUserGroupId="+parserDate(attrs[3])+",");
				sb.append("rawEvent="+(attrs[9].equals("403")?"forbidden":"Internal Error")+",");
				sb.append("deviceAddress="+config.getDeviceIp()+",");
				sb.append("deviceHostname="+config.getDeviceId()+",");
				sb.append("deviceProductType="+config.getProductionType()+",");
				sb.append("deviceSendProductName="+config.getProductionNameOfAm()+",");
				sb.append("deviceProtocol=syslog,");
				sb.append("deviceVendor="+config.getVendor());
				return sb.toString();
			}else{

				return "";
			}
		}
		return "";
	}


	private static String parserDate(String dateString){

		SimpleDateFormat dfs = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss",Locale.US);
		Date time = null;
		try {
			time = dfs.parse(dateString);
		}catch (ParseException e) {

			logger.error(e.getMessage(),e);
		}
		dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dfs.format(time);
	}
}
