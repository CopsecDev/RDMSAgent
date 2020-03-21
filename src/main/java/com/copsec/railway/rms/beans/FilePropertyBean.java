package com.copsec.railway.rms.beans;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Data;

@Data
public class FilePropertyBean {

	private String monitorId;

	private long lastModified = 0L;

	private long position = 0L;

	private ConcurrentHashMap<String, AtomicLong> urlMaps = new ConcurrentHashMap<>();

}
