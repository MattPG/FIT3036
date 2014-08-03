package streaming.attempt1;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MainWindow{

	private JFrame window;
	private static JTextArea textArea;
	private JScrollPane scrollPane;
	
	public MainWindow(){  				
	// Create the window
	window = new JFrame("Cablegate Reader");
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	// Create and add textArea to window
			textArea = new JTextArea();
			textArea.setEditable(false);
			scrollPane = new JScrollPane(textArea);
			window.add(scrollPane);
			window.setSize(SystemConfig.getScreenWidth(), SystemConfig.getScreenHeight());
			
			window.setVisible(true);	
	  }
		
	public static JTextArea getTextArea(){
		return textArea;
	}
	

}
