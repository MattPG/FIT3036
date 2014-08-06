package stickj;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Main {

	private static final String CABLES_DIRECTORY = "D:\\Users\\Matthew\\Downloads\\";
	private static final String CABLES_FILENAME = "cables.csv";
	private static final int CABLE_CHUNK = 1000;
	private static final int STRING_BUILDER_INITIAL_CHARACTER_COUNT = 5000000; // 1,000 cables @ 5,000 ea.
	private static BufferedReader reader;
	
	public static String getCablesDirectory(){
		return CABLES_DIRECTORY + CABLES_FILENAME;
	}
	
	public static void main(String[] args) {	

		StringBuilder currCable;
		Matcher matcher;
		String currLine;
		int cableCount;
		
		matcher = Pattern.compile("\"[0-9]+\"").matcher("");
		ThreadPoolExecutor threadPool = (new ThreadPoolExecutor(0, Integer.MAX_VALUE, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()));
		
		try {
			reader = new BufferedReader(new FileReader(getCablesDirectory()));
		} catch (FileNotFoundException e) {
			threadPool.shutdown();
			e.printStackTrace();
		}
		
		currLine = readLine();
		while(currLine != null){
			
			// Start saving the raw cables
			currCable = new StringBuilder(STRING_BUILDER_INITIAL_CHARACTER_COUNT);
			cableCount = 0;
			
			while(currLine != null && cableCount < CABLE_CHUNK){
				
				// Append line tracking the start of a new cable
				currCable.append(currLine);
				
				// Add lines until we hit the start of the next cable (or end of file)
				currLine = readLine();
				while(currLine != null && !matcher.reset(currLine).lookingAt()){
					currCable.append(currLine);
					currLine = readLine();		
				}
	
				cableCount++;				
			}
			
			// Send raw cables off to be parsed
			threadPool.execute(new ParseTask(currCable));
			currLine = null;
						
		}
				
	}
	
	private static String readLine(){
		String currLine = null;
		
		try {
			currLine = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return currLine;
	}
	
}
