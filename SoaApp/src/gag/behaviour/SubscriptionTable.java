package gag.behaviour;

import gag.Subscription;
import gag.Term;

import java.util.ArrayList;
import java.util.Collection;

public class SubscriptionTable {

	public ArrayList<Subscription> table = new ArrayList<Subscription>();
	
	public ArrayList<Subscription> getSubscription(Term t){
		ArrayList<Subscription> tabSub = new ArrayList<Subscription>();
		for(int i=0;i<table.size();i++){
			Subscription s1= table.get(i);
			for(int j=0;j<t.getReferences().size();j++){
				String ref=t.getReferences().get(j);
			if( ref.equals(s1.getVariableName())){
                 tabSub.add(s1);				
			}
			}
		}
		return tabSub;
	}
	
	public ArrayList<Subscription> getTable() {
		return table;
	}

	public void setTable(ArrayList<Subscription> table) {
		this.table = table;
	}

	public void addSubscription(Subscription s){
		if(!table.contains(s)){
		table.add(s);
		}
	}
	
	public void removeSubscription(Subscription s){
		table.remove(s);
	}
	
	public void addAll(Collection<Subscription> tab){
		for(Subscription sub : tab){
			this.addSubscription(sub);
		}
	}
	
	public void addSubscriptionTable(SubscriptionTable tab){
		this.addAll(tab.table);
	}
}
