package streaming.attempt1;

import java.io.StringReader;
import java.util.concurrent.BlockingQueue;

import org.csveed.api.CsvClient;
import org.csveed.api.CsvClientImpl;

public class ParseCSVTask implements Runnable{
	
	private String inputString;
	private CsvClient<CableBean> csvReader;
	private CableBean myCable;
	private final BlockingQueue<ParseCSVTask> recycleQueue;
	private final BlockingQueue<CableBean> resultQueue;
	
	public ParseCSVTask(String inputString, BlockingQueue<ParseCSVTask> recycleQueue, BlockingQueue<CableBean> resultQueue){
		this.inputString = inputString;
		this.recycleQueue = recycleQueue;
		this.resultQueue = resultQueue;
	}

	@Override
	public void run() {
		
		csvReader = new CsvClientImpl<CableBean>(new StringReader(inputString), CableBean.class);
		myCable = csvReader.readBean();
		resultQueue.add(myCable);
		// Allow this runnable to be recycled
		recycleQueue.add(this);		
	}
	
	public ParseCSVTask reset(String inputString){
		this.inputString = inputString;
		return this;
	}
}
