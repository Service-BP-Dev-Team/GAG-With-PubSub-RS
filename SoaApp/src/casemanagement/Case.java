package casemanagement;

import gag.behaviour.GAGProcess;

public class Case {
	private String name;
	private String firstName;
	private String memoirTitle;
	private int id;
	private Object content;
	
	private GAGProcess process;
	private String level;
	private static int count=0;
	
	public Case () {
		count++;
		id=count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMemoirTitle() {
		return memoirTitle;
	}

	public void setMemoirTitle(String memoirTitle) {
		this.memoirTitle = memoirTitle;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public GAGProcess getProcess() {
		return process;
	}

	public void setProcess(GAGProcess process) {
		this.process = process;
	}
	
	public static Case createTestCase() {
		Case cas= new Case();
		cas.setName("NGOUFO TAGUEU");
		cas.setFirstName("JOSKEL");
		cas.setMemoirTitle("Un modèle couplé grammaire - publish/subscribe pour la mise en oeuvre d'un processus métier collaboratif");
		cas.setContent("");
		cas.setLevel("Master 2");
		return cas;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String toString() {
		String st="";
		st+=name+" "+firstName+"\n";
		st+="Niveau: "+level+"\n";
		st+="Titre: "+memoirTitle+"\n";
		
		return st;
	}
	
}
