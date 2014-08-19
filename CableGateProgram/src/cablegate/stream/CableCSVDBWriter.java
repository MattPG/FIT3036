package cablegate.stream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class CableCSVDBWriter implements Callable<Void>{
	
	private final BlockingQueue<CableBean> resultQueue;
	private final int BATCH_SIZE = 1000;
	private final int MAX_CABLES = 251287;
	
	public CableCSVDBWriter(BlockingQueue<CableBean> resultQueue) {
		this.resultQueue = resultQueue;
	}

	@Override
	public Void call() throws Exception {
		
		CableBean cable;
		PreparedStatement prepStatement = null;
		Connection con = DataBaseManager.getConnectionAndCreate(); // Connect to the database
		int totalCount, batchCount;
		try{
			con.setAutoCommit(false); // Turn off auto-commits for batch-updates
			
			 prepStatement = con.prepareStatement(
					"INSERT INTO " + DataBaseManager.getTableName() + DataBaseManager.getTableColumnsWithValues());
	    	
			totalCount = 0;
			while(totalCount < MAX_CABLES){
				batchCount = 0;
				while(batchCount < BATCH_SIZE && batchCount + totalCount < MAX_CABLES){
					cable = resultQueue.take();
					DataBaseManager.addBatch(prepStatement, cable);
					batchCount++;
				}
				prepStatement.executeBatch();
				con.commit();
				totalCount += batchCount;
			}
		}finally{
			if(prepStatement != null)
				prepStatement.close();
			con.close();				
		}	
		
		return null;
	}
	

}
