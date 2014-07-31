package streaming.attempt1;

import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class CSVStreamer {

	private static SystemConfig  system = new SystemConfig();
	
	private static void createMainFrame(){  
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	    		JFrame window = new JFrame("Cablegate Reader");
		  		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  		window.setSize(
		  			system.getScreenWidth(),
		    		system.getScreenHeight()
		    		); // default size is 0,0
	
		  		//Add the ubiquitous "Hello World" label.
		  		JTextArea textArea = new JTextArea();
		  		window.getContentPane().add(textArea);
		  		window.getContentPane().
		  		window.setVisible(true);
	        }
	    });  
		  
  }

	public static void main(String[] args) {
		
		BufferedReader stream = new BufferedReader(system.getCableStream());
		
		createMainFrame();
		
		for(int i=0; i<1000; i++)
			try {
				System.out.println(stream.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}
