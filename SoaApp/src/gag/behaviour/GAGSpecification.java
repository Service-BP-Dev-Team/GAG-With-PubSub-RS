package gag.behaviour;

import gag.Rule;
import test.Test;
import test.WorkflowFrame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GAGSpecification implements Serializable{

	public String name;
	public List<Rule> rules=new ArrayList<Rule>();

	public ArrayList<Rule> getRules() {
		return (ArrayList<Rule>)rules;
	}

	public void setRules(ArrayList<Rule> rules) {
		this.rules = rules;
	} 
	
	public void print() {
		for(int i=0;i<this.rules.size();i++) {
			System.out.println();
			this.rules.get(i).print();
		}
	}
	
	public GAGSpecification clone() {
		GAGSpecification gag = new GAGSpecification();
		for(int i=0;i<this.rules.size();i++) {
			gag.rules.add(this.rules.get(i).clone());
		}
		return gag;
	}
	// validate a GAG after his reading
	public GAGSpecification validate() {
		
		//the clone of GAG auto validate the GAG
		return this.clone();
	}
	
	
	public static GAGSpecification readGAGSpecificationFromResources(String resourcelink) {
		InputStreamReader reader= new InputStreamReader(WorkflowFrame.class.getResourceAsStream(resourcelink));
		
		String textJson="";
		try {
			
			BufferedReader fichier= new BufferedReader(reader);
			 String ligne ;
			 
		      while ((ligne = fichier.readLine()) != null) {
		    	  textJson+=ligne+"\n";
		      }
			 //System.out.println(textJson);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Gson gson= new GsonBuilder().create();
		GAGSpecification gag= (GAGSpecification) gson.fromJson(textJson, GAGSpecification.class);
		return gag.validate();
	}
	
	public static GAGSpecification readGAGSpecificationFromFile(String path) {
		File f= new File(path);
		FileReader fread;
		String textJson="";
		try {
			fread = new FileReader(f);
			BufferedReader fichier= new BufferedReader(fread);
			 String ligne ;
			 
		      while ((ligne = fichier.readLine()) != null) {
		    	  textJson+=ligne+"\n";
		      }
			 //System.out.println(textJson);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Gson gson= new GsonBuilder().create();
		GAGSpecification gag= (GAGSpecification) gson.fromJson(textJson, GAGSpecification.class);
		return gag.validate();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}
	
	
}
