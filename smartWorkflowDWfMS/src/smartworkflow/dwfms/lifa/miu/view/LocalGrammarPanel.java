/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import smartworkflow.dwfms.lifa.miu.controllers.util.ControllerModel;
import smartworkflow.dwfms.lifa.miu.models.util.ModelModel;
import smartworkflow.dwfms.lifa.miu.util.editor.LocalEditionWorkflow;
import smartworkflow.dwfms.lifa.miu.util.editor.Parsers;
import smartworkflow.dwfms.lifa.miu.util.observer.adapters.ObserverAdapter;
import smartworkflow.dwfms.lifa.miu.view.util.PanelModel;
import smartworkflow.dwfms.lifa.miu.view.util.StyledPanel;

import com.mxgraph.swing.mxGraphComponent;

/**
 *
 * @author Ndadji Maxime
 */
public class LocalGrammarPanel extends PanelModel{
	private static final long serialVersionUID = 1L;
    private LocalEditionWorkflow<String, String> workflow;
    private JPanel grammar, document;
    private JSplitPane splitV;
    private JPanel mainPanel;
    
    public LocalGrammarPanel(ControllerModel controller) {
        super(controller);
        initComponents();
    }

    public LocalGrammarPanel() {
        initComponents();
    }

    public LocalGrammarPanel(LocalEditionWorkflow<String, String> workflow) {
        this.workflow = workflow;
        
        initComponents();
    }
    
    @Override
    protected final void initComponents() {
        this.setLayout(new BorderLayout());
        
        initPanels();
    }

    public void initPanels() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        splitV = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitV.setDividerSize(2);
        
        grammar = new JPanel();
        grammar.setPreferredSize(new Dimension(400, 300));
        grammar.setLayout(new BorderLayout());
        
        ArrayList<String> view = workflow.getView(), v = new ArrayList<String>();
        v.addAll(view);
        v.remove(0);
        
        JLabel label = new JLabel("      "+ModelModel.getConfig().getLangValue("local_view")+" : "+(view.get(0) + " = " + v).replace("[", "{").replace("]", "}"));
        label.setPreferredSize(new Dimension(200, 50));
        label.setFont(new Font("Times", Font.PLAIN, 18));
        label.setHorizontalAlignment(JLabel.LEFT);
        grammar.add(label, BorderLayout.NORTH);
        
        Updater updater = new Updater();
        ProductionsArea prods = new ProductionsArea(updater);
        prods.setPreferredSize(new Dimension(500, 200));
        prods.setBorder(ModelModel.getConfig().getTheme().getComboBorder());
        prods.setFont(ModelModel.getConfig().getTheme().getAreasFont());
        prods.setText(ModelModel.getConfig().getLangValue("wf_local_grammar")+"\n**************************************************************************"
                + "****************************************************************************************\n\n"
                +workflow.getGrammar().getGrammar());
        prods.setEnabled(false);
        
        grammar.add(new JScrollPane(prods));
        
        
        document = new JPanel();
        document.setPreferredSize(new Dimension(300, 300));
        document.setLayout(new BorderLayout());
        
        label = new JLabel("      "+ModelModel.getConfig().getLangValue("local_current_document"));
        label.setPreferredSize(new Dimension(200, 50));
        label.setFont(new Font("Times", Font.PLAIN, 18));
        label.setHorizontalAlignment(JLabel.LEFT);
        document.add(label, BorderLayout.NORTH);
        
        JPanel panel = new StyledPanel(StyledPanel.SIMPLE_GRAY, ModelModel.getConfig());
        
        ArrayList<String> docs = Parsers.toArrayList(workflow.getCurrentDocument());
        ArrayList<mxGraphComponent> graphs = new ArrayList<mxGraphComponent>();
        int h = 0;
        for(String doc : docs){
            mxGraphComponent g = Parsers.astToMxGraphWithoutBud(doc);
            h += g.getHeight();
            graphs.add(Parsers.astToMxGraphWithoutBud(doc));
        }
        
        panel.setPreferredSize(new Dimension(400, h + 100));
        
        for(mxGraphComponent g : graphs){
            panel.add(g);
        }
        
        document.add(new JScrollPane(panel));
        
        splitV.add(grammar);
        splitV.add(document);
        
        splitV.setContinuousLayout(true);
        
        mainPanel.add(splitV);
        
        
        this.add(mainPanel);
    }
    
    public class Updater extends ObserverAdapter{

        public Updater() {
        }
        
    }
}
