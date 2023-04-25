package gag.behaviour;

import java.util.ArrayList;

import gag.Artefact;
import gag.Rule;
import gag.son.SONAdaptator;

public class GAGProcess {

	public Artefact artefact;
	public SubscriptionTable subscriptionTable;
	public GAGSpecification gagSpecification;
	public ArrayList<GAGProcessListener> listeners= new ArrayList<GAGProcessListener>();
	public SONAdaptator orchestrator; 
	
	public Artefact getArtefact() {
		return artefact;
	}
	public void setArtefact(Artefact artefact) {
		this.artefact = artefact;
	}
	public SubscriptionTable getSubscriptionTable() {
		return subscriptionTable;
	}
	public void setSubscriptionTable(SubscriptionTable subscriptionTable) {
		this.subscriptionTable = subscriptionTable;
	}
	public GAGSpecification getGagSpecification() {
		return gagSpecification;
	}
	public void setGagSpecification(GAGSpecification gagSpecification) {
		this.gagSpecification = gagSpecification;
	}
	
	public ArrayList<Rule> findPossibleRuleForATask(Artefact a){
		ArrayList<Rule> array= new ArrayList<Rule>();
		for(int i=0;i<gagSpecification.getRules().size();i++) {
			if(gagSpecification.getRules().get(i).getLeftSide().getName().equals(a.getNode().getName())) {
				array.add(gagSpecification.getRules().get(i));
			}
		}
		return array;
	}
	
	public ArrayList<Rule> findPossibleRuleForATaskByName(String task){
		ArrayList<Rule> array= new ArrayList<Rule>();
		for(int i=0;i<gagSpecification.getRules().size();i++) {
			if(gagSpecification.getRules().get(i).getLeftSide().getName().equals(task)) {
				array.add(gagSpecification.getRules().get(i));
			}
		}
		return array;
	}
	public ArrayList<GAGProcessListener> getListeners() {
		return listeners;
	}
	public void setListeners(ArrayList<GAGProcessListener> listeners) {
		this.listeners = listeners;
	}
	
	public void notifyListeners() {
		
		for(int i=0;i<listeners.size();i++) {
			listeners.get(i).notify(this);
		}
	}
	public SONAdaptator getOrchestrator() {
		return orchestrator;
	}
	public void setOrchestrator(SONAdaptator orchestrator) {
		this.orchestrator = orchestrator;
	}
	
	
}
