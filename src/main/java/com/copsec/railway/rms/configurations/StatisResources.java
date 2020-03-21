package com.copsec.railway.rms.configurations;

import java.util.UUID;

public class StatisResources {

	public static final String Location_inner = "inner";

	public static final String location_outter = "outer";

	public static final int status_normal = 1;

	public static final int status_error = 0;

	public static final String web70_cert_path = "/usr/sjes/Web70/bin/certutil -L -d ";

	public static final String webProxy40_cert_path = "/usr/sjes/WebProxy40/alias/";

	public static final String server_config_path = "config/server.xml";

	public static final String default_value = "N/A";

	public static final String system_type_linux = "linux";

	public static final String system_type_solaris = "solaris";

	public static final String line_spliter =  "<;ws;>";

	public static final String copied_file_path = "/tmp/log." + UUID.randomUUID() + ".log";

}
