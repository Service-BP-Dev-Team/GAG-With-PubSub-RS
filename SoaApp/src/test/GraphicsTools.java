package test;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import casemanagement.Case;
import gag.behaviour.GAGProcessList;
import gag.Artefact;
import gag.Rule;
import gag.ServiceNode;
import gag.Term;
import gag.behaviour.GAGSpecification;

public class GraphicsTools {

	public static JTree getJTree(GAGSpecification gag,String gagName) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Business rules ["+gagName+"]");
		
		JTree jtree=  new JTree(root);
		for(int i=0;i<gag.getRules().size();i++) {
			renderRules(root, gag.getRules().get(i));
		}
		return jtree;
	}
	
	private static DefaultMutableTreeNode renderRules(DefaultMutableTreeNode root,Rule rule) {
		DefaultMutableTreeNode rNode= new DefaultMutableTreeNode(rule.getName());
		root.add(rNode);
		renderService(rNode, rule, rule.getLeftSide());
		rNode.add(new DefaultMutableTreeNode("-->"));
		for(int i=0;i<rule.getRightSide().size();i++) {
			renderService(rNode, rule, rule.getRightSide().get(i));
		}
		return rNode;
	}
	
	private static DefaultMutableTreeNode renderService(DefaultMutableTreeNode root,Rule r, ServiceNode service) {
		DefaultMutableTreeNode sNode= new DefaultMutableTreeNode(service.getName());
		 DefaultMutableTreeNode inNode = new DefaultMutableTreeNode("In");
		 for(int i=0;i<service.getIn().size();i++) {
			 renderTerm(inNode, service,r, service.getIn().get(i));
		 }
		 
		 
		 DefaultMutableTreeNode outNode = new DefaultMutableTreeNode("Out");
		 for(int i=0;i<service.getOut().size();i++) {
			 renderTerm(outNode,service,r, service.getOut().get(i));
		 }
		 sNode.add(inNode);
		 sNode.add(outNode);
		 root.add(sNode);
		return sNode;
	}
	
	private static DefaultMutableTreeNode renderTerm(DefaultMutableTreeNode root,ServiceNode service,Rule r, Term t) {
		DefaultMutableTreeNode tNode = new DefaultMutableTreeNode(t.getTrueName());
		Artefact a= new Artefact();
		if(t.getTerms()!=null) {
			for(int i=0;i<t.getTerms().size();i++) {
				renderTerm(tNode, service,r, t.getTerms().get(i));
			}
		}
		root.add(tNode);
		return tNode;
	}
	
	//method to render a GAGProcessList in a table
	public static JTable render(GAGProcessList list) {
		
		
		//we set the title
		String[] titles= {"NUMBER","FIRST NAME","NAME","LEVEL","THEME","DETAILS","ACTION"};
		Object[][]data = new Object[list.getList().size()][7];
		for(int i=0;i<list.getList().size();i++) {
			Case dossier= (Case)(list.getList().get(i).getArtefact().retrieveOneTermByName("file")).getValue();
			    data[i][0]=dossier.getId();
				data[i][1]=dossier.getName();
				data[i][2]=dossier.getFirstName();
				data[i][3]=dossier.getLevel();
				data[i][4]=dossier.getMemoirTitle();
				data[i][5]="Show details";
				data[i][6]="Process file";
			
		}
		TableCase tCase= new TableCase(data,titles);
		tCase.setCaseList(list);
		return tCase;
	}
	
	//method to render a GAGProcessList in a table
		public static JTable renderForStudent(GAGProcessList list) {
			
			
			//we set the title
			String[] titles= {"NUMBER","FIRST NAME","NAME","LEVEL","THEME","STATUS"};
			Object[][]data = new Object[list.getList().size()][6];
			for(int i=0;i<list.getList().size();i++) {
				Case dossier= (Case)(list.getList().get(i).getArtefact().retrieveOneTermByName("file")).getValue();
				    data[i][0]=dossier.getId();
					data[i][1]=dossier.getName();
					data[i][2]=dossier.getFirstName();
					data[i][3]=dossier.getLevel();
					data[i][4]=dossier.getMemoirTitle();
					data[i][5]="Show details";;
				
			}
			TableStudentCase tCase= new TableStudentCase(data,titles);
			tCase.setCaseList(list);
			return tCase;
		}
}
