package streaming.attempt1;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class PackageCableTask implements Runnable {
	
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
	
	@Override
	public void run() {
		
		try {
			resultQueue.put(cableIt.next());
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(cableIt.hasNext()){
			cableIt.next();			
		}
		
		// Allow this runnable to be recycled
		recycleQueue.add(this);		
	}
	
	public PackageCableTask reset(List<String> cable){
		myCable = cable;
		cableIt = myCable.iterator();	
		return this;
	}

}
