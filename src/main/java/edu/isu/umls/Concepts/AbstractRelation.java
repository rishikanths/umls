package edu.isu.umls.Concepts;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRelation {

	private String relationName = "";
	
	private String relationType = "";
	
	private String relationDescription = "";
	
	protected List<AbstractType> semanticRelationships = new ArrayList<AbstractType>();

	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public String getRelationDescription() {
		return relationDescription;
	}

	public void setRelationDescription(String relationDescription) {
		this.relationDescription = relationDescription;
	}
	
	
}
