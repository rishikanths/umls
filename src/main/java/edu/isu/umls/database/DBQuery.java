package edu.isu.umls.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.isu.umls.Concepts.AbstractConcept;
import edu.isu.umls.Concepts.AbstractType;
import edu.isu.umls.Concepts.RelationTo;
import edu.isu.umls.Concepts.Relationship;
import edu.isu.umls.Concepts.SemanticType;
import edu.isu.umls.Concepts.Term;
import edu.isu.umls.logging.Log;
import edu.isu.umls.utils.ConceptMapper;
import edu.isu.umls.utils.LoggerUtil;
 /**
 * @author rsaripa
 * @date Sep 28, 2015
 * @time 4:36:14 PM
 *
 * DBQuery
 *
 */
public class DBQuery {

	private Logger logger = Logger.getLogger(DBQuery.class.getName());

	private Connection connection = null;

	private Statement statement = null;

	private PreparedStatement prepStatement = null;
	
	private final int LIMIT = 20;

	private static int DEPTH = 0;
	
	public DBQuery() {
		Log.addHandlers(logger);
		connection = DBConnection.getDBConnection().getConnection();
	}

	
	/**
	 * Search the UMLS database based on the user string.
	 * @param value The string pattern to be searched
	 * @return
	 */
	public List<AbstractConcept> searchByString(String value, int limit) {
		ResultSet result = null;
		List<AbstractConcept> concepts = null;
		try {
			prepStatement = connection
					.prepareStatement("SELECT CUI, STR from MRCONSO WHERE TS = 'P' AND STT='PF' AND ISPREF='Y'"
							+ " AND STR LIKE ? LIMIT ?");

			LoggerUtil.logInfo(logger, "Search for String with pattern - " + value);
			long start = Calendar.getInstance().getTimeInMillis();

			prepStatement.setString(1, value + "%");
			if(limit!=0)
				prepStatement.setInt(2, limit);
			else
				prepStatement.setInt(2, LIMIT);
			result = prepStatement.executeQuery();

			long end = Calendar.getInstance().getTimeInMillis();
			LoggerUtil.logInfo(logger, "Executed in - " + (end - start)+ " milli seconds");

			concepts = ConceptMapper.term2Concept(result);

			prepStatement.clearParameters();
			prepStatement.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		return concepts;
	}

	
	/**
	 * Get the single concept associated with a CUI
	 * @param cui Concept Unique Identifier
	 * @return {@link AbstractConcept}
	 */
	public AbstractConcept searchByCUI(String cui) {
		ResultSet result = null;
		AbstractConcept concept = null;
		try {
			prepStatement = connection
					.prepareStatement("SELECT CUI, STR from MRCONSO WHERE TS = 'P' AND STT='PF' AND ISPREF='Y'"
							+ " AND CUI = ?");
			prepStatement.clearParameters();
			LoggerUtil.logInfo(logger, "Search for CUI - "
					+ cui);
			long start = Calendar.getInstance().getTimeInMillis();

			prepStatement.setString(1, cui);
			result = prepStatement.executeQuery();

			long end = Calendar.getInstance().getTimeInMillis();
			LoggerUtil.logInfo(logger, "Executed in - " + (end - start)
					+ " milli seconds");
			concept = ConceptMapper.term2Concept(result).get(0);
			concept.setSemanticType(getSemanticType(cui));
			
			
			prepStatement.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		return concept;
	}
	
	
	/**
	 * Search the UMLS database using AUI. The function finds the associated CUI and gets the information based on the CUI. 
	 * @param aui Atom Unique Identifier
	 * @return {@link AbstractConcept}
	 */
	public AbstractConcept searchByAUI(String aui) {
		ResultSet result = null;
		AbstractConcept concept = null;
		String cui = "";
		try {
			prepStatement = connection
					.prepareStatement("SELECT CUI from MRCONSO WHERE AUI = ?");

			LoggerUtil.logInfo(logger, "Search for AUI - "+ aui);
			long start = Calendar.getInstance().getTimeInMillis();

			prepStatement.setString(1, aui);
			result = prepStatement.executeQuery();

			long end = Calendar.getInstance().getTimeInMillis();
			LoggerUtil.logInfo(logger, "Executed in - " + (end - start)
					+ " milli seconds");
			while(result.next())
				cui = result.getString("CUI");
			
			concept = searchByCUI(cui);
			concept.setSemanticType(getSemanticType(cui));
			
			prepStatement.clearParameters();
			prepStatement.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		return concept;
	}

	/**
	 * Search for the semantic type assigned to CUI. CUI can have multiple Semantic Types
	 * @param cui
	 * @return List of {@link AbstractType}
	 */
	public List<AbstractType> getSemanticType(String cui) {
		ResultSet resultSet = null;
		List<AbstractType> type =null;
		try {
			prepStatement = connection
					.prepareStatement("SELECT TUI, STY from MRSTY WHERE CUI = ?");

			LoggerUtil.logInfo(logger, "Search for Semantic type of - "
					+ cui);
			long start = Calendar.getInstance().getTimeInMillis();

			prepStatement.setString(1, cui);
			resultSet = prepStatement.executeQuery();

			long end = Calendar.getInstance().getTimeInMillis();
			LoggerUtil.logInfo(logger, "Executed in - " + (end - start)
					+ " milli seconds");
			type = ConceptMapper.toAbstractType(resultSet);
			prepStatement.clearParameters();
			prepStatement.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		return type;
	}

	/**
	 * Get all the information associated with a CUI, which includes its name, relationships and assigned semantic type(s) 
	 * @param cui The CUI of the UMLS concept
	 * @return {@link AbstractConcept}
	 */
	public AbstractConcept getInfomationByCUI(String cui,int depth){
		ResultSet result = null;
		AbstractConcept concept = null;
		try {
			prepStatement = connection
					.prepareStatement("SELECT DISTINCT r.CUI1,c.CUI,c.STR,r.REL, r.RELA, st.TUI,st.STY"
							+ " FROM mrconso as c, mrrel as r, mrsty as st WHERE"
							+ " c.CUI = ? AND c.CUI = r.CUI2 AND c.CUI = st.CUI AND r.CUI1 <> c.CUI"
							+ " AND r.rel IN ('PAR','CHD','QB','RO','RU','XR',NULL) AND c.TS = 'P' AND c.STT='PF' AND c.ISPREF='Y'"
							+ " GROUP BY r.CUI1");
			LoggerUtil.logInfo(logger, "Get Information on CUI - "+ cui);
			long start = Calendar.getInstance().getTimeInMillis();

			prepStatement.setString(1, cui);
			prepStatement.executeQuery();
			result = prepStatement.getResultSet();
			concept = new Term();
			boolean setOnce = true;
			while(result.next()){
				if(setOnce){
					concept.setName(ConceptMapper.normalizeName(result.getString("STR")));
					concept.setCui(cui);
					AbstractType type = new SemanticType();
					type.setName(result.getString("STY"));
					type.setTypeId(result.getString("TUI"));
					concept.addSemanticType(type);
					setOnce = false;
				}
				String rel = result.getString("REL");
				String rela = result.getString("RELA");
				String conceptCUI2 = result.getString("CUI1");
				AbstractConcept cui2 = searchByCUI(conceptCUI2);
				Relationship relationship = new Relationship();
				
				if(rel.equals("CHD"))
					concept.addToHierarchy(cui2);
				else if(rel.equals("PAR")){
					System.out.println("Parent - "+ concept.getName()+ " - "+depth);
					if(depth!=3){
						depth++;
						System.out.println("Child - "+cui2.getName()+ " - "+depth);
						AbstractConcept t = getInfomationByCUI(cui2.getCui(),depth);
						depth--;
						concept.addToChildern(t);
					}else{
						concept.addToChildern(cui2);
						System.out.println("Final Child - "+cui2.getName()+ " - "+depth);
						continue;
					}
				}
				else if(!rel.equals("CHD")){
					relationship.setRelationType(result.getString("REL"));
					if(rela!=null)
						relationship.setRelationName(result.getString("RELA"));
					else
						relationship.setRelationName("N/A");
					RelationTo relTo = new RelationTo();
					relTo.setObject(cui2);
					relTo.setPredicate(relationship);
					
					concept.addToAdjacency(relTo);
				}
			}
			long end = Calendar.getInstance().getTimeInMillis();
			LoggerUtil.logInfo(logger, "Executed in - " + (end - start)
					+ " milli seconds");
			
			prepStatement.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		return concept;
	}
	
	public List<RelationTo> getAdjaceny(String cui) {

		return null;
	}

	public List<AbstractConcept> getHierarchy(String cui) {

		return null;
	}

	/**
	 * @param query The SQL query as string
	 * @return Returns the SQL {@link ResultSet}
	 */
	public ResultSet executeQuery(String query) {
		ResultSet result = null;
		try {
			LoggerUtil.logInfo(logger, query);
			long start = Calendar.getInstance().getTimeInMillis();

			statement = connection.createStatement();
			result = statement.executeQuery(query);
			statement.close();

			long end = Calendar.getInstance().getTimeInMillis();
			LoggerUtil.logInfo(logger, "Executed in - " + (end - start)
					+ " milli seconds");

		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return result;
	}
	
	public static void main(String args[]){
		
		DBQuery test = new DBQuery();
		test.getInfomationByCUI("C0018790",0);
		
	}
	
}
