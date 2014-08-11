package streaming.attempt1;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class SystemConfig {

	private static final String CURRENT_USER = System.getProperty("user.name");
	private static String CABLES_DIRECTORY_UNI = "\\\\ad.monash.edu\\home\\User009\\mpgre4\\Documents\\Documents\\";
	private static String CABLES_DIRECTORY_HOME = "D:\\Users\\Matthew\\Documents\\";
	private static String CABLES_FILE_NAME = "cables.csv";
	
	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static final int width = (int) screenSize.getWidth();
	private static final int height = (int) screenSize.getHeight();	
	
	public SystemConfig(){	
	}
	
	public static String getCableDirectory(){
		String directory = null;
		
		if(CURRENT_USER.equals("Matthew"))
			directory = CABLES_DIRECTORY_HOME + CABLES_FILE_NAME;
		else if(CURRENT_USER.equals("Matthew Paul Greenwood"))
			directory = CABLES_DIRECTORY_UNI + CABLES_FILE_NAME;
		
		return directory;
	}
	
	public static int getScreenWidth(){
		return width;
	}
	
	public static int getScreenHeight(){
		return height;
	}
	
	public static void criticalError(String currentClass, String error){
		System.out.println("Error in " + currentClass + ".\n" + "Reason: " + error);
		System.exit(0);
	}
}
