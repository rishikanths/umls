package edu.isu.umls.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 *       DBQuery
 *
 */
public class DBQuery {

	private Logger logger = Logger.getLogger(DBQuery.class.getName());

	private Connection connection = null;

	private Statement statement = null;

	private PreparedStatement prepStatement = null;

	private final int LIMIT = 30;
	private final int DEPTH = 2;
	
	private final int CONCEPT_LIMIT = 100;

	public DBQuery() {
		Log.addHandlers(logger);
		connection = DBConnection.getConnection();
	}

	/**
	 * Search the UMLS database based on the user string.
	 * 
	 * @param value
	 *            The string pattern to be searched
	 * @return
	 */
	public List<AbstractConcept> searchByString(String value) {
		ResultSet result = null;
		List<AbstractConcept> concepts = null;
		try {
			prepStatement = connection.prepareStatement(DBStatements.SEARCH_QUERY);

			LoggerUtil.logInfo(logger, "Search for String with pattern - " + value);
			long start = Calendar.getInstance().getTimeInMillis();

			prepStatement.setString(1, value + "%");
			prepStatement.setInt(2, LIMIT);
			result = prepStatement.executeQuery();

			long end = Calendar.getInstance().getTimeInMillis();
			LoggerUtil.logInfo(logger, "Executed in - " + (end - start) + " milli seconds");

			concepts = ConceptMapper.term2Concept(result);

			prepStatement.clearParameters();
			prepStatement.close();
			
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}

		return concepts;
	}

	/**
	 * Get the concept associated with a CUI
	 * 
	 * @param cui
	 *            Concept Unique Identifier
	 * @return {@link AbstractConcept}
	 */
	public AbstractConcept searchByCUI(String cui) {
		ResultSet result = null;
		AbstractConcept concept = null;
		try {
			prepStatement = connection.prepareStatement(DBStatements.SEARCH_BY_CUI);
			prepStatement.clearParameters();
			LoggerUtil.logInfo(logger, "Search for CUI - " + cui);
			long start = Calendar.getInstance().getTimeInMillis();

			prepStatement.setString(1, cui);
			result = prepStatement.executeQuery();

			long end = Calendar.getInstance().getTimeInMillis();
			LoggerUtil.logInfo(logger, "Executed in - " + (end - start) + " milli seconds");
			concept = ConceptMapper.term2Concept(result).get(0);
			concept.setSemanticType(getSemanticType(cui));

			prepStatement.close();
			
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}

		return concept;
	}

	/**
	 * Search the UMLS database using AUI. The function finds the associated CUI
	 * and gets the information based on the CUI.
	 * 
	 * @param aui
	 *            Atom Unique Identifier
	 * @return {@link AbstractConcept}
	 */
	public AbstractConcept searchByAUI(String aui) {
		ResultSet result = null;
		AbstractConcept concept = null;
		String cui = "";
		try {
			prepStatement = connection.prepareStatement(DBStatements.SEARCH_BY_AUI);

			LoggerUtil.logInfo(logger, "Search for AUI - " + aui);
			long start = Calendar.getInstance().getTimeInMillis();

			prepStatement.setString(1, aui);
			result = prepStatement.executeQuery();

			long end = Calendar.getInstance().getTimeInMillis();
			LoggerUtil.logInfo(logger, "Executed in - " + (end - start) + " milli seconds");
			while (result.next())
				cui = result.getString("CUI");

			concept = searchByCUI(cui);
			concept.setSemanticType(getSemanticType(cui));

			prepStatement.clearParameters();
			prepStatement.close();
			
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}

		return concept;
	}

	/**
	 * Search for the semantic type assigned to CUI. CUI can have multiple
	 * Semantic Types
	 * 
	 * @param cui
	 * @return List of {@link AbstractType}
	 */
	public List<AbstractType> getSemanticType(String cui) {
		ResultSet resultSet = null;
		List<AbstractType> type = null;
		try {
			prepStatement = connection.prepareStatement(DBStatements.SEARCH_SEMANTIC_TYPE_BY_CUI);

			LoggerUtil.logInfo(logger, "Search for Semantic type of - " + cui);
			long start = Calendar.getInstance().getTimeInMillis();

			prepStatement.setString(1, cui);
			resultSet = prepStatement.executeQuery();

			long end = Calendar.getInstance().getTimeInMillis();
			LoggerUtil.logInfo(logger, "Executed in - " + (end - start) + " milli seconds");
			type = ConceptMapper.toAbstractType(resultSet);
			prepStatement.clearParameters();
			prepStatement.close();
			
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}

		return type;
	}

	/**
	 * Get all the information associated with a CUI, which includes its name,
	 * relationships and assigned semantic type(s)
	 * 
	 * @param cui
	 *            The CUI of the UMLS concept
	 * @return {@link AbstractConcept}
	 */
	public AbstractConcept getHierarchyInfomationByCUI(String cui, int depth, AbstractConcept concept) {
		ResultSet result = null;
		try {
			prepStatement = connection.prepareStatement(DBStatements.SEARCH_CONCEPT_HIERARCHY);
			LoggerUtil.logInfo(logger, "Get Information on CUI - " + cui);
			long start = Calendar.getInstance().getTimeInMillis();

			prepStatement.setString(1, cui);
			prepStatement.executeQuery();
			result = prepStatement.getResultSet();
			if (concept == null)
				concept = new Term();
			boolean setOnce = true;

			result.last();
			int rows = result.getRow();
			result.beforeFirst();
			if (rows != 0) {
				while (result.next()) {
					if (setOnce) {
						concept.setName(ConceptMapper.normalizeName(result.getString("STR")));
						concept.setCui(cui);
						AbstractType type = new SemanticType();
						type.setName(result.getString("STY"));
						type.setTypeId(result.getString("TUI"));
						concept.addSemanticType(type);
						setOnce = false;
					}
					String rel = result.getString("REL");
					String conceptCUI2 = result.getString("CUI1");
					AbstractConcept cui2 = searchByCUI(conceptCUI2);
					cui2.setName(ConceptMapper.normalizeName(cui2.getName()));
					/*
					 * if(rel.equals("CHD")) concept.addToHierarchy(cui2); else
					 */
					if (rel.equals("PAR")) {
						System.out.println("Parent - " + concept.getName() + " - " + depth);
						if (depth != DEPTH) {
							depth++;
							System.out.println("Child - " + cui2.getName() + " - " + depth);
							AbstractConcept t = getHierarchyInfomationByCUI(cui2.getCui(), depth, null);
							depth--;
							concept.addToChildern(t);
							if(concept.getChildren().size() == CONCEPT_LIMIT){
								System.out.println("Breaking off  .... "+depth);
								break;
							}
						} else {
							concept.addToChildern(cui2);
							System.out.println("Final Child - " + cui2.getName() + " - " + depth);
							if(concept.getChildren().size() == CONCEPT_LIMIT){
								System.out.println("Breaking off  .... "+depth);
								break;
							}
							continue;
						}
					}
				}
			} else {
				concept = searchByCUI(cui);
				concept.setName(ConceptMapper.normalizeName(concept.getName()));
				AbstractType type = getSemanticType(cui).get(0);
				concept.addSemanticType(type);
			}
			long end = Calendar.getInstance().getTimeInMillis();
			LoggerUtil.logInfo(logger, "Executed in - " + (end - start) + " milli seconds");

			prepStatement.close();
			
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}

		return concept;
	}

	public AbstractConcept getAdjacencyInfomationByCUI(String cui, AbstractConcept concept) {
		ResultSet result = null;
		try {
			prepStatement = connection.prepareStatement(DBStatements.SEARCH_CONCEPT_RELATIONS);
			LoggerUtil.logInfo(logger, "Get Information on CUI - " + cui);
			long start = Calendar.getInstance().getTimeInMillis();

			prepStatement.setString(1, cui);
			prepStatement.executeQuery();
			result = prepStatement.getResultSet();
			if (concept == null)
				concept = new Term();
			boolean setOnce = true;
			while (result.next()) {
				if (setOnce) {
					concept.setName(ConceptMapper.normalizeName(result.getString("STR")));
					concept.setCui(cui);
					AbstractType type = new SemanticType();
					type.setName(result.getString("STY"));
					type.setTypeId(result.getString("TUI"));
					concept.addSemanticType(type);
					setOnce = false;
				}
				String rela = result.getString("RELA");
				String conceptCUI2 = result.getString("CUI1");
				AbstractConcept cui2 = searchByCUI(conceptCUI2);
				cui2.setName(ConceptMapper.normalizeName(cui2.getName()));
				Relationship relationship = new Relationship();

				relationship.setRelationType(result.getString("REL"));
				if (rela != null)
					relationship.setRelationName(rela);
				else
					relationship.setRelationName("N/A");
				RelationTo relTo = new RelationTo();
				relTo.setObject(cui2);
				relTo.setPredicate(relationship);

				concept.addToAdjacency(relTo);
			}
			long end = Calendar.getInstance().getTimeInMillis();
			LoggerUtil.logInfo(logger, "Executed in - " + (end - start) + " milli seconds");

			prepStatement.close();
			
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}

		return concept;
	}

	public List<String> getConceptDefinitons(String cui){
		ResultSet result = null;
		List<String> results = new ArrayList<String>();
		try {
			prepStatement = connection.prepareStatement(DBStatements.SEARCH_CONCEPT_DEFINITION);
			
			LoggerUtil.logInfo(logger, "Get Definition on CUI - " + cui);
	
			prepStatement.setString(1, cui);
			prepStatement.executeQuery();
			result = prepStatement.getResultSet();
			while(result.next()){
				String son = result.getString(1);
				String name = result.getString(2);
				String def = result.getString(3);
				String sab = result.getString(4);
				results.add("According to "+son+"("+sab+"),<b>"+name+"</b> is defined as <i>"+def+"</i>");
			}
			prepStatement.close();
			
		}catch(Exception e){
			LoggerUtil.logError(logger, e);
		}
		
		return results;
	}
	
	
	public List<RelationTo> getAdjaceny(String cui) {

		return null;
	}

	public List<AbstractConcept> getHierarchy(String cui) {

		return null;
	}

	/**
	 * @param query
	 *            The SQL query as string
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
			LoggerUtil.logInfo(logger, "Executed in - " + (end - start) + " milli seconds");
			
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}
		return result;
	}

	
	public Map<String,List<String>> getSynonyms(String cui){
		ResultSet resultSet = null;
		Map<String,List<String>> results = new HashMap<String, List<String>>();
		try {
			prepStatement = connection.prepareStatement(DBStatements.SEARCH_SYNONYMS);
			prepStatement.clearParameters();
			LoggerUtil.logInfo(logger, "Synonyms for CUI - " + cui);
			long start = Calendar.getInstance().getTimeInMillis();

			prepStatement.setString(1, cui);
			resultSet = prepStatement.executeQuery();

			long end = Calendar.getInstance().getTimeInMillis();
			LoggerUtil.logInfo(logger, "Executed in - " + (end - start) + " milli seconds");

			while(resultSet.next()){
				String source = resultSet.getString("SOURCE");
				List<String> temp = results.get(source);
				if(temp==null)
						temp = new ArrayList<String>();
				temp.add(resultSet.getString("STR"));
				results.put(resultSet.getString("SOURCE"), temp);
			}
			prepStatement.close();
			
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}
		return results;
	}
	
	public static void main(String args[]) {

		DBQuery test = new DBQuery();
		test.searchByString("mala");
		
		//String t = "Burkitt's_tumor_or_lymphoma__lymph_nodes_of_head__face__and_neck";
		//System.out.println(ConceptMapper.normalizeName(t));

	}

}
