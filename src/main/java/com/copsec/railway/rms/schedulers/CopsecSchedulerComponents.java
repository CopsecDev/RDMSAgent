package com.copsec.railway.rms.schedulers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.management.monitor.Monitor;

import com.alibaba.fastjson.JSON;
import com.copsec.railway.rms.beans.CertConfigBean;
import com.copsec.railway.rms.beans.LogPolicyBean;
import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ReportItem;
import com.copsec.railway.rms.beans.Reports;
import com.copsec.railway.rms.common.CopsecResult;
import com.copsec.railway.rms.configurations.CopsecConfigurations;
import com.copsec.railway.rms.configurations.StatisResources;
import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.enums.MonitorTypeEnum;
import com.copsec.railway.rms.fileUtils.FileReaderUtils;
import com.copsec.railway.rms.handler.MonitorHandler;
import com.copsec.railway.rms.httpClient.HttpClientUtils;
import com.copsec.railway.rms.sigontanPools.AmTokenPools;
import com.copsec.railway.rms.sigontanPools.CertDBPathPools;
import com.copsec.railway.rms.sigontanPools.MonitorHandlerPools;
import com.copsec.railway.rms.sigontanPools.MonitorTaskPools;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.identity.authentication.AuthContext;
import com.sun.identity.authentication.AuthContext.IndexType;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.shared.locale.L10NMessageImpl;
import org.apache.http.HttpStatus;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class CopsecSchedulerComponents {

	private static final Logger logger = LoggerFactory.getLogger(CopsecSchedulerComponents.class);

	@Autowired
	private CopsecConfigurations config;
	/**
	 * 15分钟定时获取amToken
	 */
	@Scheduled(fixedRate = 15 * 1000 * 60)
	@Async
	public void amScheduler(){

		if(config.getLocation().equals(StatisResources.Location_inner)){

			return;
		}
		if(ObjectUtils.isEmpty(System.getProperty("javax.net.ssl.keyStore"))){

			System.setProperty("javax.net.ssl.keyStore",config.getKeystorePath());
			if(logger.isDebugEnabled()){

				logger.debug("set keyStore path success {}",config.getKeystorePath());
			}
		}
		if(ObjectUtils.isEmpty(System.getProperty("javax.net.ssl.keyStorePassword"))){

			System.setProperty("javax.net.ssl.keyStorePassword",config.getKeystorePwd());
			if(logger.isDebugEnabled()){

				logger.debug("set keyStore password path success {}",config.getKeystorePwd());
			}
		}

		AuthContext authContext = null;
		try {

			authContext = new AuthContext(config.getBaseDn(),config.getNickname(),
					new URL(config.getAuthorizationPath()));

			authContext.login(IndexType.MODULE_INSTANCE,"Cert");

			String tokenId = authContext.getSSOToken().getTokenID().toString();

			if(!ObjectUtils.isEmpty(tokenId)){

				if(logger.isDebugEnabled()){

					logger.debug("get tokenId success -> {}",tokenId);
				}
				AmTokenPools.getInstances().setToken(tokenId);
			}
		}
		catch (MalformedURLException e) {

			logger.error(e.getMessage(),e);
		}
		catch (AuthLoginException e) {

			logger.error(e.getMessage(),e);
		}
		catch (L10NMessageImpl l10NMessage) {

			logger.error("am login failed code -> {},and args ars -> {}" ,l10NMessage.getErrorCode(),l10NMessage.getMessageArgs());
			logger.error(l10NMessage.getMessage());
		}
	}

	/**
	 * 5分钟获取一次监控数据项
	 */
	@Scheduled(fixedRate = 5 * 1000 * 60)
	@Async
	public void requestMonitorItems(){

		List<MonitorItem> monitorItems = Lists.newArrayList();

		MonitorItem cpuItem = new MonitorItem();
		cpuItem.setMonitorType(MonitorTypeEnum.SYSTEM);
		cpuItem.setMonitorName("CPU");
		cpuItem.setMonitorItemType(MonitorItemEnum.CPU);
		cpuItem.setMonitorId(UUID.randomUUID().toString());
		cpuItem.setItem("CPU");
		monitorItems.add(cpuItem);

		MonitorItem diskItem = new MonitorItem();
		diskItem.setMonitorType(MonitorTypeEnum.SYSTEM);
		diskItem.setMonitorName("DISK");
		diskItem.setMonitorItemType(MonitorItemEnum.DISK);
		diskItem.setMonitorId(UUID.randomUUID().toString());
		diskItem.setItem("DISK");
		monitorItems.add(diskItem);

		MonitorItem memoryItem = new MonitorItem();
		memoryItem.setMonitorType(MonitorTypeEnum.SYSTEM);
		memoryItem.setMonitorName("MEMORY");
		memoryItem.setMonitorItemType(MonitorItemEnum.MEMORY);
		memoryItem.setMonitorId(UUID.randomUUID().toString());
		memoryItem.setItem("memory");
		monitorItems.add(memoryItem);

		MonitorItem userItem = new MonitorItem();
		userItem.setMonitorType(MonitorTypeEnum.SYSTEM);
		userItem.setMonitorName("user");
		userItem.setMonitorItemType(MonitorItemEnum.USER);
		userItem.setMonitorId(UUID.randomUUID().toString());
		userItem.setItem("mor,root");
		monitorItems.add(userItem);

		MonitorItem systemTypeItem = new MonitorItem();
		systemTypeItem.setMonitorType(MonitorTypeEnum.SYSTEM);
		systemTypeItem.setMonitorName("systemType");
		systemTypeItem.setMonitorItemType(MonitorItemEnum.SYSTEMTYPE);
		systemTypeItem.setMonitorId(UUID.randomUUID().toString());
		systemTypeItem.setItem("systemType");
		monitorItems.add(systemTypeItem);

		MonitorItem systemVersionItem = new MonitorItem();
		systemVersionItem.setMonitorType(MonitorTypeEnum.SYSTEM);
		systemVersionItem.setMonitorName("systemVersionItem");
		systemVersionItem.setMonitorItemType(MonitorItemEnum.SYSTEMVERSION);
		systemVersionItem.setMonitorId(UUID.randomUUID().toString());
		systemVersionItem.setItem("systemVersionItem");
		monitorItems.add(systemVersionItem);

		MonitorItem web70Item = new MonitorItem();
		web70Item.setMonitorType(MonitorTypeEnum.INSTANCE);
		web70Item.setMonitorName("web70Instances");
		web70Item.setMonitorItemType(MonitorItemEnum.INSTANCES_WEB70);
		web70Item.setMonitorId(UUID.randomUUID().toString());
		web70Item.setItem("/usr/sjes/Web70/https-http-agent");
		monitorItems.add(web70Item);

		MonitorItem webProxy40Item = new MonitorItem();
		webProxy40Item.setMonitorType(MonitorTypeEnum.INSTANCE);
		webProxy40Item.setMonitorName("web40Instances");
		webProxy40Item.setMonitorItemType(MonitorItemEnum.INSTANCES_WEBPROXY40);
		webProxy40Item.setMonitorId(UUID.randomUUID().toString());
		webProxy40Item.setItem("/usr/sjes/WebProxy40/proxy-reverse-8092");
		monitorItems.add(webProxy40Item);

		MonitorItem configItem = new MonitorItem();
		configItem.setMonitorType(MonitorTypeEnum.INSTANCE);
		configItem.setMonitorName("configInstances");
		configItem.setMonitorItemType(MonitorItemEnum.INSTANCES_CONFIG);
		configItem.setMonitorId(UUID.randomUUID().toString());
		configItem.setItem("/usr/db/Oracle/Middleware/configds-er1");
		monitorItems.add(configItem);

		MonitorItem userDsItem = new MonitorItem();
		userDsItem.setMonitorType(MonitorTypeEnum.INSTANCE);
		userDsItem.setMonitorName("userInstances");
		userDsItem.setMonitorItemType(MonitorItemEnum.INSTANCES_USER);
		userDsItem.setMonitorId(UUID.randomUUID().toString());
		userDsItem.setItem("/usr/db/instances/userds-er1");
		monitorItems.add(userDsItem);

		MonitorItem netItem = new MonitorItem();
		netItem.setMonitorType(MonitorTypeEnum.NETWORK);
		netItem.setMonitorName("net");
		netItem.setMonitorItemType(MonitorItemEnum.NETWORK);
		netItem.setMonitorId(UUID.randomUUID().toString());
		netItem.setItem("10.1.241.246");
		monitorItems.add(netItem);

		MonitorItem certItem = new MonitorItem();
		certItem.setMonitorType(MonitorTypeEnum.CERT);
		certItem.setMonitorName("cert70");
		certItem.setMonitorItemType(MonitorItemEnum.CERT70);
		certItem.setMonitorId(UUID.randomUUID().toString());
		CertConfigBean configBean = new CertConfigBean();
		configBean.setInstanceName("/usr/sjes/Web70/https-http-agent");
		configBean.setNickname("Server-Cert,morl-app-securityagent");
		certItem.setItem(JSON.toJSONString(configBean));
		monitorItems.add(certItem);

		MonitorItem applicationItem = new MonitorItem();
		applicationItem.setMonitorType(MonitorTypeEnum.APPLICATION);
		applicationItem.setMonitorName("applicationItem");
		applicationItem.setMonitorItemType(MonitorItemEnum.APPLICATION);
		applicationItem.setMonitorId(UUID.randomUUID().toString());
		applicationItem.setItem("http://eproxy.china-railway.com.cn:5400/mapping/test/");
		monitorItems.add(applicationItem);

		MonitorItem applicationItem2 = new MonitorItem();
		applicationItem2.setMonitorType(MonitorTypeEnum.APPLICATION);
		applicationItem2.setMonitorName("applicationItem2");
		applicationItem2.setMonitorItemType(MonitorItemEnum.APPLICATION);
		applicationItem2.setMonitorId(UUID.randomUUID().toString());
		applicationItem2.setItem("http://eproxy.china-railway.com.cn:8072/testnoav/");
		monitorItems.add(applicationItem2);

		MonitorItem applicationItem3 = new MonitorItem();
		applicationItem3.setMonitorType(MonitorTypeEnum.APPLICATION);
		applicationItem3.setMonitorName("applicationItem3");
		applicationItem3.setMonitorItemType(MonitorItemEnum.APPLICATION);
		applicationItem3.setMonitorId(UUID.randomUUID().toString());
		applicationItem3.setItem("http://eaccess.china-railway.com.cn:8092/testnoav/");
		monitorItems.add(applicationItem3);

		MonitorItem applicationItem4 = new MonitorItem();
		applicationItem4.setMonitorType(MonitorTypeEnum.APPLICATION);
		applicationItem4.setMonitorName("applicationItem3");
		applicationItem4.setMonitorItemType(MonitorItemEnum.APPLICATION);
		applicationItem4.setMonitorId(UUID.randomUUID().toString());
		applicationItem4.setItem("http://eproxy.china-railway.com.cn:5100/mapping/test/");
		monitorItems.add(applicationItem4);

		MonitorItem imService = new MonitorItem();
		imService.setMonitorType(MonitorTypeEnum.PROCESSOR);
		imService.setMonitorName("imService");
		imService.setMonitorItemType(MonitorItemEnum.IMSERVICE);
		imService.setMonitorId(UUID.randomUUID().toString());
		imService.setItem("org,test2");
		monitorItems.add(imService);

		MonitorItem accessMonitor = new MonitorItem();
		accessMonitor.setMonitorType(MonitorTypeEnum.LOG);
		accessMonitor.setMonitorName("accessMonitor");
		accessMonitor.setMonitorItemType(MonitorItemEnum.ACCESSLOG);
		accessMonitor.setMonitorId(UUID.randomUUID().toString());
		LogPolicyBean logPolicyBean = new LogPolicyBean();
		logPolicyBean.setLogPath("/usr/sjes/am11/eaccess/amserver/log/amAuthentication.access");
		logPolicyBean.setThreadHold(60);
		accessMonitor.setItem(JSON.toJSONString(logPolicyBean));
		monitorItems.add(accessMonitor);

		MonitorItem proxyMonitor = new MonitorItem();
		proxyMonitor.setMonitorType(MonitorTypeEnum.LOG);
		proxyMonitor.setMonitorName("proxyMonitor");
		proxyMonitor.setMonitorItemType(MonitorItemEnum.PROXYLOG);
		proxyMonitor.setMonitorId(UUID.randomUUID().toString());
		LogPolicyBean logPolicyBean2 = new LogPolicyBean();
		logPolicyBean2.setLogPath("/usr/sjes/Web70/https-http-agent/logs/access");
		logPolicyBean2.setThreadHold(60);
		proxyMonitor.setItem(JSON.toJSONString(logPolicyBean2));
		monitorItems.add(proxyMonitor);

		MonitorTaskPools.getInstances().setMonitorItems(config.getDeviceId(),monitorItems);
		/*String url = config.getRequestUrl() + config.getDeviceId();
		HttpClientUtils.getMethod(url,null,AmTokenPools.getInstances().getCookie(),response -> {

			if(!ObjectUtils.isEmpty(response)){ //响应信息不为null，解析信息，并更新
				String content = null;
				try {
					content = EntityUtils.toString(response.getEntity(),HttpClientUtils.ENCODING);
					List<MonitorItem> monitorItems = JSON.parseArray(content,MonitorItem.class);
					MonitorTaskPools.getInstances().setMonitorItems(config.getDeviceId(),monitorItems);
				}
				catch (IOException e) {
					logger.error("content {} parse error",content);
					logger.error(e.getMessage(),e);
				}
			}
			return null;
		});*/
	}


	/**
	 * 20秒上报一次数据
	 */
	@Scheduled(fixedRate = 20 * 1000)
	@Async
	public void reportItems(){

		Collection<MonitorItem> monitorItems = MonitorTaskPools.getInstances().getMonitorItems(config.getDeviceId());
		if(ObjectUtils.isEmpty(monitorItems)){

			logger.warn("monitor items are empty and do nothing ");
			return ;
		}

		List<ReportItem> reportItems = Lists.newArrayList();
		monitorItems.parallelStream().forEach(monitorItem -> {

			Optional<MonitorHandler> optionalMonitorHandler = MonitorHandlerPools.getInstance().getHandler(monitorItem.getMonitorItemType());
			if(optionalMonitorHandler.isPresent()){

				ReportItem reportItem = optionalMonitorHandler.get().handler(monitorItem);
				reportItems.add(reportItem);
			}

		});
		String url = config.getReportUrl();
		Reports reports = new Reports();
		reports.setDeviceId(config.getDeviceId());
		reports.setReportTime(new Date());
		reports.setReports(reportItems);
		Map<String,String> params = Maps.newHashMap();
		params.put("reports",JSON.toJSONString(reports));
		if(logger.isDebugEnabled()){

			logger.debug("reports is {}" , JSON.toJSONString(reports));
		}
		HttpClientUtils.postMethod(url,params,AmTokenPools.getInstances().getCookie(),response -> {

			if(ObjectUtils.isEmpty(response)){ //响应信息不为null，解析信息，并更新

				logger.error("post reports error -> {}",response.getStatusLine().getStatusCode());
			}else{

				try {

					EntityUtils.consume(response.getEntity());
					if(logger.isDebugEnabled()){

						logger.debug("post reports success");
					}
				}catch (IOException e) {

					logger.error(e.getMessage(),e);
				}
			}
			return null;
		});
	}

	/**
	 * 每天凌晨执行一次，拷贝WebProxy40证书文件到临时目录
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void copyWebProxy40CertFiles(){

		if(CertDBPathPools.getInstances().isEmpty()){

			if(logger.isDebugEnabled()){

				logger.debug("no cert path info found");
			}
			return ;
		}
		CertDBPathPools.getInstances().getAll().entrySet().stream().forEach(entry -> {

			FileReaderUtils.copyCertFiles(config.getDeviceId(),config.getWebProxy40CertPath(),entry.getKey());
		});

	}
}
