package window.attempt_1;

import java.awt.Dimension;
import java.awt.Toolkit;

public class SystemConfig {
	
	private Dimension screenSize;
	private double width;
	private double height;
	
	public SystemConfig(){	
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = screenSize.getWidth();
		height = screenSize.getHeight();		
	}
	
	public double getScreenWidth(){
		return width;
	}
	
	public double getScreenHeight(){
		return height;
	}
}
