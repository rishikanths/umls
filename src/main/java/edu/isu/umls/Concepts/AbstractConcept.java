/**
 * 
 */
package edu.isu.umls.Concepts;

import java.util.ArrayList;
import java.util.List;


/**
 * @author rsaripa
 * @date Sep 25, 2015
 * @time 1:33:56 PM
 *
 * AbstractConcept captures the common attributes
 *
 */
public abstract class AbstractConcept {
	
	protected String name = "";
	
	protected String cui = "";
	
	protected List<AbstractType> semanticTypes = new ArrayList<AbstractType>();
	
	protected List<AbstractConcept> hierarchy = new ArrayList<AbstractConcept>();
	
	protected List<RelationTo> adjacency = new ArrayList<RelationTo>();
	
	
	public abstract void setSemanticType(List<AbstractType> type);
	
	public abstract List<AbstractType> getSemanticType();
	
	public abstract void addSemanticType(AbstractType type);
	
	public abstract void setAdjacency(List<RelationTo> adjacency);
	
	public abstract void addToHierarchy(AbstractConcept concept);
	
	public abstract void addToHierarchy(List<AbstractConcept> concepts);

	public abstract void addToAdjacency(RelationTo rel);
	
	public abstract void addToAdjacency(List<RelationTo> rels);
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the cui
	 */
	public String getCui() {
		return cui;
	}

	/**
	 * @param cui the cui to set
	 */
	public void setCui(String cui) {
		this.cui = cui;
	}

}