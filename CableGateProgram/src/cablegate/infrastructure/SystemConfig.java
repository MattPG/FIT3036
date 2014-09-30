package cablegate.infrastructure;

import java.io.File;

public class SystemConfig {
	
	private static final int NUMBER_OF_CPU_CORES = Runtime.getRuntime().availableProcessors();
	private static final int SCREEN_WIDTH = 0;
	private static final int SCREEN_HEIGHT = 0;
	private static File databaseFile = null;
	private static String databaseDirectory = null;
	private static String archiveDirectory = null;
	
	public SystemConfig(){} // Please don't call this!
	
	public static int getNumberOfCpuCores() {
		return NUMBER_OF_CPU_CORES;
	}

	public static int getScreenWidth() {
		return SCREEN_WIDTH;
	}

	public static int getScreenHeight() {
		return SCREEN_HEIGHT;
	}

	public static String getDatabaseDirectory() {
		return databaseDirectory;
	}

	public static void setDatabaseDirectory(String dataBaseDirectory) {
		SystemConfig.databaseDirectory = dataBaseDirectory;
		databaseFile = new File(dataBaseDirectory);
	}
	
	public static String getArchiveDirectory() {
		return archiveDirectory;
	}

	public static void setArchiveDirectory(String archiveDirectory) {
		SystemConfig.archiveDirectory = archiveDirectory;
	}

	public static boolean databaseExists(){
		boolean dbExists = false;
		if(databaseFile != null){
			dbExists = databaseFile.exists();
		}
		return dbExists;
	}
}
