package gag.son;

import gag.Term;
import gag.behaviour.GAGProcessList;

public interface SONAdaptatorListener {

	public void update(GAGProcessList processList);
	public void updateATerm(String sender,Term t);
}
