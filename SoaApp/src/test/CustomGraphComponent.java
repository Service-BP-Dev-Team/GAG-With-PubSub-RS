package test;

import gag.Artefact;
import gag.Term;
import gag.behaviour.GAGProcess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JOptionPane;

import org.w3c.dom.Document;

import com.mxgraph.io.mxCodec;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;


public class CustomGraphComponent extends mxGraphComponent {

    /**
     *
     */
    private static final long serialVersionUID = -6833603133512882012L;
    
    private GAGProcess process;
    private StructuredLayout layoutStructure;

    


	public GAGProcess getProcess() {
		return process;
	}

	public void setProcess(GAGProcess process) {
		this.process = process;
	}

	public StructuredLayout getLayoutStructure() {
		return layoutStructure;
	}

	public void setLayoutStructure(StructuredLayout layoutStructure) {
		this.layoutStructure = layoutStructure;
	}

	/**
     *
     * @param graph
     */
    public CustomGraphComponent(mxGraph graph) {
        super(graph);

		// Sets switches typically used in an editor
       // setPageVisible(true);
        //setPageScale(2);
        //setPreferredSize(new Dimension(5000,3000));
        setZoomFactor(1.3);
        setCenterZoom(true);
        setGridVisible(true);
        setToolTips(true);
        zoomOut();
		//setMinimumSize(new Dimension(1000,3000));
        //setPreferPageSize(false);
        
        getConnectionHandler().setCreateTarget(true);

        // Loads the defalt stylesheet from an external file
        mxCodec codec = new mxCodec();
        Document doc = mxUtils.loadDocument(StructuredLayout.class.getResource(
                "/com/mxgraph/examples/swing/resources/basic-style.xml")
                .toString());
        codec.decode(doc.getDocumentElement(), graph.getStylesheet());

        // Sets the background to white
        getViewport().setOpaque(true);
        getViewport().setBackground(Color.WHITE);
        setPreferPageSize(true);
    }

    /**
     * Overrides drop behaviour to set the cell style if the target is not a
     * valid drop target and the cells are of the same type (eg. both
     * vertices or both edges).
     */
    public Object[] importCells(Object[] cells, double dx, double dy,
            Object target, Point location) {
        if (target == null && cells.length == 1 && location != null) {
            target = getCellAt(location.x, location.y);

            if (target instanceof mxICell && cells[0] instanceof mxICell) {
                mxICell targetCell = (mxICell) target;
                mxICell dropCell = (mxICell) cells[0];

                if (targetCell.isVertex() == dropCell.isVertex()
                        || targetCell.isEdge() == dropCell.isEdge()) {
                    mxIGraphModel model = graph.getModel();
                    model.setStyle(target, model.getStyle(cells[0]));
                    graph.setSelectionCell(target);

                    return null;
                }
            }
        }

        return super.importCells(cells, dx, dy, target, location);
    }
    
	public String getEditingValue(Object cell, EventObject trigger)
	{
		if (cell instanceof mxCell)
		{
			Object value = ((mxCell) cell).getValue();
            //mxICell cellParent = ((mxCell) cell).getParent();
            //mxHierarchicalLayout layout=new mxHierarchicalLayout(arg0)
			if (value instanceof Artefact)
			{
				Artefact a= (Artefact)value;
				return a.getConfigurationLabel();

			}if(value instanceof Term){
				Term t= (Term)value;
				
				return t.getTrueName();
			}
		}

		return super.getEditingValue(cell, trigger);
	};

}

