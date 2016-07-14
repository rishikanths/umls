package edu.isu.umls.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isu.umls.logging.Log;
import edu.isu.umls.utils.LoggerUtil;

/**
 * @author rsaripa
 *
 */
public class DBConnection {

	private static Logger logger = Logger.getLogger(DBConnection.class.getName());
	
	private static DBConnection dbConn = null;
	
	private String dbURL = "jdbc:mysql://localhost:3306/umls";
	
	private String userName = "root";
	
	private String password = "rishi";
	
	private Connection connnection = null;
	
	private DBConnection(){
		try{
			Log.addHandlers(logger);
			Class.forName("com.mysql.jdbc.Driver");
			connnection = DriverManager.getConnection(dbURL,userName,password);
			LoggerUtil.logInfo(logger, "Connected to the database");
		}catch(Exception e){
			logger.log(Level.SEVERE, e.getMessage(),e);
		}
	}
	
	public static DBConnection getDBConnection(){
		if(dbConn == null)
			dbConn = new DBConnection();
		return dbConn;
	}
	
	public Connection getConnection(){
		if(connnection==null)
			getDBConnection();
		return connnection;
	}
}
