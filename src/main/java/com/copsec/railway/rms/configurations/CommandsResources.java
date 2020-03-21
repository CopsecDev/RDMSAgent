package com.copsec.railway.rms.configurations;

/**
 * 存储linux 和solaris系统命令
 */
public class CommandsResources {

	/**
	 * 检索线程是否存在
	 * @param pid
	 * @return
	 */
	public static String prcessorBuilder(String pid){

		return "/usr/bin/ps -p " + pid + " | /usr/bin/grep -v PID | /usr/bin/wc -l";
	}

	public static final String system_type_cmd = "/usr/bin/uname";

	public static final String linux_user_cmd = "/bin/cat /etc/shadow | /usr/bin/awk '{print $1,$2,$3,$5}'";

	public static final String solaris_user_cmd = "/usr/bin/passwd -sa |  /usr/bin/awk '{print $1,$2,$3,$5}'";

	public static final String linux_cpu_ratio_cmd = "/usr/bin/vmstat 1 3 | /usr/bin/tail -1| /bin/grep -v cpu | /bin/grep -v id | /usr/bin/awk '{print $NF}'";

	public static final String solaris_cpu_ratio_cmd = "/usr/bin/vmstat -q 1 3 | /usr/bin/tail -1| /usr/bin/grep -v cpu | /usr/bin/grep -v id | /usr/bin/awk '{print 100 - $NF}'";

	public static final String linux_disk_cmd = "/bin/df -hk |/bin/grep -v Mounted |  /usr/bin/awk '{print $4,$5,$6}'";

	public static final String solaris_disk_cmd = "/usr/sbin/df -k |/usr/bin/grep -v Mounted |  /usr/bin/awk '{print $5,$6}'";

	public static final String solaris_memeory_total_cmd = "/usr/sbin/prtconf  | /usr/bin/grep Mem | /usr/bin/awk -F: '{print $2}'";

	public static final String solaris_memory_free_cmd = "/usr/bin/vmstat -q 1 1 | /usr/bin/grep -v cpu | /usr/bin/grep -v free | /usr/bin/awk '{print $5}'";

	public static final String linux_memory_total_cmd = "/bin/cat /proc/meminfo |/bin/grep MemTotal |/usr/bin/awk '{print $2,$3}'";

	public static final String linux_memory_buffered_cmd = "/bin/cat /proc/meminfo |/bin/grep Buffers |/usr/bin/awk '{print $2,$3}'";

	public static final String linux_meory_cached_cmd = "/bin/cat /proc/meminfo |/bin/grep Cached | /bin/grep -v SwapCached |/usr/bin/awk '{print $2,$3}'";

	public static final String linux_memory_free_cmd = "/bin/cat /proc/meminfo |/bin/grep MemFree |/usr/bin/awk '{print $2,$3}'";

	public static String getLinuxPingCmd(String ip){

		return "/bin/ping " + ip+ " | echo $?";
	}

	public static String getSolairsPingCmd(String ip){

		return "/usr/sbin/ping " + ip + " 5 >/dev/null 2>&1 && /usr/bin/echo $?";
	}

	public static String getProcessCmd(String dName){

		return "/usr/bin/ps -ef | /usr/bin/grep "+ dName+" | /usr/bin/wc -l ";
	}

	public static final String ldm_cmd = "ldm list -p";

	public static final String raidconfig_cmd = "raidconfig list all";

	public static final String linux_version_cmd = "/usr/bin/uname -r";

	public static final String solaris_version_cmd = "/usr/bin/showrev";

	public static final String solaris_patch_cmd = "/usr/bin/showrev -p";

	public static final String linux_patch_cmd = "";

	public static String getWeb70CertInfoWithNickname(String instanceName,String nickname){

		return StatisResources.web70_cert_path +  instanceName + "/config/ -n " + nickname;
	}

	public static String getAllWeb70CertNicknames(String instanceName){

		return StatisResources.web70_cert_path + instanceName + "/config/";
	}
}
