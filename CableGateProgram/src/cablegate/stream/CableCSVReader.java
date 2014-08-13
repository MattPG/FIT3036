package cablegate.stream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CableCSVReader implements Callable<Integer>{

	private static final int CABLE_CHUNK = 50;
	private static final int STRING_BUILDER_INIT_SIZE = CABLE_CHUNK * 7000; // 7k chars ea. (avg is 6.3k)
	
	private static BufferedReader stream;
	private final ThreadPoolExecutor threadPool;
	private final BlockingQueue<CableBean> resultQueue;
	
	public CableCSVReader(ThreadPoolExecutor threadPool, BlockingQueue<CableBean> resultQueue) {
		this.threadPool = threadPool;
		this.resultQueue = resultQueue;
	}

	@Override
	public Integer call() {	
		
		// Temp variables for iterations
		String currLine;
		int cableCount, totalCableCount;
		// Stringbuilder for appending each line optimally
		StringBuilder cables = null;
		// A regex that detects the start of a new cable
		Matcher matcher = Pattern.compile("\"[0-9]+\"").matcher("");   	
		
		// Open up the buffered streamer
		getCableStream();
		
		totalCableCount = 0;
		try{
			currLine = readLine();
			while(currLine != null){		
				cables = new StringBuilder(STRING_BUILDER_INIT_SIZE);
		    	// Reset the cable counter
		    	cableCount = 0;
		    	while(currLine != null && cableCount < CABLE_CHUNK){
					
					// Append the line which is the start of the pattern
					if(cableCount > 0)
						cables.append('\n');
					cables.append(currLine);
		
					// Add lines until we hit the next cable (or end of file)
					currLine = readLine();
					while(currLine != null && !matcher.reset(currLine).lookingAt()){
						cables.append('\n');
						cables.append(currLine);
						currLine = readLine();	
					}
					
					// Finished reading a full cable, increment the counter
					cableCount++;
				}
		    	
		    	// Send off this cable chunk to be parsed
				threadPool.execute(new ParseCSVTask(cables, resultQueue));
				totalCableCount += cableCount;
			}
			
    	}finally{ 
    		// Make sure the stream is closed
    		if(stream != null)
	    		try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    	}

		return totalCableCount;
	}
		
	private static String readLine(){
		
		try {
			return stream.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return null;
	}	
	
	private static void getCableStream(){
		try {
			stream = new BufferedReader(
						new FileReader(
							SystemConfig.getCableDirectory()
							));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	

}
