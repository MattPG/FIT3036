package cablegate.stream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseManager {

	private static final String DATABASE_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String DATABASE_NAME = "DerbyDB";
	private static final String DATABASE_PROTOCOL = "jdbc:derby:";
	private static final String TABLE_NAME = "CABLES";
	
	public DataBaseManager(){
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
	
	public static Connection getConnectionAndCreate() {
		
		try {
			return DriverManager.getConnection(getDataBaseProtocol() + getDataBaseName() + ";create=true");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
