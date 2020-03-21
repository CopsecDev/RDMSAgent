package com.copsec.railway.rms.beans.vmInfo;

import lombok.Data;

@Data
public class DiskInfo {

	private String id;
	private String raidId;
	private String status;
	private String spare;
	private String type;
	private String media;
	private String chassis;
	private String slot;
	private String size;

}
