package com.copsec.railway.rms.configurations;


import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:config.conf"},encoding = "UTF-8")
@Data
public class CopsecConfigurations {

	@Value("${rms.agent.deviceId}")
	private String deviceId;

	@Value("${rms.agent.deploy.location}")
	private String location;

	@Value("${rms.agent.request.path}")
	private String requestUrl;

	@Value("${rms.agent.report.path}")
	private String reportUrl;

	@Value("${rms.agent.keystore.path}")
	private String keystorePath;

	@Value("${rms.agent.keystore.pwd}")
	private String keystorePwd;

	@Value("${rms.agent.nickname}")
	private String nickname;

	@Value("${rms.agent.base.dn}")
	private String baseDn;

	@Value("${rms.agent.authorization.path}")
	private String authorizationPath;

	@Value("${rms.agent.system.type}")
	private String systemType;

	@Value("${rms.agent.webProxy40.cert.tmp.path}")
	private String webProxy40CertPath;

	@Value("${rms.report.info.device.Ip}")
	private String deviceIp;

	@Value("${rms.report.info.device.vendor}")
	private String vendor;

	@Value("${rms.report.info.device.production.name.am}")
	private String productionNameOfAm;

	@Value("${rms.report.info.device.production.am.path}")
	private String amPath;

	@Value("${rms.report.info.device.production.am.format}")
	private String logFormat;

	@Value("${rms.report.info.device.production.name.proxy}")
	private String productionNameOfPorxy;

	@Value("${rms.report.info.device.production.type}")
	private String productionType;

	@Value("${rms.report.info.syslog.Ip}")
	private String syslogIp;

	@Value("${rms.report.info.syslog.port}")
	private int syslogPort;

	@Value("${rms.report.am.enable}")
	private boolean enableReportForAm;

	@Value("${rms.report.porxy.enable}")
	private boolean enableReportForProxy;

	@Value("${rms.agent.pid.path}")
	private String pidPath;
}
