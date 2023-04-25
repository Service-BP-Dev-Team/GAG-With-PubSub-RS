/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.view;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import smartworkflow.dwfms.lifa.miu.util.editor.Grammar;
import smartworkflow.dwfms.lifa.miu.util.editor.HaskellRunner;
import smartworkflow.dwfms.lifa.miu.util.editor.Parsers;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;

/**
 *
 * @author Ndadji Maxime
 */
public class ConsensusTest extends JFrame{
	private static final long serialVersionUID = 1L;
    public ConsensusTest()
    {
        super("Test du consensus!");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1024, 660);
        this.setLocationRelativeTo(null);
        HaskellRunner runner = new HaskellRunner();
        String hForest = null;
        JPanel panel = new JPanel();
        try{
            hForest = runner.executeCode(":l Engine/ReconciliationConsensusNew.hs\nfreeDouble test");
            hForest = hForest.replace("(", "[");
            hForest = hForest.replace(")", "]");
        }catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        Grammar<String, String> gram = Parsers.explGram();
        ArrayList<String> hGraphs = Parsers.toArrayList(hForest);
        panel.setPreferredSize(new Dimension(1000, hGraphs.size()*75));
        for(String hGraph : hGraphs){
            final mxGraphComponent graphComponent;
            graphComponent = Parsers.astToDerMxGraphWithoutBud(hGraph, gram);
            mxGraph graph = graphComponent.getGraph();
            
            graph.getModel().addListener(mxEvent.CHANGE, new mxIEventListener()
            {
                @Override
                public void invoke(Object sender, mxEventObject evt)
                {
                    graphComponent.validateGraph();
                }
            });

            // Initial validation
            graphComponent.validateGraph();

            panel.add(graphComponent);
        }
        JScrollPane sPane = new JScrollPane(panel);
        sPane.getVerticalScrollBar().setUnitIncrement(50);
        getContentPane().add(sPane);
        this.setVisible(true);
    }
}
