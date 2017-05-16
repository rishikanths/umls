/**
 * 
 *
 */
package edu.isu.umls.Concepts;

/**
 * @author Rishi Saripalle
 * @date Sep 28, 2015
 */
public class RelationTo {

	
	/**
	 * Type and information about the relation that connects the concepts.
	 */
	private Relationship predicate = null;
	
	private AbstractConcept object = null;

	public Relationship getPredicate() {
		return predicate;
	}

	public void setPredicate(Relationship predicate) {
		this.predicate = predicate;
	}

	public AbstractConcept getObject() {
		return object;
	}

	public void setObject(AbstractConcept object) {
		this.object = object;
	}
	
	
}
