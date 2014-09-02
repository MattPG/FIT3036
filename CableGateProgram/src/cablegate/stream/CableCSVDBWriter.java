package cablegate.stream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import cablegate.infrastructure.CableBean;
import cablegate.infrastructure.DataBaseManager;

public class CableCSVDBWriter implements Callable<Void>{
	
	private final BlockingQueue<CableBean> resultQueue;
	private final int BATCH_SIZE = 10000;
	private final int MAX_CABLES = 251287;
	
	public CableCSVDBWriter(BlockingQueue<CableBean> resultQueue) {
		this.resultQueue = resultQueue;
	}

	@Override
	public Void call() throws Exception {

		System.out.println("Adding cables to DataBase...");
		CableBean cable;
		PreparedStatement prepStatement = null;
		Connection con = DataBaseManager.getConnection(); // Connect to the database
		int totalCount, batchCount;
		try{
			con.setAutoCommit(false); // Turn off auto-commits for batch-updates
			
			 prepStatement = con.prepareStatement(
					"INSERT INTO " + DataBaseManager.getTableName() + DataBaseManager.getTableSchemaWithValues());
	    	
			totalCount = 0;
			System.out.println("Importing... 0%");
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
				System.out.println("Importing... " + (totalCount*100) / MAX_CABLES + "%");
			}
		} catch (SQLException e){
			System.out.println("SQL EXCEPTION DB WRITER" );
			e.printStackTrace();
			System.exit(1);
		}finally{
			// close all open connections
			try {
				if(prepStatement != null)
					prepStatement.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	

}
