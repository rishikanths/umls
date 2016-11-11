package edu.isu.umls.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import edu.isu.umls.utils.LoggerUtil;

/**
 * @author Rishi Saripalle
 *
 */
public class DBConnectionNew {

	private final static Logger logger = LogManager.getLogger(DBConnectionNew.class.getName());

	private final ComboPooledDataSource umlsPooledDataSource;

	private DBConnectionNew(String dbURL, String dbUser, String dbPwd) {
		ComboPooledDataSource cpds = null;
		try {
			cpds = new ComboPooledDataSource();
			cpds.setJdbcUrl(dbURL);
			cpds.setUser(dbUser);
			cpds.setPassword(dbPwd);
			cpds.setPreferredTestQuery("select 1");
			cpds.setTestConnectionOnCheckin(true);
			cpds.setTestConnectionOnCheckout(false);
			cpds.setIdleConnectionTestPeriod(300);
			cpds.setMaxIdleTime(600);
			cpds.setMinPoolSize(3);
			cpds.setMaxPoolSize(30);
			cpds.setAcquireIncrement(2);

		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}
		this.umlsPooledDataSource = cpds;
	}

	public static DBConnectionNew getPooledDBSource(String dbURL, String dbUser, String dbPwd) throws SQLException {
		final DBConnectionNew INSTANCE = new DBConnectionNew(dbURL,dbUser,dbPwd);
		return INSTANCE;
	}
	
	public Connection getConnection() throws SQLException{
		return umlsPooledDataSource.getConnection();
	}


	public void closeConnection() {
		try {
			if(umlsPooledDataSource!=null)
				umlsPooledDataSource.close();
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}
	}

}
