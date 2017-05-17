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

	private final static Logger logger = LogManager.getLogger(DBQuery.class.getName());

	// private Session session = null;
	private Connection dbConnection = null;
	private final int LIMIT = 30;
	private final int DEPTH = 2;

	private final int CONCEPT_LIMIT = 100;

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
		List<Object[]> results = new ArrayList<>();
		try {
			getConnection();
			{
				PreparedStatement stmt = dbConnection.prepareStatement(DBStatements.SEARCH_QUERY);
				stmt.setString(1, value + "%");
				stmt.setMaxRows(LIMIT);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Object o[] = new Object[2];
					o[0] = (Object) rs.getString(1);
					o[1] = (Object) rs.getString(2);
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
		List<Object[]> results = new ArrayList<>();
		try {
			getConnection();
			{
				PreparedStatement stmt = dbConnection.prepareStatement(DBStatements.SEARCH_BY_CUI);
				stmt.setString(1, cui);
				stmt.setMaxRows(LIMIT);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Object o[] = new Object[2];
					o[0] = (Object) rs.getString(1);
					o[1] = (Object) rs.getString(2);
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
		List<Object[]> results = new ArrayList<>();
		try {

			getConnection();
			{
				PreparedStatement stmt = dbConnection.prepareStatement(DBStatements.SEARCH_SEMANTIC_TYPE_BY_CUI);
				stmt.setString(1, cui);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Object o[] = new Object[2];
					o[0] = (Object) rs.getString(1);
					o[1] = (Object) rs.getString(2);
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

	/**
	 * Get all the information associated with a CUI, which includes its name,
	 * relationships and assigned semantic type(s)
	 * 
	 * @param cui
	 *            The CUI of the UMLS concept
	 * @return {@link AbstractConcept}
	 */
	public AbstractConcept getHierarchyInfomationByCUI(String cui, int depth, AbstractConcept concept) {
		List<Object[]> results = new ArrayList<>();
		try {
			getConnection();
			{
				PreparedStatement stmt = dbConnection.prepareStatement(DBStatements.SEARCH_CONCEPT_HIERARCHY);
				stmt.setString(1, cui);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Object o[] = new Object[7];
					o[0] = (Object) rs.getString(1);
					o[1] = (Object) rs.getString(2);
					o[2] = (Object) rs.getString(3);
					o[3] = (Object) rs.getString(4);
					o[4] = (Object) rs.getString(5);
					o[5] = (Object) rs.getString(6);
					o[6] = (Object) rs.getString(7);
					results.add(o);
				}
				stmt.close();
			}
			closeConnection();

			if (concept == null)
				concept = new Term();
			boolean setOnce = true;

			int rows = results.size();
			if (rows != 0) {
				for (Object[] o : results) {
					if (setOnce) {
						concept.setName(ConceptMapper.normalizeName(o[2].toString()));
						concept.setCui(cui);
						AbstractType type = new SemanticType();
						type.setName(o[6].toString());
						type.setTypeId(o[5].toString());
						concept.addSemanticType(type);
						setOnce = false;
					}
					String rel = o[3].toString();
					String conceptCUI2 = o[0].toString();
					AbstractConcept cui2 = searchByCUI(conceptCUI2);
					cui2.setName(ConceptMapper.normalizeName(cui2.getName()));
					/*
					 * if(rel.equals("CHD")) concept.addToHierarchy(cui2); else
					 */
					if (rel.equals("PAR")) {
						// System.out.println("Parent - " + concept.getName() +
						// " - " + depth);
						if (depth != DEPTH) {
							depth++;
							// System.out.println("Child - " + cui2.getName() +
							// " - " + depth);
							AbstractConcept t = getHierarchyInfomationByCUI(cui2.getCui(), depth, null);
							depth--;
							concept.addToChildern(t);
							if (concept.getChildren().size() == CONCEPT_LIMIT) {
								// System.out.println("Breaking off ....
								// "+depth);
								break;
							}
						} else {
							concept.addToChildern(cui2);
							// System.out.println("Final Child - " +
							// cui2.getName() + " - " + depth);
							if (concept.getChildren().size() == CONCEPT_LIMIT) {
								// System.out.println("Breaking off ....
								// "+depth);
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
		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}

		return concept;
	}

	public AbstractConcept getAdjacencyInfomationByCUI(String cui, AbstractConcept concept) {
		List<Object[]> results = new ArrayList<>();
		try {
			getConnection();
			{
				PreparedStatement stmt = dbConnection.prepareStatement(DBStatements.SEARCH_CONCEPT_RELATIONS);
				stmt.setString(1, cui);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Object o[] = new Object[7];
					o[0] = (Object) rs.getString(1);
					o[1] = (Object) rs.getString(2);
					o[2] = (Object) rs.getString(3);
					o[3] = (Object) rs.getString(4);
					o[4] = (Object) rs.getString(5);
					o[5] = (Object) rs.getString(6);
					o[6] = (Object) rs.getString(7);
					results.add(o);
				}
				stmt.close();
			}
			closeConnection();

			if (concept == null)
				concept = new Term();
			boolean setOnce = true;
			for (Object[] o : results) {
				if (setOnce) {
					concept.setName(ConceptMapper.normalizeName(o[2].toString()));
					concept.setCui(cui);
					AbstractType type = new SemanticType();
					type.setName(o[6].toString());
					type.setTypeId(o[5].toString());
					concept.addSemanticType(type);
					setOnce = false;
				}

				String rela = "N/A";
				if (o[4] != null)
					rela = o[4].toString();
				String conceptCUI2 = o[0].toString();
				AbstractConcept cui2 = searchByCUI(conceptCUI2);
				cui2.setName(ConceptMapper.normalizeName(cui2.getName()));
				Relationship relationship = new Relationship();

				relationship.setRelationType(o[3].toString());
				if (rela != null)
					relationship.setRelationName(rela);
				else
					relationship.setRelationName("N/A");
				RelationTo relTo = new RelationTo();
				relTo.setObject(cui2);
				relTo.setPredicate(relationship);

				concept.addToAdjacency(relTo);
			}

		} catch (Exception e) {
			LoggerUtil.logError(logger, e);
		}

		return concept;
	}

	public List<String> getConceptDefinitons(String cui) {
		List<String> results = new ArrayList<String>();
		List<Object[]> queryResults = new ArrayList<>();
		try {
			getConnection();
			{
				PreparedStatement stmt = dbConnection.prepareStatement(DBStatements.SEARCH_CONCEPT_DEFINITION);
				stmt.setString(1, cui);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					Object o[] = new Object[4];
					o[0] = (Object) rs.getString(1);
					o[1] = (Object) rs.getString(2);
					o[2] = (Object) rs.getString(3);
					o[3] = (Object) rs.getString(4);
					queryResults.add(o);
				}
				stmt.close();
			}
			closeConnection();
			for (Object[] o : queryResults) {
				String son = o[0].toString();
				String name = o[1].toString();
				String def = o[2].toString();
				String sab = o[3].toString();
				results.add(
						"According to " + son + "(" + sab + "),<b>" + name + "</b> is defined as <i>" + def + "</i>");
			}
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
	public List<Object[]> executeQuery(String nativeQuery) {
		List<Object[]> result = new ArrayList<>();
		try {
			getConnection();
			{
				ResultSet rs = dbConnection.createStatement().executeQuery(nativeQuery);
				int columns = rs.getMetaData().getColumnCount();
				while (rs.next()) {
					Object o[] = new Object[columns];
					for(int i=0;i<columns;i++)
						o[i] = (Object) rs.getString(i+1);
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
