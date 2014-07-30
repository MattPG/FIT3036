package window.attempt_1;

import javax.swing.*;
import java.sql.*;

public class Main {
	  private static JFrame createFrame(String windowTitle){
		  SystemConfig systemConfig = new SystemConfig();
		  
		  	JFrame window = new JFrame(windowTitle);
		  	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    window.setSize(
		    		(int)systemConfig.getScreenWidth(),
		    		(int)systemConfig.getScreenHeight()
		    		); // default size is 0,0

	        //Add the ubiquitous "Hello World" label.
	        JLabel label = new JLabel("Hello World");
	        window.getContentPane().add(label);
		    
		    //window.pack();
		    window.setVisible(true);
		    
		    return window;
	  }
	  
	  public static void main(String[] args) {
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                JFrame frame = createFrame("My First Window!");
	            }
	        });
	  }	  
}