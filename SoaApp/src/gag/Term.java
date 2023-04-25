package gag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Term implements Serializable{

	private static long count = 0;
	private long id;
	private String label;
	private boolean defined = false;
	private List<Term> terms = null;
	private String name;
	private Object value;
	private List<String> references;

	public Term() {
		count++;
		id = count;
		references = new ArrayList<String>();
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isDefined() {
		return defined;
	}

	public void setDefined(boolean defined) {
		this.defined = defined;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		if(name!=null) {
			if(!references.contains(name))
			{
			references.add(name);
			}
		}
	}

	public ArrayList<String> getReferences() {
		return (ArrayList<String>)references;
	}

	public void setReferences(ArrayList<String> references) {
		this.references = references;
	}

	public ArrayList<Term> var() {

		ArrayList<Term> tab = new ArrayList<Term>();
		if (!this.defined) {
			tab.add(this);
			return tab;
		}
		computeVar(tab, this);
		return tab;
	}

	public ArrayList<Term> getTerms() {
		return (ArrayList<Term>)terms;
	}

	public void setTerms(ArrayList<Term> terms) {
		this.terms = terms;
		if (terms != null) {
			this.defined = true;
		}
	}

	public void computeVar(ArrayList<Term> tab, Term term) {
		if (term.terms != null) {
			for (int i = 0; i < term.terms.size(); i++) {
				if (!term.terms.get(i).isDefined()) {
					tab.add(term.terms.get(i));
				} else {
					computeVar(tab, term.terms.get(i));
				}
			}
		}
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
		if(value!=null) {
		this.defined = true;
		}
	}

	public Term clone() {

		Term t = new Term();
		t.setLabel(label);
		t.setDefined(defined);
		t.setValue(value);
		t.setName(name);
		ArrayList<Term> a = null;
		if (terms != null) {
			a = new ArrayList<Term>();
			for (int i = 0; i < terms.size(); i++) {
				a.add(terms.get(i).clone());
			}
			t.setTerms(a);
		}
		return t;
	}

	public void print() {

		System.out.print(name + "-"+id+" " + label + " " + value + " ");
		if(terms!=null){
		for (int i = 0; i < terms.size(); i++) {
			terms.get(i).print();
		}
		}

	}
	
	public String text() {
        String p="";
		p+=name + "-"+id+" " + label + " " + value + " ";
		if(terms!=null){
		for (int i = 0; i < terms.size(); i++) {
			p+=terms.get(i).text();
		}
		}
        return p;
	}
	
	public String getTrueName(){
		if(name!=null){
			return name;
		}else{
			return label;
		}
	}
	
	public String htmlJGgraphx() {
		String htm="";
		   htm+="<b>"+this.getTrueName()+"<b>";
		   if(value !=null) {
			   htm+=" = "+value;
		   }
		   /*
		   if(this.terms!=null) {
			   for(int i=0;i<terms.size();i++) {
				   htm+=this.terms.get(i).html();
			   }
		   }*/
		return htm;
	}
	
	// find a term by his name
	public Term findTermByHisName(String name) {
		if(this.name.equals(name)) {
			return this;
		}else {
			if(terms!=null) {
				for(int i=0;i<terms.size();i++) {
					Term t= terms.get(i).findTermByHisName(name);
					if(t!=null) {
						return t;
					}
				}
			}
		}
		return null;
	}
	
	// find a term by his name and his id
		public Term findTermByHisNameAndId(String name, int id) {
			if(this.name.equals(name) && id==this.id) {
				return this;
			}else {
				if(terms!=null) {
					for(int i=0;i<terms.size();i++) {
						Term t= terms.get(i).findTermByHisNameAndId(name,id);
						if(t!=null) {
							return t;
						}
					}
				}
			}
			return null;
		}
		
  //test if a data is completely define
		public boolean isCompletelyDefine(){
			if(isDefined()){
				if(this.terms!=null && this.terms.size()==0){
					return true;
				}else if((this.terms!=null && this.terms.size()>0)){
					//boolean test=true;
					for(int i=0;i<this.terms.size();i++){
						if(!this.terms.get(i).isCompletelyDefine()){
							return false;
						}
					}
				}
				return true;
			}else{
				return false;
			}
		}
}
