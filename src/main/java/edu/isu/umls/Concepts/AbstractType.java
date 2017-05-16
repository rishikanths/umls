/**
 * 
 *
 */
package edu.isu.umls.Concepts;

/**
 * @author Rishi Saripalle
 * @date Sep 28, 2015
 * @time 4:49:08 PM
 *
 * AbstractType - the abstract concept to represent semantic type
 *
 */
public abstract class AbstractType {

	private String typeId = "";
	
	private String name = "";

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
