package edu.isu.umls.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import edu.isu.umls.logging.UMLSLog;
import edu.isu.umls.utils.LoggerUtil;

/**
 * @author rsaripa
 *
 */
public class DBConnection {

	private static Logger logger = Logger.getLogger(DBConnection.class.getName());
	
	private static Connection connection = null;
	
	private DBConnection(String dbURL, String dbUser, String dbPwd){
		try{
			UMLSLog.addFileHandler(logger);
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(dbURL,dbUser,dbPwd);
			LoggerUtil.logInfo(logger, "Connected to the database");
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
	}
	
	public static void initConnection(String dbURL, String dbUser, String dbPwd){
		if(connection == null)
			new DBConnection(dbURL, dbUser,dbPwd);
	}
	
	public static Connection getConnection(){
		return connection;
	}
	
	public static void closeConnection(){
		try {
			connection.close();
		} catch (SQLException e) {
			LoggerUtil.logError(logger, e);
		}
	}
	
}
