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
public class Relationship extends AbstractConcept{

	private String relationShipType = "";
	
	public String getRelationShipType() {
		return relationShipType;
	}

	public void setRelationShipType(String relationShipType) {
		this.relationShipType = relationShipType;
	}

	public String getRelationShipName() {
		return name;
	}

	public void setRelationShipName(String relationShipName) {
		this.name = relationShipName;
	}

	/* (non-Javadoc)
	 * @see edu.isu.umls.Concepts.AbstractConcept#setSemanticType(java.util.List)
	 */
	@Override
	public void setSemanticType(List<AbstractType> semanticType) {
		this.semanticTypes = semanticType;
		
	}

	/* (non-Javadoc)
	 * @see edu.isu.umls.Concepts.AbstractConcept#getSemanticType()
	 */
	@Override
	public List<AbstractType> getSemanticType() {
		return semanticTypes;
	}

	/* (non-Javadoc)
	 * @see edu.isu.umls.Concepts.AbstractConcept#addSemanticType(edu.isu.umls.Concepts.AbstractType)
	 */
	@Override
	public void addSemanticType(AbstractType semanticType) {
		semanticTypes.add(semanticType);
		
	}

	/* (non-Javadoc)
	 * @see edu.isu.umls.Concepts.AbstractConcept#setAdjacency(java.util.List)
	 */
	@Override
	public void setAdjacency(List<RelationTo> adjacency) {
		
	}

	/* (non-Javadoc)
	 * @see edu.isu.umls.Concepts.AbstractConcept#addToHierarchy(edu.isu.umls.Concepts.AbstractConcept)
	 */
	@Override
	public void addToHierarchy(AbstractConcept concept) {
		
	}

	/* (non-Javadoc)
	 * @see edu.isu.umls.Concepts.AbstractConcept#addToHierarchy(java.util.List)
	 */
	@Override
	public void addToHierarchy(List<AbstractConcept> concepts) {
		
	}

	/* (non-Javadoc)
	 * @see edu.isu.umls.Concepts.AbstractConcept#addToAdjacency(edu.isu.umls.Concepts.RelationTo)
	 */
	@Override
	public void addToAdjacency(RelationTo rel) {
		
	}

	/* (non-Javadoc)
	 * @see edu.isu.umls.Concepts.AbstractConcept#addToAdjacency(java.util.List)
	 */
	@Override
	public void addToAdjacency(List<RelationTo> rels) {
		
	}
	
	
}
