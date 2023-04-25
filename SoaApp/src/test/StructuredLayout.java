package test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;

import gag.Artefact;
import gag.Term;
import gag.behaviour.GAGProcess;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

public class StructuredLayout {
	private CustomGraphComponent graphComponent;
    private CustomGraph graph;
    private boolean pageView;
    private JPanel panel;
    private GAGProcess process;
	private mxHierarchicalLayout layoutForParent;
	private Hashtable<String, Object> mapTerm;
    public static String style=mxConstants.STYLE_FILLCOLOR + "=#ffffff";
  //  public static String styleService=style+";"+mxConstants.STYLE_SHAPE+"="+mxConstants.SHAPE_IMAGE+";"+mxConstants.STYLE_IMAGE+"=/icons/png/activity.png;";
    public static String styleService=style+";"+mxConstants.STYLE_SHAPE+"="+mxConstants.SHAPE_ELLIPSE+";";
    public static String styleIN="";
    public static String styleArrowIN=mxConstants.STYLE_ENDARROW + "=" + mxConstants.ARROW_OVAL+";"+mxConstants.STYLE_STARTARROW + "=" + mxConstants.ARROW_OPEN+";";
    public static String styleArrowOut=mxConstants.STYLE_ENDARROW + "=" + mxConstants.ARROW_OPEN+";"+mxConstants.STYLE_STARTARROW + "=" + mxConstants.ARROW_DIAMOND+";";
    public static String styleArrowReturn= mxConstants.STYLE_EDGE + "=" + mxConstants.EDGESTYLE_ENTITY_RELATION+";"+mxConstants.STYLE_DASHED + "=1;" +  mxConstants.STYLE_DASH_PATTERN + "=3;"+mxConstants.STYLE_ENDARROW + "=" + mxConstants.ARROW_OPEN;
    public static String styleWaitCollor=mxConstants.STYLE_STROKECOLOR+"="+"#fb0000;";
    public static String stylePendingCollor=mxConstants.STYLE_STROKECOLOR+"="+"#e8c218;";
    public static String styleFinishCollor=mxConstants.STYLE_STROKECOLOR+"="+"#18e81c;";
    public CustomGraphComponent getGraphComponent() {
		return graphComponent;
	}
	public void setGraphComponent(CustomGraphComponent graphComponent) {
		this.graphComponent = graphComponent;
	}
	
	public boolean isPageView() {
		return pageView;
	}
	public void setPageView(boolean pageView) {
		this.pageView = pageView;
	}
	public JPanel getPanel() {
		return panel;
	}
	public void setPanel(JPanel panel) {
		this.panel = panel;
	}
	public GAGProcess getProcess() {
		return process;
	}
	public void setProcess(GAGProcess process) {
		this.process = process;
	}
	
	
	public CustomGraph getGraph() {
		return graph;
	}
	public void setGraph(CustomGraph graph) {
		this.graph = graph;
	}
	public void dispose(JPanel jpanel, GAGProcess process){
		
		this.panel=jpanel;
		this.process=process;
		this.panel.removeAll();
		this.panel.setLayout(new BorderLayout());
		mapTerm= new Hashtable<String,Object>();
		 mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
	        mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";
	        graph = new CustomGraph();
	        graph.setProcess(process);
	        Object parent = graph.getDefaultParent();
	        //parent = graph.insertVertex(parent, null, "", 100, 50, 80, 30,mxConstants.STYLE_FILLCOLOR+"="+"#FFFFFF");
	        graph.getModel().beginUpdate();
	         layoutForParent = new mxHierarchicalLayout(graph);
	        mxFastOrganicLayout layout = new mxFastOrganicLayout(graph);
	     // set some properties
	        layout.setForceConstant( 40); // the higher, the more separated
	        layout.setDisableEdgeStyle( false); // true transforms the edges and makes them direct lines

	        //layoutForParent.setOrientation(SwingConstants.EAST);
	        try{
	          Artefact a=process.getArtefact();
	          Object v;
	          int startYSubArtefact=100; // integer for layout of position of artefact 
	          if(a.getNode()!=null){
	           v = graph.insertVertex(parent, null, a, 500, 100, a.getNode().getName().length()*10, 100,styleService);

	         //we insert Term in a graph if the rule was not already apply in the artefact
	           //if(a.getLabel()==null){
		          Object tsin=graph.insertVertex(parent, null, "", 20, 100, 80, 30,styleIN+mxConstants.STYLE_STROKECOLOR+"="+"#ffffff;");
		          Object tsout=graph.insertVertex(parent, null, "", 1000, 100, 80, 30,styleIN+mxConstants.STYLE_STROKECOLOR+"="+"#ffffff;");
		          graph.insertEdge(parent, null, "Input", v, tsin,styleArrowIN);
		          graph.insertEdge(parent, null, "Output", v, tsout,styleArrowOut);
		          proceedTerms(a.getNode().getIn(), tsin, graph);
		          proceedOutTerms(a.getNode().getOut(), tsout, graph);
		          layoutForParent.execute(tsin);
		          layoutForParent.execute(tsout);
	           //}
	          }else{
	        	v= graph.insertVertex(parent, null, a, 520, 100, 40, 50,mxConstants.STYLE_SHAPE+"="+mxConstants.SHAPE_ELLIPSE+";");
	        	startYSubArtefact=-300;
	          }
	          
	          //we insert sub artefact in the graph
	         proceedArtefact(a, parent, v,graph,startYSubArtefact);
	          
	        }
	        catch(Exception e){
	        	e.printStackTrace();
	        }
	        finally{
	        
	        	 graph.getModel().endUpdate();
	        }
	        graphComponent = new CustomGraphComponent(graph);
	        
		    graphComponent.setBackground(Color.WHITE);
	        //graphComponent.setSize(new Dimension(500, 500));
	        //graphComponent.setPreferredSize(new Dimension(500,500));
	        graphComponent.setBorder(null);
	        graphComponent.setAutoScroll(false);

		  //  graphComponent.repaint();
	        // graphComponent.validate();
	        // graphComponent.validateGraph();
	        graphComponent.refresh();
	     
	        graphComponent.setLayoutStructure(this);
	        graphComponent.setProcess(process);
	        //graphComponent.repaint();
	        //System.out.println("le genre ci c'est pur dire encore quoi ?");
	        jpanel.add(graphComponent);
	        jpanel.updateUI();
	        
	        jpanel.validate();
	        jpanel.repaint();
	}
	
	public void proceedArtefact(Artefact a,Object parent,Object currentNode, mxGraph graph, int y){
		if(a.getSons()!=null){
		for(int i=0;i<a.getSons().size();i++){
			 Object v = graph.insertVertex(parent, null, a.getSons().get(i), 500, y+500, a.getSons().get(i).getNode().getName().length()*10, 100,styleService);
			 graph.insertEdge(parent, null, "", currentNode, v);
			 
			 //we insert Term in a graph if the rule was not already apply in the artefact
			
			 Object tsin=graph.insertVertex(parent, null, "", 20, y+500, 80, 30,styleIN+mxConstants.STYLE_STROKECOLOR+"="+"#ffffff;");
	          Object tsout=graph.insertVertex(parent, null, "", 1000, y+500, 80, 30,styleIN+mxConstants.STYLE_STROKECOLOR+"="+"#ffffff;");
	          graph.insertEdge(parent, null, "Input", v, tsin,styleArrowIN);
	          graph.insertEdge(parent, null, "Output", v, tsout,styleArrowOut);
	          proceedTerms(a.getSons().get(i).getNode().getIn(), tsin, graph);
	          proceedOutTerms(a.getSons().get(i).getNode().getOut(), tsout, graph);
	          layoutForParent.execute(tsin);
	          layoutForParent.execute(tsout);
			 
	          //we insert subArtefact
			 proceedArtefact(a.getSons().get(i), parent, v, graph,y+500);
		}
		}
	}
	
	public void proceedTerms(ArrayList<Term> terms,Object parent, mxGraph graph){
		for(int i=0;i<terms.size();i++){
			String style=styleIN;
			style+=this.getTermColor(terms.get(i));
			Object v = graph.insertVertex(parent, null, terms.get(i),20, 20, terms.get(i).getTrueName().length()*10, 30,style);
			proceedTerm(terms.get(i),parent,v,graph);
		}
	}
	public void proceedTerm(Term t,Object parent,Object currentNode, mxGraph graph){
		if(t.getTerms()!=null){
		for(int i=0;i<t.getTerms().size();i++){
			String style=styleIN;
			style+=this.getTermColor(t.getTerms().get(i));
			 Object v = graph.insertVertex(parent, null, t.getTerms().get(i), 20, 20, t.getTerms().get(i).getTrueName().length()*10, 30,style);
			 graph.insertEdge(parent, null, "", currentNode, v);
			 
			 proceedTerm(t.getTerms().get(i), parent, v, graph);
		}
		}
	}
	
	public void proceedOutTerms(ArrayList<Term> terms,Object parent, mxGraph graph){
		for(int i=0;i<terms.size();i++){
			String style=styleIN;
			style+=this.getTermColor(terms.get(i));
			
			Object v = graph.insertVertex(parent, null, terms.get(i),20, 20, terms.get(i).getTrueName().length()*10, 30,style);
			if(mapTerm.get(terms.get(i).getName()+terms.get(i).getId())==null){
			mapTerm.put(terms.get(i).getName()+terms.get(i).getId(),v);
			System.out.println("term registered in the map "+ terms.get(i).getName()+terms.get(i).getId() );
			proceedOutTerm(terms.get(i),parent,v,graph);
			}else{
				Object v1=mapTerm.get(terms.get(i).getName()+terms.get(i).getId());
				graph.insertEdge(graph.getDefaultParent(), null, "value return", v, v1,styleArrowReturn);
			}
			
		}
	}
	public void proceedOutTerm(Term t,Object parent,Object currentNode, mxGraph graph){
		if(t.getTerms()!=null){
		for(int i=0;i<t.getTerms().size();i++){
			String style=styleIN;
			style+=this.getTermColor(t.getTerms().get(i));
			 Object v = graph.insertVertex(parent, null, t.getTerms().get(i), 20, 20, t.getTerms().get(i).getTrueName().length()*10, 30,style);
			 
			 
			 graph.insertEdge(parent, null, "", currentNode, v);
			 
			 Term ft=t.getTerms().get(i);
			 if(mapTerm.get(ft.getName()+ft.getId())==null){
				 mapTerm.put(ft.getName()+ft.getId(), v);
                 proceedOutTerm(t.getTerms().get(i), parent, v, graph);
			 }else{
				    Object v1=mapTerm.get(ft.getName()+ft.getId());
					graph.insertEdge(graph.getDefaultParent(), null, "value return", v, v1,styleArrowReturn); 
			 }
		}
		}
	}
	
	public String getTermColor(Term t){
		if(t.isCompletelyDefine()){
			return styleFinishCollor;
		}
		if(t.isDefined()){
			return stylePendingCollor;
		}
		return styleWaitCollor+mxConstants.STYLE_STROKEWIDTH+"=3;";
	}
}
