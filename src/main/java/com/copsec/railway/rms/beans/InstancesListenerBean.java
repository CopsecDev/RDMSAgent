package com.copsec.railway.rms.beans;

import java.util.List;

import lombok.Data;

@Data
public class InstancesListenerBean {

	private List<Integer> ports;

	private String message;
}
