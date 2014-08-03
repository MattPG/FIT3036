package streaming.attempt1;

import java.io.BufferedReader;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.SwingUtilities;

public class Main {
	
	private static final CountDownLatch initiator = new CountDownLatch(1);
	
	public static void main(String[] args) {
		
	    		SystemConfig system = new SystemConfig();

	    		SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						new MainWindow();
						Main.countDown();
					}
				});
				
	    		BufferedReader stream  = new BufferedReader(system.getCableStream());
	    		BlockingQueue<List<String>> queue = new LinkedBlockingQueue<List<String>>(50000);
	        	
	        	
	        	try {
					initiator.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        	CompletionService<Void> producer = new ExecutorCompletionService<Void>(Executors.newSingleThreadExecutor());
	        	Future<Void> future = producer.submit(new ParseTask(stream, queue));
	        	
	        	//TODO: DriverManager.getConnection(SystemConfig.)
	        	 
	        	
	        	while(!future.isDone())
					try {
						queue.take();

						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	
	        	System.out.println("main done!");
	}
	
	public static void countDown(){
		initiator.countDown();
	}
}
		
		