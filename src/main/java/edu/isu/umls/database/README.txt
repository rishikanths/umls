Local Database Connection
=========================

What Is in this package?
-----------------------

This package provides source files to connect to the local MYSQL database 
where UMLS terminology is installed. 

1. DBConnection - provides the data source connection to the UMLS database. 
2. DBQuery - runs SQL queries on the UMLS database using Java PreparedStatement.  
3. LoggerDBConnection - for Database logging. Need to modify the log4j2.xml to add database appender
