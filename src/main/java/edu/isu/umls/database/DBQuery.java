package edu.isu.umls.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isu.umls.Concepts.AbstractConcept;
import edu.isu.umls.Concepts.AbstractType;
import edu.isu.umls.Concepts.RelationTo;
import edu.isu.umls.Concepts.Relationship;
import edu.isu.umls.Concepts.SemanticType;
import edu.isu.umls.Concepts.Term;
import edu.isu.umls.utils.ConceptMapper;
import edu.isu.umls.utils.LoggerUtil;

/**
 * @author Rishi Saripalle
 * @date Sep 28, 2015
 * @time 4:36:14 PM
 *
 */
public class DBQuery {

	enum QUERY{
		Hierarchy,
		Adjacency
	}
	
	
	private final static Logger logger = LogManager.getLogger(DBQuery.class.getName());

	private Connection dbConnection = null;
	private final int LIMIT = 30;
	
	private final String UNKNOWN_RELA = "unknown";
	
	/**
	 * Obtains a data connection from the data source Check
	 * {@link DataConnection}
	 */
	private void getConnection() {
		try {
			dbConnection = DataConnection.getConnection();
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}
	}

	/**
	 * Closes the connection obtained from the data source Check
	 * {@link DataConnection}
	 */
	private void closeConnection() {
		try {
			if (dbConnection != null) {
				dbConnection.close();
				dbConnection = null;
			}
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}
	}

	/**
	 * Search the UMLS database based on the user string.
	 * 
	 * @param value
	 *            The string pattern to be searched
	 * @return List of {@link AbstractConcept}
	 */
	public List<AbstractConcept> searchByString(String value) {
		List<AbstractConcept> concepts = null;
		List<String[]> results = new ArrayList<>();
		try {
			getConnection();
			{
				PreparedStatement stmt = dbConnection.prepareStatement(DBStatements.SEARCH_QUERY);
				stmt.setString(1, value + "%");
				stmt.setMaxRows(LIMIT);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					String o[] = new String[2];
					o[0] = rs.getString(1);
					o[1] = rs.getString(2);
					results.add(o);
				}
				stmt.close();
			}
			closeConnection();
			concepts = ConceptMapper.term2Concept(results);
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
		AbstractConcept concept = null;
		List<String[]> results = new ArrayList<>();
		try {
			getConnection();
			{
				PreparedStatement stmt = dbConnection.prepareStatement(DBStatements.SEARCH_BY_CUI);
				stmt.setString(1, cui);
				stmt.setMaxRows(LIMIT);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					String o[] = new String[2];
					o[0] = rs.getString(1);
					o[1] = rs.getString(2);
					results.add(o);
				}
				stmt.close();
			}
			closeConnection();
			concept = ConceptMapper.term2Concept(results).get(0);
			concept.setSemanticType(getSemanticType(cui));

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
		AbstractConcept concept = null;
		String cui = "";
		try {
			getConnection();
			{
				PreparedStatement stmt = dbConnection.prepareStatement(DBStatements.SEARCH_BY_AUI);
				stmt.setString(1, aui);
				ResultSet rs = stmt.executeQuery();
				while (rs.next())
					cui = rs.getString(1);
				stmt.close();
			}
			closeConnection();
			concept = searchByCUI(cui);
			concept.setSemanticType(getSemanticType(cui));

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
		List<AbstractType> type = null;
		List<String[]> results = new ArrayList<>();
		try {

			getConnection();
			{
				PreparedStatement stmt = dbConnection.prepareStatement(DBStatements.SEARCH_SEMANTIC_TYPE_BY_CUI);
				stmt.setString(1, cui);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					String o[] = new String[2];
					o[0] = rs.getString(1);
					o[1] = rs.getString(2);
					results.add(o);
				}
				stmt.close();
			}
			closeConnection();
			type = ConceptMapper.toAbstractType(results);

		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}

		return type;
	}

	
	private void getInfomationByCUI(String cui, AbstractConcept sourceConcept,QUERY query) {
		List<String[]> results = new ArrayList<>();
		try {
			getConnection();
			{
				PreparedStatement stmt = null;
				if(query.equals(QUERY.Hierarchy))
					stmt = dbConnection.prepareStatement(DBStatements.SEARCH_CONCEPT_HIERARCHY);
				else
					stmt = dbConnection.prepareStatement(DBStatements.SEARCH_CONCEPT_RELATIONS);
				stmt.setString(1, cui);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					String o[] = new String[10];
					o[0] = rs.getString(1); // CUI1
					o[1] = rs.getString(2); // STR
					o[2] = rs.getString(3); // STY
					o[3] = rs.getString(4); // TUI
					o[4] = rs.getString(5); // REL
					o[5] = rs.getString(6); // RELA
					o[6] = rs.getString(7); // CUI2
					o[7] = rs.getString(8); // STR
					o[8] = rs.getString(9); // STY
					o[9] = rs.getString(10); // TUI
					results.add(o);
				}
				stmt.close();
			}
			closeConnection();

			if (sourceConcept == null)
				sourceConcept = new Term();
			boolean setOnce = true;

			int rows = results.size();
			if (rows != 0) {
				for (String[] o : results) {
					if (setOnce) {
						buildConcept(sourceConcept, o[7], o[6], o[8], o[9]);
						setOnce = false;
					}
					AbstractConcept objectConcept = new Term();
					buildConcept(objectConcept, o[1], o[0], o[2], o[3]);
					if(query.equals(QUERY.Hierarchy)){
						
						sourceConcept.addToChildern(objectConcept);
					}else{
						Relationship relationship = new Relationship();

						relationship.setRelationType(o[4]);
						if (o[5] != null)
							relationship.setRelationName(o[5]);
						else
							relationship.setRelationName(UNKNOWN_RELA);
						RelationTo relTo = new RelationTo();
						relTo.setObject(objectConcept);
						relTo.setPredicate(relationship);
						sourceConcept.addToAdjacency(relTo);
					}
				}
			} else {
				sourceConcept = searchByCUI(cui);
				sourceConcept.setName(ConceptMapper.normalizeName(sourceConcept.getName()));
				AbstractType type = getSemanticType(cui).get(0);
				sourceConcept.addSemanticType(type);
			}
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}
	}

	/**
	 * Get the hierarchical information associated with a CUI, which includes its name,
	 * children and assigned semantic type(s)
	 * 
	 * @param cui
	 *            The CUI of the UMLS concept
	 * @return {@link AbstractConcept}
	 */
	public AbstractConcept getHierarchyInfomationByCUI(String cui, AbstractConcept sourceConcept) {
		getInfomationByCUI(cui, sourceConcept, QUERY.Hierarchy);

		return sourceConcept;
	}

	/**
	 * Get the relational information associated with a CUI, which includes its name,
	 * relationship, relationship type and assigned semantic type(s).
	 * 
	 * https://www.nlm.nih.gov/research/umls/knowledge_sources/metathesaurus/release/abbreviations.html
	 * 
	 * @param cui
	 *            The CUI of the UMLS concept
	 * @return {@link AbstractConcept}
	 */
	public AbstractConcept getAdjacencyInfomationByCUI(String cui, AbstractConcept sourceConcept) {

		getInfomationByCUI(cui, sourceConcept, QUERY.Adjacency);
		
		return sourceConcept;
	}

	public List<String> getConceptDefinitons(String cui) {
		List<String> results = new ArrayList<String>();
		try {
			getConnection();
			{
				PreparedStatement stmt = dbConnection.prepareStatement(DBStatements.SEARCH_CONCEPT_DEFINITION);
				stmt.setString(1, cui);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					results.add(
							"According to <span id='definitionSource'>" + rs.getString(1) + "(" + rs.getString(4) +
							")</span>, <b>" + rs.getString(2)+ " </b> is defined as <i>" + rs.getString(3)+ "</i>");
				}
				stmt.close();
			}
			closeConnection();
		} catch (Exception e) {
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
	public List<String[]> executeQuery(String nativeQuery) {
		List<String[]> result = new ArrayList<>();
		try {
			getConnection();
			{
				ResultSet rs = dbConnection.createStatement().executeQuery(nativeQuery);
				int columns = rs.getMetaData().getColumnCount();
				while (rs.next()) {
					String o[] = new String[columns];
					for(int i=0;i<columns;i++)
						o[i] = rs.getString(i+1);
					result.add(o);
				}
			}
			closeConnection();
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}
		return result;
	}

	public Map<String, List<String>> getSynonyms(String cui) {
		Map<String, List<String>> results = new HashMap<String, List<String>>();
		List<Object[]> queryResults = new ArrayList<>();
		try {
			
			getConnection();
			{
				PreparedStatement stmt = dbConnection.prepareStatement(DBStatements.SEARCH_SYNONYMS);
				stmt.setString(1, cui);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Object o[] = new Object[2];
					o[0] = (Object) rs.getString(1);
					o[1] = (Object) rs.getString(2);
					queryResults.add(o);
				}
				stmt.close();
			}
			closeConnection();
			
			for (Object[] o : queryResults) {
				String source = o[1].toString();
				List<String> temp = results.get(source);
				if (temp == null)
					temp = new ArrayList<String>();
				temp.add(o[0].toString());
				results.put(source, temp);
			}

		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}
		return results;
	}

	private void buildConcept(AbstractConcept concept, 
			String name, String cui, String semanticType, String semanticTypeId){
		
		concept.setName(ConceptMapper.normalizeName(name));
		concept.setCui(cui);
		AbstractType type = new SemanticType();
		type.setName(semanticType);
		type.setTypeId(semanticTypeId);
		concept.addSemanticType(type);
		
	}
	
	
	public static void main(String args[]) {

		/*
		 * DBQuery test; try { //test = new
		 * DBQuery(DBConnectionNew.getPooledDBSource(
		 * "jdbc:mysql://138.87.238.34:3306/umls", // "umls",
		 * "umls123").getConnection()); //test.searchByString("mala"); } catch
		 * (SQLException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		// String t =
		// "Burkitt's_tumor_or_lymphoma__lymph_nodes_of_head__face__and_neck";
		// System.out.println(ConceptMapper.normalizeName(t));

	}

}
