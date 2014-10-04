package cablegate.infrastructure;


public class SystemConfig {
	
	private static String archiveDirectory = null;
			
	public static String getArchiveDirectory() {
		return archiveDirectory;
	}

	public static void setArchiveDirectory(String archiveDirectory) {
		SystemConfig.archiveDirectory = archiveDirectory;
	}
}
