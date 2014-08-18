package cablegate.stream;

import java.io.File;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
	
	
	public static void main(String[] args) {
		
		if(!(new File(DataBaseManager.getDataBaseName())).isDirectory()){
			// Create the thread to handle cable.csv reading
	    	CompletionService<Void> singleThread = new ExecutorCompletionService<Void>(Executors.newSingleThreadExecutor());
			// Run the cable.csv reading thread
	    	Future<Void> allCablesRead = singleThread.submit(new CableCSVReader());
	    	
	    	try {
	    		allCablesRead.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.exit(0);
	}
}
