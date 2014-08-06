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

public class Main {

	private static final int CABLE_CHUNK = 1000;
	private static final int STRING_BUILDER_INIT_SIZE = 5000000; // 1000 cables @ 5000 chars ea.
	
	private static BufferedReader stream;
	private Reader reader;
	
	
	public static void main(String[] args) {		
		getCableStream();
		
		StringBuilder cables = null;
		Matcher matcher;
		String currLine;
		int cableCount;

		matcher = Pattern.compile("\"[0-9]+\"").matcher("");

		currLine = readLine();
		cables = new StringBuilder(STRING_BUILDER_INIT_SIZE);
		for(cableCount = 0; cableCount < CABLE_CHUNK; cableCount++){
			
			// Get the first line which definitely matches the pattern
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
		
    	// Create a thread pool to parse all the individual cables and extract information
    	ThreadPoolExecutor threadPool = (new ThreadPoolExecutor(0, Integer.MAX_VALUE, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()));
		threadPool.execute(new ParseCSVTask(new StringReader(cables.toString())));
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
