package com.copsec.railway.rms.beans.vmInfo;


import lombok.Data;

@Data
public class VolumeInfo {

	private String id;
	private String name;
	private String device;
	private String status;
	private String level;
	private String numDisks;
	private String size;
}
