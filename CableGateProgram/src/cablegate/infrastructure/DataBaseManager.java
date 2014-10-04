package cablegate.infrastructure;

import org.apache.commons.lang3.SystemUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataBaseManager {
	private static final Logger log = LoggerFactory.getLogger(DataBaseManager.class);
	
	private static final String DATABASE_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String DATABASE_NAME = "DerbyDB";
	private static final String DATABASE_PROTOCOL = "jdbc:derby:";
	private static final String TABLE_NAME = "CABLES";
	
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
		String systemDatabaseLocation = getDatabaseProtocol() + SystemUtils.getUserDir();
		if(SystemUtils.IS_OS_WINDOWS){
			systemDatabaseLocation += ('\\' + DATABASE_NAME + ";create=true");
		}else {
			systemDatabaseLocation += ('/' + DATABASE_NAME + ";create=true");
		}
		return systemDatabaseLocation;
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
}
