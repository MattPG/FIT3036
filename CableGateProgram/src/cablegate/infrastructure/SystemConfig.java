package cablegate.infrastructure;

import java.io.File;

public class SystemConfig {
	
	private static File databaseFile = null;
	private static String databaseDirectory = null;
	private static String archiveDirectory = null;
		

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
			dbExists = databaseFile.isDirectory();
		}
		return dbExists;
	}
}
