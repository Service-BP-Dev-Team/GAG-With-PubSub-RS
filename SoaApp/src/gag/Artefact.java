package gag;

import gag.behaviour.SubscriptionTable;
import gag.son.SONAdaptator;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.ArrayList;

public class Artefact implements Serializable{

	public String label;
	public ServiceNode node;
	public ArrayList<Artefact> sons = null;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ServiceNode getNode() {
		return node;
	}

	public void setNode(ServiceNode node) {
		this.node = node;
	}

	public ArrayList<Artefact> getSons() {
		return sons;
	}

	public void setSons(ArrayList<Artefact> sons) {
		this.sons = sons;
	}

	public boolean isOpen() {
		return (sons == null);
	}

	public boolean isClosed() {
		return !isOpen();
	}
	
	public ArrayList<Artefact> getCurrentTask(){
		
		ArrayList<Artefact> artArray= new ArrayList<Artefact>();
		   proceedSearchOfCurrentTask(artArray, this);
		return artArray;
	}
	
    public void proceedSearchOfCurrentTask(ArrayList<Artefact> array, Artefact a){
		if(a.isOpen()) {
			array.add(a);
		}
		if(a.getSons()!=null) {
			for(int i=0;i<a.sons.size();i++) {
				proceedSearchOfCurrentTask(array, a.sons.get(i));
			}
		}
		
		
	}

	public Artefact match(Rule rule, SubscriptionTable LS, SONAdaptator sona) {
		// we clone rule to work with in a copie of rule
		Rule r = rule.clone();
		// we create sons for the artefact node
		this.setSonsService(r);
		this.setLabel(rule.getName());
		// we create subscription list for all node
		Hashtable<ServiceNode, SubscriptionTable> Lnode = this
				.createSubscriptionList(r.getRightSide());
		// we apply substitution sigma in
		ServiceNode l = r.getLeftSide();
		for (int i = 0; i < this.node.getIn().size(); i++) {
			Term t = l.getIn().get(i);
			t.setDefined(true);
			t.setValue(this.node.getIn().get(i).getValue());
			t.setTerms(this.node.getIn().get(i).getTerms());
			changeValue(t, r.getRightSide());
		}
		// we apply substitution sigma out
		for (int i = 0; i < this.node.getOut().size(); i++) {
			Term t = this.node.getOut().get(i);
			Term t1 = l.getOut().get(i);
			t.setDefined(true);
			t.setValue(t1.getValue());
			t.setTerms(t1.getTerms());
			/*starting validate the output */
			ArrayList<Term> varT=t.var();
			for(Term subT:varT){
				changeValue(subT, r.getRightSide());
			}
			/* end validate the output */
			// we notify subscribers with the value of data on witch there have
			// subscribe and
			// create subscription list for every actor in charge to execute a
			// service called by the rule
			ArrayList<Subscription> tabSub = LS.getSubscription(t);
			for (int j = 0; j < tabSub.size(); j++) {
				Subscription sub = tabSub.get(j);
				if (t.isDefined()) {
					Term rt = t.clone();
					rt.setId(sub.getVariableId());
					rt.setName(sub.getVariableName());
					rt.setTerms(t.getTerms());
					sona.returnTerm(sub.getLocation(), rt);
					LS.removeSubscription(sub);// we remove subscription after notification
					ArrayList<Term> rtVar = rt.var();
					// we create new subscription for the variable in term that
					// we want to return
					for (int k = 0; k < rtVar.size(); k++) {
						Subscription newSub = new Subscription();
						newSub.setVariableId(rtVar.get(k).getId());
						newSub.setVariableName(rtVar.get(k).getName());
						newSub.setLocation(sub.getLocation());
						ServiceNode serviceInCharge = producer(rtVar.get(k),
								r.getRightSide());
						Lnode.get(serviceInCharge).addSubscription(newSub);
					}
				} else {
					// we have an equation of sort y=x, we add y as a reference
					// on x
					t1.getReferences().add(t.getName());
					ServiceNode serviceInCharge = producer(t1, r.getRightSide());
					if (!serviceInCharge.getLocation().equals(
							this.node.getLocation())) {
						LS.removeSubscription(sub);
						Lnode.get(serviceInCharge).addSubscription(sub);
					}
				}
			}
		}

		// we continue to create subscription list for every actor in charge to
		// execute a service called by the rule
		for (int i = 0; i < r.getRightSide().size(); i++) {
			subscribeToData(r.getRightSide().get(i), r.getRightSide(), Lnode);
		}
		// now we update the local subscription list
		for (int i = 0; i < r.getRightSide().size(); i++) {
			if (r.getRightSide().get(i).getLocation()
					.equals(this.node.getLocation())) {
				LS.addSubscriptionTable(Lnode.get(r.getRightSide().get(i)));
			}
		}

		// now we invoke distant service with the sonadaptator

		for (int i = 0; i < r.getRightSide().size(); i++) {
			if (!r.getRightSide().get(i).getLocation()
					.equals(this.node.getLocation())) {
				ServiceNode distantService = r.getRightSide().get(i);
				SubscriptionTable lsnode = Lnode.get(distantService);
				sona.invoke(distantService, lsnode);
			}
		}

		return this;
	}

	public void setSonsService(Rule r) {
		ArrayList<ServiceNode> services = r.getRightSide();
		sons = new ArrayList<Artefact>();
		for (int i = 0; i < services.size(); i++) {
			Artefact a = new Artefact();
			//a.setLabel(services.get(i).getName());
			a.setNode(services.get(i));
			sons.add(a);
		}
	}

	public void changeValue(Term t, ArrayList<ServiceNode> nodes) {

		for (int i = 0; i < nodes.size(); i++) {
			this.changeValueInNode(t, nodes.get(i));
		}
	}

	public void changeValueInNode(Term t, ServiceNode node) {
		ArrayList<Term> terms = node.inVar();
		terms.addAll(node.outVar());
		for (int i = 0; i < terms.size(); i++) {
			if (terms.get(i).getName().equals(t.getName())) {
				terms.get(i).setId(t.getId());
				terms.get(i).setValue(t.getValue());
				terms.get(i).setTerms(t.getTerms());
			}
		}
	}

	public Hashtable<ServiceNode, SubscriptionTable> createSubscriptionList(
			ArrayList<ServiceNode> services) {
		Hashtable<ServiceNode, SubscriptionTable> h = new Hashtable<ServiceNode, SubscriptionTable>();
		for (int i = 0; i < services.size(); i++) {
			h.put(services.get(i), new SubscriptionTable());
		}

		return h;
	}

	// util function retrieve a service node in charge of production of a data
	public ServiceNode producer(Term t, ArrayList<ServiceNode> services) {
		ServiceNode s = null;
		for (int i = 0; i < services.size(); i++) {
			ArrayList<Term> out = services.get(i).getOut();
			for (int j = 0; j < out.size(); j++) {
				if (out.get(i).getName().equals(t.getName())) {
					return services.get(i);
				}
			}
		}
		return s;
	}

	// util function to subscribe to list of service with list of var
	public void subscribeToData(ServiceNode node,
			ArrayList<ServiceNode> services,
			Hashtable<ServiceNode, SubscriptionTable> Lnode) {
		for (int i = 0; i < node.getIn().size(); i++) {
			Term t = node.getIn().get(i);
			if (!t.isDefined()) {
				ServiceNode sp = producer(t, services);
				Subscription sub = new Subscription();
				sub.setVariableId(t.getId());
				sub.setVariableName(t.getName());
				sub.setLocation(node.getLocation());
				// we add the subscription
				Lnode.get(sp).addSubscription(sub);
			}
		}
	}
	
	//method to retieve a term by his name
	public Term retrieveOneTermByName(String name) {
		Term t=null;
		if(node!=null){
		ArrayList<Term> in = node.getIn();
		ArrayList<Term> out = node.getOut();
		for(int i=0;i<in.size();i++) {
			t=in.get(i).findTermByHisName(name);
			if(t!=null) {return t;}
		}
		for(int i=0;i<out.size();i++) {
			t=out.get(i).findTermByHisName(name);
			if(t!=null) {return t;}
		}
		
		}
		if(sons!=null) {
			for(int i=0;i<sons.size();i++) {
				t=sons.get(i).retrieveOneTermByName(name);
				if(t!=null) {return t;}
			}
		}
		return t;
	}
	
   public Term retrieveTerm(String name,int id) {
	   Term t=null;
	   if(node!=null){
		ArrayList<Term> in = node.getIn();
		ArrayList<Term> out = node.getOut();
		for(int i=0;i<in.size();i++) {
			t=in.get(i).findTermByHisNameAndId(name,id);
			if(t!=null) {return t;}
		}
		for(int i=0;i<out.size();i++) {
			t=out.get(i).findTermByHisNameAndId(name,id);
			if(t!=null) {return t;}
		}
		
	   }
	   if(sons!=null) {
			for(int i=0;i<sons.size();i++) {
				t=sons.get(i).retrieveTerm(name,id);
				if(t!=null) {return t;}
			}
		}
		return t;
	}
   
   public String getConfigurationLabel(){
	   if(label!=null){
		   String st=this.getLabel();
		   if(this.node!=null){
			   st=this.node.getName()+"\n";
			   st+="["+this.getLabel()+"]";
		   }
		   return st;
	   }else if(this.node!=null){
		   return this.node.getName();
	   }
	   return " ";
   }
   
   public ArrayList<Term> getDistantData(String myLocation){
		
		ArrayList<Term> termArray= new ArrayList<Term>();
		   proceedSearchOfDistantData(termArray, this, myLocation);
		return termArray;
	}
	
   public void proceedSearchOfDistantData(ArrayList<Term> array, Artefact a, String myLocation){
		if(a.isOpen() && a.getNode()!=null && !a.getNode().getLocation().equals(myLocation)) {
			array.addAll(a.getNode().getOut());
		}
		if(a.getSons()!=null) {
			for(int i=0;i<a.sons.size();i++) {
				proceedSearchOfDistantData(array, a.sons.get(i),myLocation);
			}
		}
		
		
	}
   
}
