package cablegate.stream;

public class SystemConfig {
	
	private static String CABLES_DIRECTORY_UNI = "\\\\ad.monash.edu\\home\\User009\\mpgre4\\Documents\\Documents\\";
	private static String CABLES_FILE_NAME = "cables.csv";
	
	public SystemConfig(){	
	}
	
	public static String getCableDirectory(){
		return CABLES_DIRECTORY_UNI + CABLES_FILE_NAME;
	}
}
