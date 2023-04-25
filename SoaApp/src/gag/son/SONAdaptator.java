package gag.son;

import java.util.ArrayList;
import java.util.Collection;

import soaApp.SoaApp;
import gag.Artefact;
import gag.ServiceNode;
import gag.Subscription;
import gag.Term;
import gag.behaviour.GAGProcess;
import gag.behaviour.GAGProcessList;
import gag.behaviour.GAGSpecification;
import gag.behaviour.SubscriptionTable;

public class SONAdaptator implements SONAdaptatorInterface{

	
	private GAGProcessList processList;
	private ArrayList<SONAdaptatorListener> listeners= new ArrayList<SONAdaptatorListener>();
    private SoaApp soaApp;
	
	public void returnTerm(String receiver,Term at){
		System.out.println("Je retourne a "+receiver);
		at.print();
		soaApp.outReturnTo(receiver, at);
	}

	@Override
	public void invoke(ServiceNode node, SubscriptionTable subscriptions) {
		// TODO Auto-generated method stub
		System.out.println("j'invoque ici "+node.getLocation());
		node.print();
		soaApp.outInvokeTo(node.getLocation(), node, subscriptions);
	}

	@Override
	public void execute(String sender,ServiceNode node, SubscriptionTable subscriptions) {
		// TODO Auto-generated method stub
		GAGProcessList pList=getProcessList();
		processList= new GAGProcessList();
		GAGProcess process= new GAGProcess();
		Artefact a= new Artefact();
		a.setNode(node);
		process.setSubscriptionTable(new SubscriptionTable());
		SubscriptionTable LS= process.getSubscriptionTable();
		ArrayList<Term> outVar= node.outVar();
		//we subcribe the person who have invoke the service to the returning value of the service
		for(int i=0;i<outVar.size();i++) {
			Subscription sub = new Subscription();
			sub.setLocation(sender);
			sub.setVariableId(outVar.get(i).getId());
			sub.setVariableName(outVar.get(i).getName());
			LS.addSubscription(sub);
		}
		// we add other subscribe send by the person that have invoke the service
		System.out.println("Souscriptions contenu dans la liste pour le "+soaApp.getIdName());
		for(int i=0;i<subscriptions.getTable().size();i++){
			System.out.println(subscriptions.getTable().get(i).getLocation());
		}
		LS.addSubscriptionTable(subscriptions);
		process.setArtefact(a);
		process.setOrchestrator(this);
		process.setSubscriptionTable(LS);
		//now we put semantic rule to the process by reading gagspecification located in the resource file for the particular actor 
		process.setGagSpecification(getGAGSpecication());
		processList.getList().add(process);
		for(int i=0;i<pList.getList().size();i++){
			processList.getList().add(pList.getList().get(i));
		}
		for(int i=0;i<listeners.size();i++){
			listeners.get(i).update(processList);
		}
	}



	@Override
	public void receiveTerm(String sender, Term at) {
		// TODO Auto-generated method stub
		if(getProcessList().getList().size()>0){
			GAGProcess process= getProcessList().getList().get(0);
			Artefact a= process.getArtefact();
			Term t= a.retrieveTerm(at.getName(), (int)at.getId());
			if(t!=null){
				System.out.println(soaApp.getIdName()+":j'ai trouvé le term "+at.text());
				t.setValue(at.getValue());
				t.setTerms(at.getTerms());
				t.setDefined(true);
				//now we notify listeners
				for(int i=0;i<listeners.size();i++){
					listeners.get(i).update(processList);
					listeners.get(i).updateATerm(sender, t);
				}
			}else{
				System.out.println(soaApp.getIdName()+":je n'ai pas trouvé le term "+at.text());
			}
		}
	}
	



	public ArrayList<SONAdaptatorListener> getListeners() {
		return listeners;
	}


	public void setListeners(ArrayList<SONAdaptatorListener> listeners) {
		this.listeners = listeners;
	}
	

	
	public GAGProcessList getProcessList() {
		if(processList==null) {
			processList=new GAGProcessList();
		}
		return processList;
	}

	public void setProcessList(GAGProcessList processList) {
		this.processList = processList;
	}

	public SoaApp getSoaApp() {
		return soaApp;
	}

	public void setSoaApp(SoaApp soaApp) {
		this.soaApp = soaApp;
	}

	public GAGSpecification getGAGSpecication(){
		String source=null;
		String text="";
		GAGSpecification gag= new GAGSpecification();
		if(soaApp.getIdName().equals("student")){
			source="gagstudent.json";
		}
		else if(soaApp.getIdName().equals("department")){
			source="gagdepartment.json";
		}else if(soaApp.getIdName().equals("DschangSchool")){
			source="gagdschangschool.json";
		}
		else if(soaApp.getIdName().equals("DAAC")){
			source="gagdaac.json";
		}
		else if(soaApp.getIdName().equals("rectorate")){
			source="gagrectorate.json";
		}
		System.out.println(soaApp.getIdName()+"  source "+source);
		if(source!=null){
			gag=GAGSpecification.readGAGSpecificationFromResources("/resources/"+source);
		}
		return gag;
	}

	public void hasChangedAndInformListener(){
		for(int i=0;i<listeners.size();i++){
			listeners.get(i).update(processList);
		}
	}
	
	
}
