/**
 * 
 *
 */
package edu.isu.umls.Concepts;

import java.util.List;


/**
 * @author rsaripa
 * @date Sep 25, 2015
 * @time 1:48:59 PM
 *
 * Term
 *
 */
public class Term extends AbstractConcept {

	
	/**
	 * @return the semanticType
	 */
	@Override
	public List<AbstractType> getSemanticType() {
		return semanticTypes;
	}

	/**
	 * @param semanticType the semanticType to set
	 */
	@Override
	public void setSemanticType(List<AbstractType> semanticType) {
		this.semanticTypes = semanticType;
	}
	
	@Override
	public void addSemanticType(AbstractType semanticType){
		semanticTypes.add(semanticType);
	}
	
	/**
	 * @return the hierarchy
	 */
	public List<AbstractConcept> getHierarchy() {
		return hierarchy;
	}

	/**
	 * @param hierarchy the hierarchy to set
	 */
	public void setHierarchy(List<AbstractConcept> hierarchy) {
		this.hierarchy = hierarchy;
	}

	/**
	 * @return the adjacency
	 */
	public List<RelationTo> getAdjacency() {
		return adjacency;
	}

	/**
	 * @param adjacency the adjacency to set
	 */
	public void setAdjacency(List<RelationTo> adjacency) {
		this.adjacency = adjacency;
	}
	
	public void addToHierarchy(AbstractConcept concept){
		hierarchy.add(concept);
	}
	
	public void addToHierarchy(List<AbstractConcept> concepts){
		hierarchy.addAll(concepts);
	}

	public void addToAdjacency(RelationTo rel){
		adjacency.add(rel);
	}
	
	public void addToAdjacency(List<RelationTo> rels){
		adjacency.addAll(rels);
	}
	
	@Override
	public String toString(){
		String top = super.toString();
		String termString = top+"\n";
				
		return termString;
	}
	
}
