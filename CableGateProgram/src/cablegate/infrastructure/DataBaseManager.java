package cablegate.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cablegate.importer.CSVReader;
import cablegate.importer.CableBean;

public class DataBaseManager {

	private static final String DATABASE_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String DATABASE_NAME = "DerbyDB";
	private static final String DATABASE_PROTOCOL = "jdbc:derby:";
	private static final String TABLE_NAME = "CABLES";

	
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
	
	public DataBaseManager(){
	}
	
	/*
	 * 
	 */
	public static void addBatch(PreparedStatement pstmt, CableBean cable){
		try {
			pstmt.setInt(1, cable.getCableID());
			pstmt.setString(2, cable.getDateTime());
			pstmt.setString(3, cable.getCableNumber());
			pstmt.setString(4, cable.getSender());
			pstmt.setString(5, cable.getClassification());
			pstmt.setString(6, cable.getReferrals());
			pstmt.setString(7, cable.getMailingList());
			pstmt.setString(8, cable.getCableText());
			pstmt.addBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static String getDataBaseProtocol(){
		return DATABASE_PROTOCOL;
	}
	
	public static String getDataBaseName(){
		return DATABASE_NAME;
	}
	
	public static String getDataBaseDriver(){
		return DATABASE_DRIVER;
	}
	
	public static String getTableName(){
		return TABLE_NAME;
	}
	
	public static Connection getConnection() {
		
		try {
			return DriverManager.getConnection(getDataBaseProtocol() + getDataBaseName() + ";create=true");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
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
	
	public static void searchTable(){
		
	}
	
	public static void createTable(Statement stmt, String tableName, String tableSchema){
		try {
			stmt.execute("CREATE TABLE " + tableName + tableSchema);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static Future<Void> instantiateDatabase(){
		
		// Create the thread to handle cable.csv reading
    	CompletionService<Void> singleThread = new ExecutorCompletionService<Void>(Executors.newSingleThreadExecutor());
		
    	// Run the cable.csv reading thread
    	Future<Void> allCablesRead = singleThread.submit(new CSVReader());
    	
    	return allCablesRead;  	
	}
}
