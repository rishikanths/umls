package edu.isu.umls.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
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
		super.flush();
	}

	public Logger getLogger() {
		return logger;
	}

	private String trunc(String str, int length) {
		if (str.length() < length)
			return str;
		return (str.substring(0, length));
	}

	@Override
	public void publish(LogRecord record) {
		try {
			prepStatement = connection.prepareStatement(DBStatements.JDBC_LOG);
			prepStatement.setInt(1, record.getLevel().intValue());
			prepStatement.setString(2, trunc(record.getLoggerName(), 63));
			if (record.getMessage() != null)
				prepStatement.setString(3, trunc(record.getMessage(), 255));
			else
				prepStatement.setString(3, "");
			if (record.getThrown() != null) {
				prepStatement.setString(4, trunc(record.getThrown().toString(), 63));
				StringWriter stackTraceWriter = new StringWriter();
				record.getThrown().printStackTrace(new PrintWriter(stackTraceWriter));
				prepStatement.setString(5, trunc(stackTraceWriter.toString(), 255));
			} else {
				prepStatement.setString(4, "");
				prepStatement.setString(5, "");
			}
			prepStatement.setLong(6, record.getSequenceNumber());
			prepStatement.setString(7, trunc(record.getSourceClassName(), 63));
			prepStatement.setString(8, trunc(record.getSourceMethodName(), 31));
			prepStatement.setInt(9, record.getThreadID());
			prepStatement.setTimestamp(10, new Timestamp(record.getMillis()));
			prepStatement.executeUpdate();
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}
	}

	public static void main(String argv[]) {
		JDBCHandler jdbcHandler = new JDBCHandler();
		Logger l = jdbcHandler.getLogger();
		l.addHandler(jdbcHandler);

		try {
			int i = 0 / 0;
		} catch (Exception e) {
			l.log(Level.WARNING, e.getMessage(), e);
		}
	}
}
