package gag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class Rule implements Serializable{

	private String name;
	private ServiceNode leftSide;
	private List<ServiceNode> rightSide = new ArrayList<ServiceNode>();
	private List<Term> parameters = new ArrayList<Term>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ServiceNode getLeftSide() {
		return leftSide;
	}
	public void setLeftSide(ServiceNode leftSide) {
		this.leftSide = leftSide;
	}
	public ArrayList<ServiceNode> getRightSide() {
		return (ArrayList<ServiceNode>)rightSide;
	}
	public void setRightSide(ArrayList<ServiceNode> rightSide) {
		this.rightSide = rightSide;
	}
	public ArrayList<Term> getParameters() {
		return (ArrayList<Term>)parameters;
	}
	public void setParameters(ArrayList<Term> parameters) {
		this.parameters = parameters;
	}
	
	public Rule clone(){
		Rule r =new Rule();
		r.setName(name);
		ServiceNode l= leftSide.clone();
		ArrayList<ServiceNode> rs= new ArrayList<ServiceNode>();
		for(int i=0; i<rightSide.size();i++){
			rs.add(rightSide.get(i).clone());
		}
		for(int i=0; i<parameters.size();i++){
			r.parameters.add(parameters.get(i));
		}
		r.setLeftSide(l);
		r.setRightSide(rs);
		return r;
	}
	
	public String text(){
		String p=name+": ";
		p+=leftSide.text()+" ->  \n";
		for(int i=0;i<rightSide.size();i++){
			p+=rightSide.get(i).text()+"  ";
		}
		return p;
	}
	
	public void print(){
		System.out.println(text());
	}
	
	public ArrayList<Term> var(){
		ArrayList<Term> var= new ArrayList<Term>();
		var.addAll(leftSide.inVar());
		var.addAll(leftSide.outVar());
		for(int i=0;i<rightSide.size();i++) {
			var.addAll(rightSide.get(i).inVar());
			var.addAll(rightSide.get(i).outVar());
		}
		return var;
	}
	public void setAParameter(Term p) {
		ArrayList<Term> var= this.var();
		System.out.println("size var:"+var.size());
		for(int i=0;i<var.size();i++) {
			Term t=var.get(i);
			if(t.getName().equals(p.getName())) {
				t.setTerms(p.getTerms());
				t.setValue(p.getValue());
			}
		}
	}
	// this method validate a rule after his read in a json file
	public Rule validate() {
		// the clone of rule force it validation
		return this.clone();
	}
}
