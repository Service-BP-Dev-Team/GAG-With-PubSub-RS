/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import smartworkflow.gui.lifa.miu.controllers.util.ControllerModel;
import smartworkflow.gui.lifa.miu.util.editor.Parsers;
import smartworkflow.gui.lifa.miu.view.util.PanelModel;

/**
 *
 * @author Ndadji Maxime
 */
public class GlobalWorkflowDetailsPanel extends PanelModel{
	private static final long serialVersionUID = 1L;
    private JPanel mainPanel;
    private JTabbedPane mainPane;
    
    public GlobalWorkflowDetailsPanel(ControllerModel controller) {
        super(controller);
        initComponents();
    }

    public GlobalWorkflowDetailsPanel() {
        initComponents();
    }
    
    @Override
    protected final void initComponents() {
        this.setLayout(new BorderLayout());
        
        this.initPanels();
    }

    public void initPanels() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        mainPane = new JTabbedPane();
        mainPane.setPreferredSize(new Dimension(500, 500));
        mainPane.setMinimumSize(new Dimension(500, 0));
        mainPane.setFont(new Font("Cambria", Font.PLAIN, 13));
        mainPane.setBorder(BorderFactory.createEtchedBorder());
        mainPane.setTabPlacement(JTabbedPane.BOTTOM);
        
        JPanel panel = new JPanel();
        panel.add(Parsers.astToMxGraphWithoutBud("A[C[Aomega[],C[]],B[Comega[],A[]]]"));
        mainPane.add("Initial document", panel);
        
        panel = new JPanel();
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(800, 270));
        p.add(Parsers.astToDerMxGraphWithoutBud("P1[P5[P1[P7[],P4[P3[Comega[],P2[]],P3[P7[],P2[]]]],P7[]],P3[P6[P7[],P7[]],P2[]]]", Parsers.explGram()));
        panel.add(new JScrollPane(p));
        
        JLabel label = new JLabel("Is the result of the consensual merging of the documents below");
        label.setPreferredSize(new Dimension(800, 20));
        label.setFont(new Font("Cambria", Font.BOLD, 20));
        panel.add(label);
        
        p = new JPanel();
        p.setPreferredSize(new Dimension(800, 250));
        p.add(Parsers.astToMxGraphWithoutBud("A[A[B[B[A[],A[]],B[A[]]]],B[A[]]]"));
        label = new JLabel();
        label.setPreferredSize(new Dimension(100, 250));
        p.add(label);
        p.add(Parsers.astToMxGraphWithoutBud("A[C[A[C[],C[],A[],C[],A[]],C[]],C[C[],C[]],A[]]"));
        panel.add(new JScrollPane(p));
        
        mainPane.add("Document 1", panel);
        
        
        mainPanel.add(mainPane);
        
        this.add(mainPanel);
    }
}
