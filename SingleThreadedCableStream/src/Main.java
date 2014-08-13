

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.profiler.Profiler;

public class Main {
	
	
	public static void main(String[] args) {
		
		// Create the thread to handle cable.csv reading
    	CompletionService<Void> singleThread = new ExecutorCompletionService<Void>(Executors.newSingleThreadExecutor());
		// A blocking queue to allow the cables to be written to the Database
		BlockingQueue<CableBean> resultQueue = new ArrayBlockingQueue<CableBean>(500);		
    	Future<Void> allCablesRead = singleThread.submit(new CableCSVReader(resultQueue));
    	int count = 0;
    	Profiler timer = new Profiler("Main.java");
    	timer.start("Adding");
    	while(count < 251287){
    		try {
				resultQueue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		count++;
    	}
    	timer.stop().print();
    	System.out.println("Total cables added: " + count);
    	System.exit(0);
	}
}
