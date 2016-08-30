package edu.isu.umls.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import edu.isu.umls.logging.Log;
import edu.isu.umls.utils.LoggerUtil;

/**
 * @author rsaripa
 *
 */
public class DBConnection {

	private static Logger logger = Logger.getLogger(DBConnection.class.getName());
	
	private String dbURL = "jdbc:mysql://138.87.238.34:3306/umls";
	
	private String userName = "root";
	
	private String password = "umls123";
	
	private Connection connection = null;
	
	public DBConnection(){
		try{
			Log.addHandlers(logger);
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(dbURL,userName,password);
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
