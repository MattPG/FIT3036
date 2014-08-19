package cablegate.stream;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CableCSVReader implements Callable<Void>{

	private static final int CABLE_CHUNK = 50;
	private static final int STRING_BUILDER_INIT_SIZE = CABLE_CHUNK * 7000; // 7k chars ea. (avg is 6.3k)
	
	private static BufferedReader stream;
	private final ThreadPoolExecutor threadPool;
	private final ExecutorService dataBaseWriter;
	private final BlockingQueue<CableBean> resultQueue;
	
	public CableCSVReader() {
		this.dataBaseWriter = Executors.newSingleThreadExecutor();
		this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(2*SystemConfig.getNumberOfCPUCores());
		this.resultQueue = new ArrayBlockingQueue<CableBean>(500);
	}

	@Override
	public Void call() {	
		
		createDBandCableTable();
		
		// Temp variables for iterations
		String currLine;
		int cableCount;
		// Stringbuilder for appending each line optimally
		StringBuilder cables = null;
		// A regex that detects the start of a new cable
		Matcher matcher = Pattern.compile("\"[0-9]+\"").matcher("");   	
		
		// Open up the buffered stream to cable.csv
		getCableStream();
		
		//Spawn the datbaseWriter
		Future<Void> dataBaseWriterFuture = dataBaseWriter.submit(new CableCSVDBWriter(resultQueue));
		
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
			}
			
    	}finally{ 
    		// Make sure the streams are closed
    		if(stream != null)
	    		try {
					stream.close();
					threadPool.shutdown();
					dataBaseWriter.shutdown();
				} catch (IOException e) {
					e.printStackTrace();
				}
    	}
		
		// Make sure the whole DB creation process is finished
		try {
			dataBaseWriterFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Return to main
		return null;
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
	
	private static void createDBandCableTable(){
		Connection con = DataBaseManager.getConnectionAndCreate(); // Connect to the database
		try{
			
			// Create Cable Table
			Statement createTableStatement = con.createStatement();
			createTableStatement.execute("CREATE TABLE " + DataBaseManager.getTableName() + DataBaseManager.getTableColumnsCreate());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
