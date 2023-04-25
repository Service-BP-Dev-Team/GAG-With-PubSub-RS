package gag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServiceNode implements Serializable{
	
    private String name;
	private List<Term> in= new ArrayList<Term>();
	private List<Term> out= new ArrayList<Term>();
	private String location;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Term> getIn() {
		return (ArrayList<Term>)in;
	}
	public void setIn(ArrayList<Term> in) {
		this.in = in;
	}
	public ArrayList<Term> getOut() {
		return (ArrayList<Term>)out;
	}
	public void setOut(ArrayList<Term> out) {
		this.out = out;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public ArrayList<Term> inVar(){
		ArrayList<Term> var= new ArrayList<Term>();
		for(int i=0;i<in.size();i++){
			var.addAll(in.get(i).var());
		}
		return var;
	}
	
	public ArrayList<Term> outVar(){
		ArrayList<Term> var= new ArrayList<Term>();
		for(int i=0;i<out.size();i++){
			var.addAll(out.get(i).var());
		}
		return var;
	}
	
	public ServiceNode clone(){
		ServiceNode s= new ServiceNode();
		ArrayList<Term> in1= new ArrayList<Term>();
		ArrayList<Term> out1= new ArrayList<Term>();
		s.setName(name);
		s.setLocation(location);
		for(int i=0;i<in.size();i++){
			in1.add(in.get(i).clone());
		}
		for(int i=0;i<out.size();i++){
			out1.add(out.get(i).clone());
		}
		s.setIn(in1);
		s.setOut(out1);
		return s;
	}
	
	public void print(){
		
		System.out.println(text());
		
	}
	
	public String text(){
		String p=name;
		p+="( ";
		  for(int i=0;i<in.size();i++){
			  p+=in.get(i).text()+", ";
		  }
		p+=" )";
		
        p+="[ ";
        for(int i=0;i<out.size();i++){
			  p+=out.get(i).text()+", ";
		  }
		p+=" ]";
		return p;
	}
}
