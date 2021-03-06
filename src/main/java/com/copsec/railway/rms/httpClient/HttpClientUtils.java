package com.copsec.railway.rms.httpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.NoRouteToHostException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.copsec.railway.rms.common.CopsecResult;
import com.copsec.railway.rms.sigontanPools.AmTokenPools;
import com.google.common.collect.Lists;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.ObjectUtils;

public class HttpClientUtils {

	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

	private static final int connectionTimeOut = 5*1000;

	private static final int socketTimeOut = 5*1000;

	public static final String ENCODING = "UTF-8";

	private static final Integer max_total =512;

	private static final Integer max_per_route = 50;

	private static final Integer req_timeout = 5*1000;

	public static BasicCookieStore cookieStore = new BasicCookieStore();

	private static Registry<ConnectionSocketFactory> socketFactoryRegistry;

	private static PoolingHttpClientConnectionManager connectionManager;

	static {

		SSLContext sslContext = mySSLContext();

		socketFactoryRegistry = RegistryBuilder.
				<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1", "TLSv1.2" }, null, new HostnameVerifier() {
					@Override
					public boolean verify(String s, SSLSession sslSession) {
						s = "eaccess.china-railway.com.cn";
						return SSLConnectionSocketFactory.getDefaultHostnameVerifier().verify(s, sslSession);
					}
				})).build();

		connectionManager =
				new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connectionManager.setMaxTotal(max_total);
		connectionManager.setDefaultMaxPerRoute(max_per_route);
	}

	private static SSLContext mySSLContext() {

		SSLContext context = null;
		try {
			context = SSLContext.getInstance("SSLv3");
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};
		try {
			context.init(null, new TrustManager[] { trustManager }, null);
		}
		catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return context;
	}

	private static CloseableHttpClient getHttpClient() {

		return HttpClients.custom()
				.setConnectionManager(connectionManager)
				.setDefaultCookieStore(cookieStore).build();
	}

	private static CopsecResult execute(CloseableHttpClient httpClient, CloseableHttpResponse httpResponse,
			HttpRequestBase httpMethod, Function<CloseableHttpResponse, CopsecResult> function) throws
			Exception {

		httpResponse = httpClient.execute(httpMethod);
		if (!ObjectUtils.isEmpty(httpResponse) && !ObjectUtils.isEmpty(httpResponse.getStatusLine())) {

			if (!ObjectUtils.isEmpty(httpResponse.getEntity()) && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				return function.apply(httpResponse);
			}else{

				httpMethod.abort();
			}
		}else{

			httpMethod.abort();
		}
		return function.apply(null);
	}

	public static CopsecResult postMethod(String url, Cookie cookie, Function<CloseableHttpResponse, CopsecResult> function) {

		return postMethod(url, null, cookie, function);
	}

	/**
	 *
	 * @param url 连接地址
	 * @param param 参数信息
	 * @param cookie cookie值
	 * @param function 解析函数
	 * @return
	 */
	public static CopsecResult postMethod(String url, Map<String, String> param, Cookie cookie,
			Function<CloseableHttpResponse, CopsecResult> function) {

		CloseableHttpClient httpClient = getHttpClient();

		HttpPost post = new HttpPost(url);

		RequestConfig requestConfig = RequestConfig.custom().
				setConnectionRequestTimeout(connectionTimeOut).setSocketTimeout(socketTimeOut).setConnectionRequestTimeout(req_timeout).build();

		post.setConfig(requestConfig);
		CloseableHttpResponse httpResponse = null;
		try {

			packageParam(param, post);

			post.addHeader(new BasicHeader("Cookie","iPlanetDirectoryPro="+AmTokenPools.getInstances().getToken()));

			return execute(httpClient, httpResponse, post, function);

		}
		catch (Throwable t) {

			logger.error(t.getMessage(), t);

		}
		finally {

			if (!ObjectUtils.isEmpty(httpResponse)) {

				try {
					httpResponse.close();
				}
				catch (IOException e) {

					logger.error(e.getMessage(), e);
				}
			}
		}
		return null;
	}

	public static CopsecResult getMethod(String url, Map<String, String> param, Cookie cookie,
			Function<CloseableHttpResponse, CopsecResult> function) {

		CloseableHttpClient httpClient = getHttpClient();

		URIBuilder uriBuilder = null;
		try {
			uriBuilder = new URIBuilder(url);
		}
		catch (URISyntaxException e) {

			logger.error(e.getMessage(), e);
		}
		if (param != null) {
			Set<Entry<String, String>> entrySet = param.entrySet();
			for (Entry<String, String> entry : entrySet) {
				uriBuilder.setParameter(entry.getKey(), entry.getValue());
			}
		}
		HttpGet get = null;
		try {
			get = new HttpGet(uriBuilder.build());
		}
		catch (URISyntaxException e) {

			logger.error(e.getMessage(), e);
		}

		RequestConfig requestConfig = RequestConfig.custom().
				setConnectionRequestTimeout(connectionTimeOut).setSocketTimeout(socketTimeOut).build();

		get.setConfig(requestConfig);

		CloseableHttpResponse httpResponse = null;
		try {

			get.addHeader(new BasicHeader("Cookie","iPlanetDirectoryPro="+AmTokenPools.getInstances().getToken()));
			//packageCookie(cookie);
			return execute(httpClient, httpResponse, get, function);
		}
		catch (Throwable t) {

			logger.error(t.getMessage(), t);
			if (t instanceof NoRouteToHostException) {

				return CopsecResult.failed("无法访问远程主机");
			}
		}
		finally {

			if (!ObjectUtils.isEmpty(httpResponse)) {

				try {
					httpResponse.close();
				}
				catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return null;
	}

	public static CopsecResult getMethod(String url, Cookie cookie, Function<CloseableHttpResponse, CopsecResult> function) {

		return getMethod(url, null, cookie, function);
	}

	public static void packageParam(Map<String, String> params, HttpEntityEnclosingRequestBase httpMethod)
			throws UnsupportedEncodingException {
		// 封装请求参数
		List<NameValuePair> nvps = Lists.newArrayList();
		if (!ObjectUtils.isEmpty(params)) {

			params.entrySet().stream().forEach(entry -> {

				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			});
			httpMethod.setEntity(new UrlEncodedFormEntity(nvps, ENCODING));
		}
	}

	public static void packageHeader(Map<String, String> params, HttpRequestBase httpMethod) {
		// 封装请求头
		if (!ObjectUtils.isEmpty(params)) {

			params.entrySet().stream().forEach(entry -> {

				httpMethod.setHeader(entry.getKey(), entry.getValue());
			});
		}

	}

	public static void packageCookie(Cookie cookie) {

		if (!ObjectUtils.isEmpty(cookie)) {

			if(logger.isDebugEnabled()){

				logger.debug("package cookie -> {}", Objects.toString(cookie));
			}
			cookieStore.addCookie(cookie);
		}
	}
}
