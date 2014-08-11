package streaming.attempt1;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseTask implements Callable<Integer>{
	
	private final ThreadPoolExecutor cableQueue;
	private final BlockingQueue<CableBean> resultQueue;
	private final BlockingQueue<ParseCSVTask> recycleQueue;
	
	private final BufferedReader stream;
	private final Matcher matcher;
	private int cableCount;
	private List<String> currCable;
	
	public ParseTask(BufferedReader stream, ThreadPoolExecutor workQueue, BlockingQueue<ParseCSVTask> recycleQueue, BlockingQueue<CableBean> resultQueue){		
		this.stream = stream;
		cableQueue = workQueue; 
		this.recycleQueue = recycleQueue;
		this.resultQueue = resultQueue;
		
		matcher = Pattern.compile("\"[0-9]+\"").matcher("");
		currCable = null; cableCount = 0; 
	}
	
	@Override
	public Integer call() throws Exception {
		
		// Stringbuilder for appending each line optimally
		StringBuilder cable = null;
		
		String currLine = readLine();
		while(currLine != null){
			// Start  saving the cable
			//currCable = new LinkedList<String>();
			//currCable.add(currLine);
			cable = new StringBuilder(7000);
			cable.append(currLine);
			
			// Add lines until we hit the next cable (or end of file)
			currLine = readLine();
			while(currLine != null && !matcher.reset(currLine).lookingAt()){
				//currCable.add(currLine);
				cable.append('\n');
				cable.append(currLine);
				currLine = readLine();		

			}

			cableCount++;
			
			if(cableCount%10000 == 0)
				System.out.println(cableCount);	
			
			// Send cable off to be processed 
			if(recycleQueue.isEmpty())
				// Create a new PCT if no old ones available
				cableQueue.execute(new ParseCSVTask(cable.toString(), recycleQueue, resultQueue));
			else			
				// Re-use an old PCT that's still in memory
				cableQueue.execute(recycleQueue.take().reset(cable.toString()));
		}
		
		return new Integer(cableCount);
	}

	private String readLine(){
		String currLine = null;
		
		try {
			currLine = stream.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return currLine;
	}
}
