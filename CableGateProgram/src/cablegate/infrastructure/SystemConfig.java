package cablegate.infrastructure;


public class SystemConfig {
	
	private static String archiveDirectory = null;
	private static int javaRam = (int) (Runtime.getRuntime().maxMemory() / (1024^2)) - 500; // ram in MB
			
	public static String getArchiveDirectory() {
		return archiveDirectory;
	}

	public static void setArchiveDirectory(String archiveDirectory) {
		SystemConfig.archiveDirectory = archiveDirectory;
	}
	
	public static int getJavaRam(){
		return javaRam;
	}
}
