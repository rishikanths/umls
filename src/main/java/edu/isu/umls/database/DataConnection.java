package edu.isu.umls.database;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isu.umls.utils.LoggerUtil;
/**
 * 
 * @author Rishi Saripalle
 */
public class DataConnection {

	private final static Logger logger = LogManager.getLogger(DataConnection.class.getName());

	private static DataSource _dataSource;
	//private static DataSource _loggerDataSource;

	public static void startDataConnection() {
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			if (_dataSource == null)
				_dataSource = (DataSource) envCtx.lookup("jdbc/MySqlUMLSDS");
			//if (_loggerDataSource == null)
			//	_loggerDataSource = (DataSource) envCtx.lookup("jdbc/MySqlLoggerDS");
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}
	}

	public static void stopDataConnection(){
		if(_dataSource!= null)
			_dataSource = null;
	}
	
	public static Connection getConnection() throws SQLException {
		return _dataSource.getConnection();
	}
	
	/*public static Connection getLoggerConnection() throws SQLException {
		return _loggerDataSource.getConnection();
	}*/

	public static void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

}
