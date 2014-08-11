package cablegate.stream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.profiler.Profiler;

public class Main {

	private static final int CABLE_CHUNK = 10000;
	private static final int STRING_BUILDER_INIT_SIZE = CABLE_CHUNK * 7000; // 7k chars ea. (avg is 6.3k)
	
	private static BufferedReader stream;
	
	public static void main(String[] args) {		
		
		getCableStream();
		
		// Temp variables for iterations
		String currLine;
		int cableCount;
		// Stringbuilder for appending each line optimally
		StringBuilder cables = null;
		// A regex that detects the start of a new cable
		Matcher matcher = Pattern.compile("\"[0-9]+\"").matcher("");
		// A cached thread pool for executing each cable chunk
    	ThreadPoolExecutor threadPool = (new ThreadPoolExecutor(0, Integer.MAX_VALUE, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()));
		
    	Profiler timer;
    	try{
			currLine = readLine();
			do{		
				cables = new StringBuilder(STRING_BUILDER_INIT_SIZE);
		    	timer = new Profiler("Reading Cables.csv").startNested("A");
				for(cableCount = 0; cableCount < CABLE_CHUNK; cableCount++){
					
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
				}
		    	// Send off this cable chunk to be parsed
				threadPool.execute(new ParseCSVTask(cables));
				timer.stop().print();
			}while(currLine != null);
    	}finally{ 
    		if(stream != null)
	    		try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
	}
		
	private static String readLine(){
		String currLine = null;
		
		try {
			currLine = stream.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return currLine;
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
