package cablegate.infrastructure;

import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cablegate.models.Cable;

public class DatabaseManager {
	private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
	
	private static final String DATABASE_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String DATABASE_NAME = "DerbyDB";
	private static final String DATABASE_PROTOCOL = "jdbc:derby:";
	private static final String TABLE_NAME = "CABLES";
	private static final String LUCENE_NAME = "LuceneIndex";
	
	private static SessionFactory sessionFactory;
	
	private static final String TABLE_SCHEMA_CREATE =	"(CABLE_ID INT PRIMARY KEY," +
														"DATE_TIME VARCHAR(16) NOT NULL," +
														"CABLE_NUMBER VARCHAR(50) NOT NULL,"+
														"SENDER VARCHAR(100) NOT NULL,"+
														"CLASSIFICATION VARCHAR(50) NOT NULL,"+
														"REFERRALS CLOB,"+
														"MAILING_LIST CLOB NOT NULL,"+
														"CABLE_TEXT CLOB NOT NULL)";
	
	private static final String TABLE_SCHEMA =		"(CABLE_ID," +
													"DATE_TIME," +
													"CABLE_NUMBER,"+
													"SENDER,"+
													"CLASSIFICATION,"+
													"REFERRALS,"+
													"MAILING_LIST,"+
													"CABLE_TEXT)";
	
	public static void configureHibernateSession(){
		// Load hibernate.cfg.xml and programmatically add database location
		Configuration configuration = new Configuration();
		configuration.configure("cablegate/infrastructure/hibernate.cfg.xml");

		configuration.setProperty("hibernate.connection.url", getDatabaseURL());
		configuration.setProperty("hibernate.search.default.indexBase", getLuceneURL());
		
		
		// Bind the configurations to a sessionFactory
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();        
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}
	
	public static Session openSession(){
		return sessionFactory.openSession();
	}
	
	public static void closeHibernateSession(){
		sessionFactory.close();
	}
	
	public static String getDatabaseProtocol(){
		return DATABASE_PROTOCOL;
	}
	
	public static String getDatabaseName(){
		return DATABASE_NAME;
	}
	
	public static String getDatabaseDriver(){
		return DATABASE_DRIVER;
	}
	
	public static String getTableName(){
		return TABLE_NAME;
	}
	
	public static String getDatabaseURL(){
		String systemDatabaseLocation = getDatabaseProtocol() + SystemUtils.getUserDir().getAbsolutePath();
		if(SystemUtils.IS_OS_WINDOWS){
			systemDatabaseLocation += ('\\' + DATABASE_NAME + ";create=true");
		}else {
			systemDatabaseLocation += ('/' + DATABASE_NAME + ";create=true");
		}
		return systemDatabaseLocation;
	}
	
	public static String getLuceneURL(){
		String systemLuceneLocation = SystemUtils.getUserDir().getAbsolutePath();
		if(SystemUtils.IS_OS_WINDOWS){
			systemLuceneLocation += '\\';
		}else {
			systemLuceneLocation += '/';
		}
		return systemLuceneLocation + LUCENE_NAME;
	}
	
	public static String getTableSchemaCreate() {
		return TABLE_SCHEMA_CREATE;
	}

	public static String getTableSchema() {
		return TABLE_SCHEMA;
	}
	
	public static String getTableSchemaWithQueryValues() {
		return TABLE_SCHEMA  + " VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	}
	
	/*
	 * Searches for the relevant query in the database
	 */
	@SuppressWarnings("unchecked")
	public static List<Cable> query(String query, int amount, int offset){
		Session session = DatabaseManager.openSession();
		session.beginTransaction();
		
		List<Cable> cables = session.createQuery(query).setMaxResults(amount)
													   .setFirstResult(offset)
													   .list();
		
		cables.forEach(Cable::convertText);  // Turns internal Clob obj. to a String
				
		session.getTransaction().commit();
		session.close();
		
		return cables;
	}
	
	/*
	 * Does a full text search on all fields in Cable
	 */
	@SuppressWarnings("unchecked")
	public static List<Cable> searchText(String query, int amount, int offset){
		Session session = DatabaseManager.openSession();
		FullTextSession ftSession = Search.getFullTextSession(session);
		
		// Generate the query parser for Cable object
		QueryBuilder queryBuilder = ftSession.getSearchFactory()
											 .buildQueryBuilder()
											 .forEntity(Cable.class)
											 .get();
		
		// Create the raw lucene search query
		org.apache.lucene.search.Query luceneQuery = queryBuilder
														.keyword()
														.onFields(Cable.getClobHeader())
														.matching(query)
														.createQuery();
		
		// Wrap the lucene query in a hibernate query and get results
		List<Cable> cables = ftSession.createFullTextQuery(luceneQuery, Cable.class)
									  .setMaxResults(amount)
									  .setFirstResult(offset)
									  .list();
		
		cables.forEach(Cable::convertText);  // Turns internal Clob obj. to a String
		ftSession.close();
		return cables;
	}
}
