package cablegate.stream;

public class SystemConfig {
	
	private static final String CURRENT_USER = System.getProperty("user.name");
	private static String CABLES_DIRECTORY_UNI = "\\\\ad.monash.edu\\home\\User009\\mpgre4\\Documents\\Documents\\";
	private static String CABLES_DIRECTORY_HOME = "D:\\Users\\Matthew\\Documents\\";
	private static String CABLES_FILE_NAME = "cables.csv";
	
	public SystemConfig(){	
	}
	
	public static String getCurrentUser(){
		return CURRENT_USER;
	}
	
	public static String getCableDirectory(){
		String directory = null;
		
		if(CURRENT_USER.equals("Matthew"))
			directory = CABLES_DIRECTORY_HOME + CABLES_FILE_NAME;
		else if(CURRENT_USER.equals("Matthew Paul Greenwood"))
			directory = CABLES_DIRECTORY_UNI + CABLES_FILE_NAME;
		
		return directory;
	}
}
