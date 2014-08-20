package cablegate.stream;

import java.io.File;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.profiler.Profiler;

public class Main {
	
	
	public static void main(String[] args) {
		File dataBase = new File(DataBaseManager.getDataBaseName());
		
		if(!dataBase.isDirectory()){
			System.out.println("Creating Database...");
	    	Profiler timer = new Profiler("Main.java");
	    	timer.start("Adding ");
	    	
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
	    	
	    	timer.stop().print();
		}
		System.exit(0);
	}
}
