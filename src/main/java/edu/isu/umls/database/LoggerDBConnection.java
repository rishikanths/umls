package edu.isu.umls.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;


public class LoggerDBConnection {

	
	private static interface Singleton {
		final LoggerDBConnection INSTANCE = new LoggerDBConnection();
    }
 
    private final DataSource loggerDB;
 
    private LoggerDBConnection(){
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "umls123"); 
        
        GenericObjectPool pool = new GenericObjectPool();
        DriverManagerConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
                "jdbc:mysql://138.87.238.34:3306/logger", properties
        );
        new PoolableConnectionFactory(
                connectionFactory, pool, null, "SELECT 1", 3, false, false, Connection.TRANSACTION_READ_COMMITTED
        );
        this.loggerDB = new PoolingDataSource(pool);
    }
 
    public static Connection getLoggerConnection() throws SQLException {
        return Singleton.INSTANCE.loggerDB.getConnection();
    }
}
