package gag;

import java.io.Serializable;

public class Subscription implements Serializable{

	private long variableId;

	private String variableName;
	private String location;
	
	public String getVariableName() {
		return variableName;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public long getVariableId() {
		return variableId;
	}
	public void setVariableId(long variableId) {
		this.variableId = variableId;
	}
}
