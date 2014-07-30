package window.attempt_1;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.swing.JOptionPane;

public class SystemConfig {
	
	private final String cablesDirectory = "D:\\Users\\Matthew\\Downloads\\";
	private final String cableFileName = "cables.csv";
	private final File cableFile;
	
	private final Dimension screenSize;
	private final double width;
	private final double height;
	
	
	public SystemConfig(){	
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = screenSize.getWidth();
		height = screenSize.getHeight();	
		cableFile = new File(cablesDirectory + cableFileName);
	}
	
	public FileReader getCableStream(){
		FileReader file;
		try{
			file = new FileReader(cableFile);
		}catch(FileNotFoundException noFile){
			SystemConfig.criticalError("SystemConfig", "File not found");
		}
	}
	
	public double getScreenWidth(){
		return width;
	}
	
	public double getScreenHeight(){
		return height;
	}
	
	public void criticalError(String currentClass, String error){
		createDialog(currentClass + error);		
	}
}
