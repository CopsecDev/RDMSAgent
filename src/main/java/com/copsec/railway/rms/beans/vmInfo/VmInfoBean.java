package com.copsec.railway.rms.beans.vmInfo;

import java.util.List;

import lombok.Data;

@Data
public class VmInfoBean {

	private List<DiskInfo> diskInfos;

	private List<DomainInfo> domainInfos;

	private List<ControllerInfo> controllerInfos;

	private List<VolumeInfo> volumeInfos;
}
