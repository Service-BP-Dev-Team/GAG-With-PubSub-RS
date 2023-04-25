package gag.son;

import java.util.Collection;

import gag.ServiceNode;
import gag.Term;
import gag.behaviour.SubscriptionTable;

public interface SONAdaptatorInterface {

	
	public void returnTerm(String receiver,Term at);
	public void receiveTerm(String sender,Term at);
	public void invoke(ServiceNode node, SubscriptionTable subscription);
	public void execute(String sender,ServiceNode node, SubscriptionTable subscription);
}
