package gag.behaviour;

import gag.Term;

import java.util.Hashtable;

public class VariableTable {

	public Hashtable<Integer, Term> table;

	public Hashtable<Integer, Term> getTable() {
		return table;
	}

	public void setTable(Hashtable<Integer, Term> table) {
		this.table = table;
	}
	
	public boolean put(int id, Term at){
		table.put(id, at);
		return true;
	}
	
	public Term get(int id){
		return table.get(id);
	
	}
}
