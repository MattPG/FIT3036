package streaming.attempt1;

import java.io.BufferedReader;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class CSVStreamer {

	private static SystemConfig system;
	private static BufferedReader stream;
	private JFrame window;
	private static JTextArea textArea;
	private JScrollPane scrollPane;

	CountDownLatch latch = new CountDownLatch(1);
	
	private CSVStreamer(){  
		system = new SystemConfig();
		stream  = new BufferedReader(system.getCableStream());
				
		// Create the window
		window = new JFrame("Cablegate Reader");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create and add textArea to window
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane = new JScrollPane(textArea);
		window.add(scrollPane);
		window.setSize(system.getScreenWidth(), system.getScreenHeight());
		
		window.setVisible(true);	
  }
	
	public static void main(String[] args) {
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	        	new CSVStreamer();
	        	new StreamParser(stream, textArea, StreamParser.OPERATION.COUNT_CABLES).execute();
	        }
	    });		
		
		
		
	}

}
