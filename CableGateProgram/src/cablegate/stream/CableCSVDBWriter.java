package cablegate.stream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.slf4j.profiler.Profiler;

public class CableCSVDBWriter implements Callable<Void>{
	
	private final BlockingQueue<CableBean> resultQueue;
	
	public CableCSVDBWriter(BlockingQueue<CableBean> resultQueue) {
		this.resultQueue = resultQueue;
	}

	@Override
	public Void call() throws Exception {
		
		Connection con = DataBaseManager.getConnectionAndCreate(); // Connect to the database
		con.setAutoCommit(false); // Turn off auto-commits for batch-updates
		// TODO: CREATE CORRECT TABLE
		PreparedStatement statement = con.prepareStatement("");
		
    	Profiler timer = new Profiler("CableCSVDBWriter.java");
    	timer.start("Adding ");

			for(int count = 0; count < 251287; count++){
				resultQueue.take();		
				
			}
		
		con.close();	
    	timer.stop().print();
    	
		return null;
	}
	

}
