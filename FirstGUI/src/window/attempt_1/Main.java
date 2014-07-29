package window.attempt_1;

import javax.swing.*;

public class Main {
	  public static void main(String[] args) {
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createFrame("My First Window!");
	            }
	        });
	  }
	  
	  private static void createFrame(String windowTitle){
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
	  }
}