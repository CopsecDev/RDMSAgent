package com.copsec.railway.rms.certUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * 用户获取证书内容
 */
public class CertUtils {


	public static void main(String[] args) throws Exception{

		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		FileInputStream input = new FileInputStream(new File("C:\\Users\\Copsec\\Desktop\\证书实例\\https-http-agent\\config\\cert8.db"));

		Certificate c = cf.generateCertificate(input);

	}
}
