package cablegate.importer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

import javafx.concurrent.Task;

import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.infrastructure.DataBaseManager;
import cablegate.infrastructure.SystemConfig;
import cablegate.models.Cable;

public class DBWriter extends Task<Void> {
	private static final Logger log = LoggerFactory.getLogger(DBWriter.class);
	private final int MAX_CABLES = 251287;
	private final int PROGRESS_SIZE = 2500; // 1% step of MAX_CABLES
	private final int BATCH_SIZE = 100;
	
	private final BlockingQueue<Cable> resultQueue;
	
	public DBWriter(BlockingQueue<Cable> resultQueue) {
		this.resultQueue = resultQueue;
	}
	
	@Override
	public Void call() throws Exception {
		log.info("Adding cables to DataBase...");
		updateMessage("Importing, this may take a while...");
		DataBaseManager.openSession().doWork(new Worker(resultQueue));
		return null;
	}

    @Override protected void succeeded() {
        super.succeeded();
        updateMessage("Import Complete! Please restart the application.");
		log.info("Import Complete!");
    }
	
	private class Worker implements Work {
		
		private BlockingQueue<Cable> workerQueue;
		
		public Worker(BlockingQueue<Cable> inQueue){
			workerQueue = inQueue;
		}
		
		@Override
		public void execute(Connection connection) throws SQLException {
			Cable cable = null;
			int insertedCount = 0;
			PreparedStatement prepStatement = null;
			
			try {
				connection.setAutoCommit(false); // Turn off auto-commits for batch-updates
				prepStatement = connection.prepareStatement("insert into CABLES (CABLE_ID, DATE_TIME, CABLE_NUMBER, SENDER, CLASSIFICATION, REFERRALS, MAILING_LIST, CABLE_TEXT) values (?, ?, ?, ?, ?, ?, ?, ?)");
				
				for(insertedCount = 0; insertedCount < MAX_CABLES; insertedCount++) {
					cable = workerQueue.take();
					
					// Add this cable to insertion batch
					try {
						prepStatement.setInt(1, cable.getCableID());
						prepStatement.setString(2, cable.getDateTime());
						prepStatement.setString(3, cable.getCableNumber());
						prepStatement.setString(4, cable.getSender());
						prepStatement.setString(5, cable.getClassification());
						prepStatement.setString(6, cable.getReferrals());
						prepStatement.setString(7, cable.getMailingList());
						prepStatement.setString(8, cable.getCableString()); // Gets converted into a CLOB
						prepStatement.addBatch();
					} catch (SQLException e) {
						log.error("Insertion at addBatch exception", e);
					}
					
					// Check if it's time to commit this batch
					if(insertedCount % BATCH_SIZE == 0) {
						prepStatement.executeBatch();
						connection.commit();
					}
					
					// Check if we should update progress on UI thread
					if(insertedCount % PROGRESS_SIZE == 0){
						updateProgress(insertedCount, MAX_CABLES);
						log.debug("Imported {}%", (insertedCount*100) / MAX_CABLES);
					}
				}
				
				prepStatement.executeBatch();
				connection.commit();
			} catch (SQLException e) {
				if(cable != null) {
					log.error("SQL EXCEPTION DB WRITER {}\n{}\n{}\n{}", cable.toString(), cable.getReferrals(), cable.getMailingList(), cable.getCableString(), e);
				}
			} catch (InterruptedException e1) {
				log.error("Interrupted while waiting on workerQueue!", e1);
			} finally {
				try {
					if(prepStatement != null) {
						prepStatement.clearBatch();
						prepStatement.close();
					}	// Connection closing is handled by Hibernate
				} catch (SQLException e) {
					log.error("SQL EXCEPTION CLOSING STATEMENTS", e);
				}
			}
		}
	}
}

