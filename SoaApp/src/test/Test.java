package test;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gag.Artefact;
import gag.Rule;
import gag.ServiceNode;
import gag.Subscription;
import gag.Term;
import gag.behaviour.GAGProcess;
import gag.behaviour.GAGSpecification;
import gag.behaviour.SubscriptionTable;
import gag.son.SONAdaptator;

public class Test {
	
	public static void testTerms(){
		Term t1 = new Term();
		Term tcons= new Term();
		tcons.setLabel("Cons");
		Term tcons1 = new Term();
		tcons1.setName("b");
		t1.setName("a");
		ArrayList<Term> tab= new ArrayList<Term>();
		tab.add(tcons1);
		tab.add(t1);
		tcons.setTerms(tab);
		Term t2=t1.clone();
		t1.print();
		System.out.println();
		t2.print();
		System.out.println();
		tcons.print();
		System.out.println();
		tcons.clone().print();
	}

	public static void testService(){
		Term t1 = new Term();
		Term tcons= new Term();
		tcons.setLabel("Cons");
		Term tcons1 = new Term();
		tcons1.setName("b");
		t1.setName("a");
		ArrayList<Term> tab= new ArrayList<Term>();
		tab.add(tcons1);
		tab.add(t1);
		tcons.setTerms(tab);
		Term t2=t1.clone();
		//t1.print();
		System.out.println();
		//t2.print();
		System.out.println();
		//tcons.print();
		System.out.println();
		//tcons.clone().print();
		ServiceNode s= new ServiceNode();
		s.setName("service");
		ArrayList<Term> sin = new ArrayList<Term>();
		ArrayList<Term> sout = new ArrayList<Term>();
		sin.add(t2);
		sout.add(tcons);
		s.setIn(sin);
		s.setOut(sout);
		s.print();
		System.out.println();
		s.clone().print();
	}
	
	public static Rule departmentGAGSpecification(){
		
		Rule r=new Rule();
		r.setName("Traitement Département");
		Term parameterTraitement = new Term();
		parameterTraitement.setName("traitementDépartement");
		ServiceNode departementService= new ServiceNode();
		departementService.setName("Service du département");
		departementService.setLocation("département");
		Term dossier= new Term();
		dossier.setName("dossier");
		Term l= new Term();
		l.setName("l");
		Term consTraiteL= new Term();
		consTraiteL.setLabel("Cons");
		ArrayList<Term> consTraiteLTerms = new ArrayList<Term>();
		consTraiteLTerms.add(parameterTraitement);
		consTraiteLTerms.add(l);
		consTraiteL.setTerms(consTraiteLTerms);
		ArrayList<Term> departementServiceIn= new ArrayList<Term>();
		departementServiceIn.add(dossier);
		ArrayList<Term> departementServiceOut= new ArrayList<Term>();
		departementServiceOut.add(consTraiteL);
		departementService.setIn(departementServiceIn);
		departementService.setOut(departementServiceOut);
		
		ServiceNode dschangSchoolService= new ServiceNode();
		dschangSchoolService.setName("Service de la dschangSchool");
		dschangSchoolService.setLocation("dschangSchool");
		ArrayList<Term> dschangSchoolServiceIn= new ArrayList<Term>();
		dschangSchoolServiceIn.add(dossier);
		dschangSchoolServiceIn.add(parameterTraitement);
		ArrayList<Term> dschangSchoolServiceOut= new ArrayList<Term>();
		dschangSchoolServiceOut.add(l);
		dschangSchoolService.setIn(dschangSchoolServiceIn);
		dschangSchoolService.setOut(dschangSchoolServiceOut);
		
		r.setLeftSide(departementService);
		r.getRightSide().add(dschangSchoolService);
		r.print();
		
		System.out.println();
		System.out.println();
		//r.clone().print();
		return r;
	}
	
	public static Artefact startArtefact(){
		Artefact a = new Artefact();
		ServiceNode departementService= new ServiceNode();
		departementService.setName("Service du département");
		departementService.setLocation("département");
		Term dossier= new Term();
		dossier.setName("dossier");
		Term traitement= new Term();
		traitement.setName("traitement");
		ArrayList<Term> departementServiceIn= new ArrayList<Term>();
		departementServiceIn.add(dossier);
		ArrayList<Term> departementServiceOut= new ArrayList<Term>();
		departementServiceOut.add(traitement);
		departementService.setIn(departementServiceIn);
		departementService.setOut(departementServiceOut);
		a.setNode(departementService);
		return a;
	}
	public static void main(String args[]){
		
		
		testTerms();
		testService();
		System.out.println();
		System.out.println();
		departmentGAGSpecification();
		GAGProcess p=new GAGProcess();
		GAGSpecification spec= new GAGSpecification();
		Rule rdept=departmentGAGSpecification();
		spec.getRules().add(rdept);
		p.setArtefact(startArtefact());
		p.setGagSpecification(spec);
		SubscriptionTable LS = new SubscriptionTable();
		p.setSubscriptionTable(LS);
		Subscription sub= new Subscription();
		sub.setLocation("etudiant");
		sub.setVariableName("traitement");
		LS.addSubscription(sub);
		Rule rd=rdept.clone();
		Term param= new Term();
		param.setName("traitementDepartement");
		param.setValue("Hum");
		rd.setAParameter(param);
		rd.print();
		p.getArtefact().match(rd, p.getSubscriptionTable(), new SONAdaptator());
		StructuredLayout layout = new StructuredLayout();
		Fenetre fen= new Fenetre();
		fen.setVisible(true);
		layout.dispose(fen.getPanel(), p);
		//TestGraphics frame = new TestGraphics();
		//frame.setVisible(true);
		//layout.dispose(frame.getPanel(), p);
		//frame.getPanel().removeAll();
		//frame.getPanel().add(new JLabel("HUM QU'EST CE QUI CE PASSE"),BorderLayout.CENTER);
		Gson gson = new GsonBuilder().create();
		System.out.println(gson.toJson(spec));
	    GAGSpecification gag= GAGSpecification.readGAGSpecificationFromResources("/resources/gagdepartement.json");
	    gag.print();
	}
}
