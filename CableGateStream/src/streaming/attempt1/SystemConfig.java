package streaming.attempt1;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class SystemConfig {
	
	//private static final String ERROR_FRAME_TITLE = "ERROR!";
	private final String CABLES_DIRECTORY = "D:\\Users\\Matthew\\Downloads\\";
	//private final String CABLES_DIRECTORY = "\\\\ad.monash.edu\\home\\User009\\mpgre4\\Documents\\Documents\\Uni Work\\FIT3036\\";
	private final String CABLES_FILENAME = "cables.csv";
	
	private static File cableFile = null;
	
	private final Dimension screenSize;
	private final int width;
	private final int height;	
	
	public SystemConfig(){	
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();	
		cableFile = new File(CABLES_DIRECTORY + CABLES_FILENAME);
	}
	
	public FileReader getCableStream(){
		FileReader file = null;
		try{
			file = new FileReader(cableFile);
		}catch(FileNotFoundException noFile){
			criticalError("SystemConfig", "File not found");
		}
		return file;
	}
	
	public int getScreenWidth(){
		return width;
	}
	
	public int getScreenHeight(){
		return height;
	}
	
	public static void criticalError(String currentClass, String error){
		System.out.println("Error in " + currentClass + ".\n" + "Reason: " + error);
		System.exit(0);
	}
}
