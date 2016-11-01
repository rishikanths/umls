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
	
	private Connection connection = null;
	
	public DBConnection(String dbURL, String dbUser, String dbPwd){
		try{
			UMLSLog.addFileHandler(logger);
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(dbURL,dbUser,dbPwd);
			LoggerUtil.logInfo(logger, "Connected to the database");
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
	}
	
	public Connection getConnection(){
		return connection;
	}
	
	public void closeConnection(){
		try {
			connection.close();
		} catch (SQLException e) {
			LoggerUtil.logError(logger, e);
		}
	}
	
}
