package cablegate.infrastructure;

import org.apache.commons.lang3.SystemUtils;


public class SystemConfig {
	
	private static String archiveDirectory = null;
	private static long javaRam = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
	private static final String WORKING_FOLDER = "CableGateFiles";
	
	public static String getArchiveDirectory() {
		return archiveDirectory;
	}

	public static void setArchiveDirectory(String archiveDirectory) {
		SystemConfig.archiveDirectory = archiveDirectory;
	}
	
	public static long getJavaRam(){
		return javaRam;
	}
	
	public static String getWorkingDirectory(){
		String path = SystemUtils.getUserDir().getAbsolutePath();
		if(SystemUtils.IS_OS_WINDOWS){
			path += ('\\' + WORKING_FOLDER + '\\');
		}else {
			path += ('/' + WORKING_FOLDER + '/');
		}
		return path;
	}
}
