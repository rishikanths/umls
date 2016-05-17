/**
 * 
 *
 */
package edu.isu.umls.Concepts;

/**
 * @author rsaripa
 * @date Sep 28, 2015
 * @time 4:18:12 PM
 *
 * Statement
 *
 */
public class RelationTo {

	
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
