package streaming.attempt1;

import java.io.BufferedReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

public class Main {
	
	private static final CountDownLatch initiator = new CountDownLatch(1);
	private static BufferedReader stream;
	
	public static void main(String[] args) {		
		initialise();		
				
		// This queue is where the PCTs place their results
		BlockingQueue<String> resultPCTQueue = new LinkedBlockingQueue<String>();
		
		// This queue allows me to recycle PCTs as they finish
		BlockingQueue<PackageCableTask> recyclePCTQueue = new LinkedBlockingQueue<PackageCableTask>();
		
		// Create the thread to handle cable.csv reading
    	CompletionService<Integer> cableProducer = new ExecutorCompletionService<Integer>(Executors.newSingleThreadExecutor());
    	
    	// Create a thread pool to parse all the individual cables and extract information
    	ThreadPoolExecutor TPE = (new ThreadPoolExecutor(0, Integer.MAX_VALUE, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()));
    	
    	// Submit the runnable that handles cable.csv reading to the singular thread
    	Future<Integer> allCablesRead = cableProducer.submit(new ParseTask(stream, TPE, recyclePCTQueue, resultPCTQueue));
    	
    	
    	while(!allCablesRead.isDone() || !resultPCTQueue.isEmpty()){
    		try {
				MainWindow.getTextArea().append(resultPCTQueue.take() + "\n");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    			
		try {
			System.out.println("Finished reading!\n" + "Number of cables read: " + allCablesRead.get().intValue());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		TPE.shutdown();
    	//TODO: DriverManager.getConnection(SystemConfig.)
    	System.out.println("main done!");
	}
	
	private static void initialise(){		
		SystemConfig system = new SystemConfig();

		SwingUtilities.invokeLater(new Runnable() {					
			@Override
			public void run() {
				new MainWindow();
				Main.countDown();
			}
		});
		
		stream  = new BufferedReader(system.getCableStream());    	
    	
    	try {
			initiator.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void countDown(){
		initiator.countDown();
	}
}
		
		