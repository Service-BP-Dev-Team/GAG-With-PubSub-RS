package test;

import gag.Artefact;
import gag.Subscription;
import gag.Term;
import gag.behaviour.GAGProcess;

import java.awt.Dimension;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;


/**
* A graph that creates new edges from a given template edge.
*/
public class CustomGraph extends mxGraph {
	
    public static final NumberFormat numberFormat = NumberFormat.getInstance();

   /**
    * Holds the edge to be used as a template for inserting new edges.
    */
   protected Object edgeTemplate;
   
   private GAGProcess process;

   /**
    * Custom graph that defines the alternate edge style to be used when
    * the middle control point of edges is double clicked (flipped).
    */
   public CustomGraph() {
       setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
   }

   /**
    * Sets the edge template to be used to inserting edges.
    */
   public void setEdgeTemplate(Object template) {
       edgeTemplate = template;
   }
   
   
   
// Overrides method to provide a cell label in the display
			public String convertValueToString(Object cell)
			{
				if (cell instanceof mxCell)
				{
					Object value = ((mxCell) cell).getValue();

					if (value instanceof Artefact)
					{
						Artefact a= (Artefact)value;
						return a.getConfigurationLabel();

					}if(value instanceof Term){
						Term t= (Term)value;
						
						return t.getTrueName();
					}
				}

				return super.convertValueToString(cell);
			}
   

   /**
    * Prints out some useful information about the cell in the tooltip.
    */
   public String getToolTipForCell(Object cell) {
       String tip = "<html>";
       /*
       mxGeometry geo = getModel().getGeometry(cell);
       mxCellState state = getView().getState(cell);

       if (getModel().isEdge(cell)) {
           tip += "points={";

           if (geo != null) {
               List<mxPoint> points = geo.getPoints();

               if (points != null) {
                   Iterator<mxPoint> it = points.iterator();

                   while (it.hasNext()) {
                       mxPoint point = it.next();
                       tip += "[x=" + numberFormat.format(point.getX())
                               + ",y=" + numberFormat.format(point.getY())
                               + "],";
                   }

                   tip = tip.substring(0, tip.length() - 1);
               }
           }

           tip += "}<br>";
           tip += "absPoints={";

           if (state != null) {

               for (int i = 0; i < state.getAbsolutePointCount(); i++) {
                   mxPoint point = state.getAbsolutePoint(i);
                   tip += "[x=" + numberFormat.format(point.getX())
                           + ",y=" + numberFormat.format(point.getY())
                           + "],";
               }

               tip = tip.substring(0, tip.length() - 1);
           }

           tip += "}";
       } else {
           tip += "geo=[";

           if (geo != null) {
               tip += "x=" + numberFormat.format(geo.getX()) + ",y="
                       + numberFormat.format(geo.getY()) + ",width="
                       + numberFormat.format(geo.getWidth()) + ",height="
                       + numberFormat.format(geo.getHeight());
           }

           tip += "]<br>";
           tip += "state=[";

           if (state != null) {
               tip += "x=" + numberFormat.format(state.getX()) + ",y="
                       + numberFormat.format(state.getY()) + ",width="
                       + numberFormat.format(state.getWidth())
                       + ",height="
                       + numberFormat.format(state.getHeight());
           }

           tip += "]";
       }

       mxPoint trans = getView().getTranslate();

       tip += "<br>scale=" + numberFormat.format(getView().getScale())
               + ", translate=[x=" + numberFormat.format(trans.getX())
               + ",y=" + numberFormat.format(trans.getY()) + "]";
       */
       
       if (cell instanceof mxCell)
		{
			Object value = ((mxCell) cell).getValue();
			if(value instanceof Term) {
				Term t= (Term) value;
				ArrayList<Subscription> subs=process.getSubscriptionTable().getSubscription(t);
				if(subs.isEmpty()){
				tip+="<p>";
				 tip+="<pre>";
				 tip+=t.htmlJGgraphx();
				 tip+="</pre>";
				tip+="</p>";
				}else{
					tip+="<p>";
					 tip+="<br/>";
					 tip+="<b><u>Subscribers</u></b><br/>";
					 tip+="<ul>";
					 for(int i=0;i<subs.size();i++){
						 tip+="<li>"+subs.get(i).getLocation()+"</li>";
					 }
					 tip+="</ul>";
					 tip+="</br/>";
					tip+="</p>";
				}
			}
		}
       tip += "</html>";
      
       return tip;
   }

   
   /**
    * Overrides the method to use the currently selected edge template for
    * new edges.
    *
    * @param graph
    * @param parent
    * @param id
    * @param value
    * @param source
    * @param target
    * @param style
    * @return
    */
   public Object createEdge(Object parent, String id, Object value,
           Object source, Object target, String style) {
       if (edgeTemplate != null) {
           mxCell edge = (mxCell) cloneCells(new Object[]{edgeTemplate})[0];
           edge.setId(id);

           return edge;
       }

       return super.createEdge(parent, id, value, source, target, style);
   }

public GAGProcess getProcess() {
	return process;
}

public void setProcess(GAGProcess process) {
	this.process = process;
}

   
}
