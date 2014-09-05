package cablegate.infrastructure;

public class SystemConfig {
	
	private static final String HOME_USER_NAME = "Matthew";
	private static final String UNI_USER_NAME = "mpgre4";	
	private static final String CURRENT_USER = System.getProperty("user.name");
	private static final String CABLES_DIRECTORY_UNI = "\\\\ad.monash.edu\\home\\User009\\mpgre4\\Documents\\Documents\\";
	private static final String CABLES_DIRECTORY_HOME = "D:\\Users\\Matthew\\Documents\\";
	private static final String CABLES_FILE_NAME = "cables.csv";
	private static final int NUMBER_OF_CPU_CORES = Runtime.getRuntime().availableProcessors();
	private static final int SCREEN_WIDTH = 0;
	private static final int SCREEN_HEIGHT = 0;
	
	public SystemConfig(){	
	}
	
	public static int getNumberOfCpuCores() {
		return NUMBER_OF_CPU_CORES;
	}

	public static int getScreenWidth() {
		return SCREEN_WIDTH;
	}

	public static int getScreenHeight() {
		return SCREEN_HEIGHT;
	}

	public static String getCurrentUser(){
		return CURRENT_USER;
	}
	
	public static String getCableDirectory(){
		String directory = null;

		if(CURRENT_USER.equals(HOME_USER_NAME))
			directory = CABLES_DIRECTORY_HOME + CABLES_FILE_NAME;
		else if(CURRENT_USER.equals(UNI_USER_NAME))
			directory = CABLES_DIRECTORY_UNI + CABLES_FILE_NAME;
		
		return directory;
	}
}
