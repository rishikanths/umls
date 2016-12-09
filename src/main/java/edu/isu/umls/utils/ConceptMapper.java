/**
 * 
 *
 */
package edu.isu.umls.utils;

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

	
	public static List<AbstractConcept> term2Concept(List<Object[]> resultSet) throws Exception{
		
		List<AbstractConcept> concepts = new ArrayList<AbstractConcept>();
		for(Object[] o:resultSet){
			Term concept = new Term();
			concept.setCui(o[0].toString());
			concept.setName(o[1].toString().replace(" ", "_").replace(",", "_"));
			concepts.add(concept);
		}
		return concepts;
	}
		
	public static List<AbstractType> toAbstractType(List<Object[]> resultSet) throws Exception{
		
		List<AbstractType> type = new ArrayList<AbstractType>();
		for(Object[] o:resultSet){
			AbstractType tempType = null;
			tempType = new SemanticType();
			tempType.setName(o[1].toString());
			tempType.setTypeId(o[0].toString());
			type.add(tempType);
		}
		return type;
	}
	
	public static String normalizeName(String name){
		return name.replaceAll("'", "").replaceAll("[^\\w0-9]","_").replaceAll(" ", "_");
	}
}
