package cablegate.importer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

import javafx.concurrent.Task;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.infrastructure.DataBaseManager;
import cablegate.models.Cable;

/* Import cables wrapper class for a hibernate worker */
public class DBWriter extends Task<Void> {
	private static final Logger log = LoggerFactory.getLogger(DBWriter.class);
	private final int MAX_CABLES = 251287;
	private final int PROGRESS_SIZE = 2500; // 1% step of MAX_CABLES
	private final int BATCH_SIZE = 100;
	
	private final BlockingQueue<Cable> resultQueue;
	private Session session;
	private FullTextSession ftSession;
	
	public DBWriter(BlockingQueue<Cable> resultQueue) {
		this.resultQueue = resultQueue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javafx.concurrent.Task#call()
	 * Initial function call
	 */
	@Override public Void call() throws Exception {
		session = DataBaseManager.openSession();
		
		updateMessage("Importing, this may take a while...");
		log.info("Importing, this may take a while...");
		session.doWork(new CableImporter(resultQueue));
		
        updateMessage("Import Complete! Indexing Entries...");
		log.info("Import Complete! Indexing Entries...");
		ftSession = Search.getFullTextSession(session);
		ftSession.setFlushMode(FlushMode.MANUAL);
		ftSession.setCacheMode(CacheMode.IGNORE);
		ftSession.beginTransaction();
		
		//Scrollable results will avoid loading too many objects in memory
		ScrollableResults results = ftSession.createCriteria(Cable.class)
		    .setFetchSize(BATCH_SIZE)
		    .scroll(ScrollMode.FORWARD_ONLY);
		int index = 0;
		while(results.next()) {
		    index++;
		    ftSession.index(results.get(0)); //index each element
		    
			// Check if it's time to commit this batch
		    if (index % BATCH_SIZE == 0) {
		    	ftSession.flushToIndexes(); //apply changes to indexes
		    	ftSession.clear(); //free memory since the queue is processed
		    }
			
			// Check if we should update progress on UI thread
			if(index % PROGRESS_SIZE == 0){
				updateProgress(index + MAX_CABLES, 2*MAX_CABLES);
				log.debug("Indexed {}%", (index*100) / MAX_CABLES);
			}
		}
		ftSession.getTransaction().commit();
		session.close();
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javafx.concurrent.Task#succeeded()
	 */
    @Override protected void succeeded() {
        super.succeeded();
        updateMessage("Indexing Complete! Please restart the application.");
		log.info("Indexing Complete!");
    }    
    
    /* Worker that imports the cables and allows for progress updates */
	private class CableImporter implements Work {
		
		private BlockingQueue<Cable> workerQueue;
		
		public CableImporter(BlockingQueue<Cable> inQueue){
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
						updateProgress(insertedCount, 2*MAX_CABLES);
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
	
    /* allows for progress updates while indexing */
    private class JFXIndexer implements MassIndexerProgressMonitor{		
		@Override
		public void documentsAdded(long increment) {
			log.debug("Docs added: {}", increment);
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.hibernate.search.batchindexing.MassIndexerProgressMonitor#addToTotalCount(long)
		 * The total count of entities to be indexed is added here; It could be called more than once, the implementation should add them up.
		 * This is invoked several times and concurrently during the indexing process.
		 */
		@Override
		public void addToTotalCount(long count) {
			log.debug("Add Total Count: {}", count);
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.hibernate.search.batchindexing.MassIndexerProgressMonitor#documentsBuilt(int)
		 * The number of Documents built; This is invoked several times and concurrently during the indexing process.
		 */
		@Override
		public void documentsBuilt(int number) {
			log.debug("Docs built: {}", number);
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.hibernate.search.batchindexing.MassIndexerProgressMonitor#entitiesLoaded(int)
		 * 
		 * The number of entities loaded from database; This is invoked several times and concurrently during the indexing process.
		 */
		@Override
		public void entitiesLoaded(int size) {
			updateProgress(size + MAX_CABLES, 2*MAX_CABLES);		
			log.debug("Updating indexing progress {}/{}", size, 2*MAX_CABLES);
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.hibernate.search.batchindexing.MassIndexerProgressMonitor#indexingCompleted()
		 * Invoked when the indexing is completed.
		 */
		@Override
		public void indexingCompleted() {
			log.info("Indexing finished!");
		}
    	
    }
}

