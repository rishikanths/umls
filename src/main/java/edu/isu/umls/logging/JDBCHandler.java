package edu.isu.umls.logging;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import edu.isu.umls.database.DBConnection;
import edu.isu.umls.database.DBStatements;
import edu.isu.umls.utils.LoggerUtil;

public class JDBCHandler extends StreamHandler {

	private Connection connection = null;
	PreparedStatement prepStatement = null;
	private Logger logger = Logger.getLogger(JDBCHandler.class.getName());

	public JDBCHandler() {
		try {
			UMLSLog.addFileHandler(logger);
			logger.setLevel(Level.INFO);
			connection = DBConnection.getDBConnection().getConnection();
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}
	}

	@Override
	public void close() throws SecurityException {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			LoggerUtil.logError(logger, e);
		}
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		super.flush();
	}

	public Logger getLogger() {
		return logger;
	}
	private String trunc(String str,int length)
	  {
	    if ( str.length()<length )
	      return str;
	    return( str.substring(0,length) );
	  }

	@Override
	public void publish(LogRecord record) {
		try {
			prepStatement = connection.prepareStatement(DBStatements.JDBC_LOG);
			// adding entry to log
			prepStatement.setInt(1, record.getLevel().intValue());
			prepStatement.setString(2, trunc(record.getLoggerName(), 63));
			prepStatement.setString(3, trunc(record.getMessage(), 255));
			prepStatement.setLong(4, record.getSequenceNumber());
			prepStatement.setString(5, trunc(record.getSourceClassName(), 63));
			prepStatement.setString(6, trunc(record.getSourceMethodName(), 31));
			prepStatement.setInt(7, record.getThreadID());
			prepStatement.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
			prepStatement.executeUpdate();
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}
	}

	public static void main(String argv[]) {
		JDBCHandler jdbcHandler = new JDBCHandler();
		Logger l = jdbcHandler.getLogger();
		l.addHandler(jdbcHandler);

		l.info("Sample log entry");

		l.warning("Sample warning");

		try {
			int i = 0 / 0;
		} catch (Exception e) {
			l.log(Level.WARNING, "This is what an exception looks like", e);
		}
	}
}
