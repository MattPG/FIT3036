package streaming.attempt1;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseTask implements Callable<Void>{
	
	private final BlockingQueue<List<String>> cableQueue;
	private final BufferedReader stream;
	private final Matcher matcher;

	private int count = 0, cablecount = 1;
	private List<String> currCable;
	
	public ParseTask(BufferedReader stream, BlockingQueue<List<String>> queue){		
		this.stream = stream;
		cableQueue = queue; 
		matcher = Pattern.compile("\"[0-9]+\"").matcher("");
		currCable = null;
	}
	
	@Override
	public Void call() throws Exception {
		String currLine = readLine();
		while(currLine != null){
			// Start  saving the cable
			currCable = new LinkedList<String>();
			currCable.add(currLine);
			
			// Add lines until we hit the next cable (or end of file)
			currLine = readLine();
			while(currLine != null && !matcher.reset(currLine).lookingAt()){
				currCable.add(currLine);
				currLine = readLine();		

			}

			if(cablecount%10000 == 0)
				System.out.println(cablecount);	
			cablecount++;
			
			// Send cable off to be processed			
			cableQueue.put(currCable);			
		}
		
		System.out.println(count);
		
		return null;
	}

	private String readLine(){
		count++;
		String currLine = null;
		
		try {
			currLine = stream.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return currLine;
	}
}
