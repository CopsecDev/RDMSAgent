package com.copsec.railway.rms.sigontanPools;


import java.util.Date;

import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

public class AmTokenPools {

	private static AmTokenPools pools =null;
	private AmTokenPools(){}

	public static volatile  String tokenId = null;

	public synchronized static AmTokenPools getInstances(){

		if(pools == null){

			synchronized (AmTokenPools.class){

				if(pools == null){

					pools = new AmTokenPools();
				}
			}
		}
		return pools;
	}

	public String getToken(){

		return tokenId;
	}

	public void setToken(String newTokenId){

		tokenId = newTokenId;
	}

	public Cookie getCookie(){

		BasicClientCookie cookie = new BasicClientCookie("iPlanetDirectoryPro",tokenId);
		cookie.setPath("/");
		cookie.setDomain(".china-railway.com.cn");
		cookie.setAttribute(ClientCookie.MAX_AGE_ATTR,"0");
		cookie.setSecure(true);
		return cookie;
	}
}
