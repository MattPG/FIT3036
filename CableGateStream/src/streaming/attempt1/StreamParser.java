package streaming.attempt1;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class StreamParser extends SwingWorker<Void, String> {
	
	public enum OPERATION {
		PRINT_TO_TEXTAREA, COUNT_CABLES
	}
	
	BufferedReader stream;
	JTextArea textArea;
	int numLinesToRead;
	OPERATION operation;
	
	public StreamParser(BufferedReader stream, JTextArea textArea, int numLinesToRead, OPERATION operation){
		this.stream = stream;
		this.textArea = textArea;
		this.numLinesToRead = numLinesToRead;
		this.operation = operation;
	}
	
	public StreamParser(BufferedReader stream, JTextArea textArea, OPERATION operation){
		this.stream = stream;
		this.textArea = textArea;
		this.numLinesToRead = 0;
		this.operation = operation;
	}
	
	private String readLine(){
		String currLine = null;
		try {
			currLine = stream.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return currLine;
	}
	
	@Override
	protected Void doInBackground() {	
		switch(operation){
			case PRINT_TO_TEXTAREA:{
				for(int i=0; i<numLinesToRead; i++)	{
					publish(readLine());
				}
				
				return null;
			}
		
			case COUNT_CABLES:{
				Pattern pattern = Pattern.compile("\"[0-9]+\"");
				Matcher matcher;
				int cableCount = 0;
				
				String currLine = readLine();			
				while(currLine != null){
					matcher = pattern.matcher(currLine);
					if(matcher.find()){
						publish(currLine);
						cableCount++;
					}
					currLine = readLine();
				}				
				publish(cableCount + "");
				
				return null;
			}
			default:
				return null;
		}
	}
	
	@Override
	protected void process(List<String> toPrint) {
		Iterator<String> iterator = toPrint.iterator();
		while(iterator.hasNext())
			textArea.append(iterator.next() + "\n");
	}
}

