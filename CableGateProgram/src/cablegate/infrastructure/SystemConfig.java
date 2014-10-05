package cablegate.infrastructure;


public class SystemConfig {
	
	private static String archiveDirectory = null;
	private static long javaRam = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
			
	public static String getArchiveDirectory() {
		return archiveDirectory;
	}

	public static void setArchiveDirectory(String archiveDirectory) {
		SystemConfig.archiveDirectory = archiveDirectory;
	}
	
	public static long getJavaRam(){
		return javaRam;
	}
}
