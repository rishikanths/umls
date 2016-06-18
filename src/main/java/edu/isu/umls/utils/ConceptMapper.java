/**
 * 
 *
 */
package edu.isu.umls.utils;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import edu.isu.umls.Concepts.AbstractConcept;
import edu.isu.umls.Concepts.AbstractType;
import edu.isu.umls.Concepts.SemanticType;
import edu.isu.umls.Concepts.Term;

/**
 * @author rsaripa
 * @date Sep 28, 2015
 * @time 3:08:56 PM
 *
 * TermConceptMapper
 *
 */
public class ConceptMapper {

	
	public static List<AbstractConcept> term2Concept(ResultSet resultSet) throws Exception{
		
		List<AbstractConcept> concepts = new ArrayList<AbstractConcept>();
		while(resultSet.next()){
			Term concept = new Term();
			concept.setCui(resultSet.getString("CUI"));
			concept.setName(resultSet.getString("STR").replace(" ", "_").replace(",", "_"));
			concepts.add(concept);
		}
		return concepts;
	}
	
		
	public static List<AbstractType> toAbstractType(ResultSet resultSet) throws Exception{
		
		List<AbstractType> type = new ArrayList<AbstractType>();
		while(resultSet.next()){
			AbstractType tempType = null;
			tempType = new SemanticType();
			tempType.setName(resultSet.getString("STY"));
			tempType.setTypeId(resultSet.getString("TUI"));
			type.add(tempType);
		}
		return type;
	}
	
	public static String normalizeName(String name){
		return name.replaceAll("[^\\w0-9]","_").replaceAll(" ", "_");
	}
	
}
