package streaming.attempt1;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.csveed.api.CsvClient;

public class PackageCableTask implements Runnable {
	
	String cable;
	CsvClient csvReader;
	private List<String> myCable;
	private Iterator<String> cableIt;
	private final BlockingQueue<PackageCableTask> recycleQueue;
	private final BlockingQueue<String> resultQueue;
	
	public PackageCableTask(List<String> cable, BlockingQueue<PackageCableTask> recycleQueue, BlockingQueue<String> resultQueue){
		myCable = cable;
		cableIt = myCable.iterator();
		this.recycleQueue = recycleQueue;
		this.resultQueue = resultQueue;
	}
	
	public PackageCableTask(String cable, BlockingQueue<PackageCableTask> recycleQueue, BlockingQueue<String> resultQueue){
		this.cable = cable;
		this.recycleQueue = recycleQueue;
		this.resultQueue = resultQueue;
	}
	
	@Override
	public void run() {
		
		
		
		// Allow this runnable to be recycled
		recycleQueue.add(this);		
	}
	
	public PackageCableTask reset(List<String> cable){
		myCable = cable;
		cableIt = myCable.iterator();	
		return this;
	}
	
	public PackageCableTask reset(String cable){
		this.cable = cable;
		return this;
	}

}
