package streaming.attempt1;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class CSVStreamer {

	private static SystemConfig system;
	private BufferedReader stream;
	private JFrame window;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private StringReader stringReader;
	
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
	
	private class StringReader extends SwingWorker<Void, String> {
	    @Override
	    protected Void doInBackground() {	    		
    		for(int i=0; i<1000; i++)
				try {
					publish(stream.readLine());
				} catch (IOException e) {
					SystemConfig.criticalError("StringReader", "Can't read der next line");
				}
	        return null;
	    }
	
	    @Override
	    protected void process(List<String> toPrint) {
	    	Iterator<String> iterator = toPrint.iterator();
	    	while(iterator.hasNext())
	    		textArea.append(iterator.next());
	    }
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               CSVStreamer csv = new CSVStreamer();
            }
        });
		
		
		
	}

}
