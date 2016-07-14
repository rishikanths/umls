/**
 * 
 *
 */
package edu.isu.umls.Concepts;

import java.util.List;

/**
 * @author rsaripa
 * @date Sep 25, 2015
 * @time 1:54:48 PM
 *
 * Relationship
 *
 */
public class Relationship extends AbstractRelation{


	/** 
	 * @see edu.isu.umls.Concepts.AbstractConcept#setSemanticType(java.util.List)
	 */
	public void setSemanticType(List<AbstractType> semanticRelationships) {
		this.semanticRelationships = semanticRelationships;
		
	}

	/**
	 * @see edu.isu.umls.Concepts.AbstractConcept#getSemanticType()
	 */
	public List<AbstractType> getSemanticRelations() {
		return semanticRelationships;
	}

	/** 
	 * @see edu.isu.umls.Concepts.AbstractConcept#addSemanticType(edu.isu.umls.Concepts.AbstractType)
	 */
	public void addSemanticType(AbstractType semanticType) {
		semanticRelationships.add(semanticType);
		
	}
	
	@Override
	public String toString(){
		
		return "Relation Name - "+relationName+" Relation Type - "+relationType+
				"  Relation Description - "+relationDescription;
	}
}
