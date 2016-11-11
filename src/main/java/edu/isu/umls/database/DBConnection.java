package edu.isu.umls.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isu.umls.utils.LoggerUtil;

/**
 * @author rsaripa
 *
 */
public class DBConnection {

	
	private final static Logger logger = LogManager.getLogger(DBConnection.class.getName());
	
	private Connection connection = null;
	
	
	public DBConnection(String dbURL, String dbUser, String dbPwd){
		try{
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
