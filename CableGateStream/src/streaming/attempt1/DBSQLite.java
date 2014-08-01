/*
 * CODE USED UNDER CREATIVE COMMONS LICENSE:
 * http://en.wikibooks.org/wiki/Java_JDBC_using_SQLite/Extending_the_base_class
 */

package streaming.attempt1;

public class DBSQLite extends DBBase {
	
	String sDriverForClass = "org.sqlite.JDBC";
	
	public DBSQLite(String sUrlKey) throws Exception{	 
		init(sDriverForClass, sUrlKey);
		if(conn != null){
			System.out.println("Connected OK using " + sDriverForClass + " to " + sUrlKey);
		}
		else{
			System.out.println("Connection failed");
		}
	}
}
