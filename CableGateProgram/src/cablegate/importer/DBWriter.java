package cablegate.importer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.infrastructure.DataBaseManager;

public class DBWriter implements Callable<Void>{

	private static final Logger log = LoggerFactory.getLogger(DBWriter.class);
	private final BlockingQueue<CableBean> resultQueue;
	private final int BATCH_SIZE = 10000;
	private final int MAX_CABLES = 251287;
	
	public DBWriter(BlockingQueue<CableBean> resultQueue) {
		this.resultQueue = resultQueue;
	}

	@Override
	public Void call() throws Exception {

		log.debug("Adding cables to DataBase...");
		CableBean cable;
		PreparedStatement prepStatement = null;
		Connection con = DataBaseManager.getConnection(); // Connect to the database
		int totalCount, batchCount;
		try{
			con.setAutoCommit(false); // Turn off auto-commits for batch-updates
			
			 prepStatement = con.prepareStatement(
					"INSERT INTO " + DataBaseManager.getTableName() + DataBaseManager.getTableSchemaWithQueryValues());
	    	
			totalCount = 0;
			log.debug("Importing... 0%");
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
				log.debug("Importing... " + (totalCount*100) / MAX_CABLES + "%");
			}
		} catch (SQLException e){
			log.debug("SQL EXCEPTION DB WRITER" );
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
