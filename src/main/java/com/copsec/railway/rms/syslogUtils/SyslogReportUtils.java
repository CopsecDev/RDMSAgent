package com.copsec.railway.rms.syslogUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyslogReportUtils {

	private static final Logger logger = LoggerFactory.getLogger(SyslogReportUtils.class);
	private static DatagramSocket datagramSocket = null;
	private static String ip = "";
	private static int port = 0;

	public static boolean sendLog(String message,String logIp,int logPort){

		try {

			//如果地址发生改变，则关闭当前连接
			if(!logIp.equals(ip) || logPort != port){

				logger.warn("logConfig changed current socket will close");
				closeSocket();
				ip = logIp;
				port = logPort;
			}

			if(null == datagramSocket){
				try {

					datagramSocket = new DatagramSocket();
					logger.warn("create new socket to send syslog");
				}catch(Exception e){

					logger.error(e.getMessage(),e);
					//关闭socket连接
					closeSocket();
					return false;
				}
			}

			//组织log为string 类型数据，按照以，分割的方式
			try {

				if(logger.isDebugEnabled()){

					logger.debug("send syslog to remote -> {}",message);
				}
				byte[]data = message.getBytes("utf-8");
				datagramSocket.send(new DatagramPacket(data, data.length,InetAddress.getByName(ip),port));
			} catch (IOException e) {

				logger.error(e.getMessage(),e);
				return false;
			}
		} catch (Throwable e) {

			logger.error(e.getMessage(),e);
			return false;
		}
		return true;
	}

	public static void closeSocket(){

		if(null != datagramSocket){

			datagramSocket.close();
			datagramSocket = null;
			logger.warn("logConfig changed current socket will close");
		}
	}
}
