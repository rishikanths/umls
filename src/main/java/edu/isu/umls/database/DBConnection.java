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
	
	private static Connection loggerConnection = null;
	
	public DBConnection(String dbURL, String dbUser, String dbPwd){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(dbURL,dbUser,dbPwd);
			LoggerUtil.logInfo(logger, "Connected to the database");
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
	}
	
	public static Connection getLoggerConnection(){
		try{
			if(loggerConnection == null){
				Class.forName("com.mysql.jdbc.Driver");
				loggerConnection = DriverManager.getConnection("jdbc:mysql://138.87.238.34:3306/logger",
						"root","umls123");
			}
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
		return loggerConnection;
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
