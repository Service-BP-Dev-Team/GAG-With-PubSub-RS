/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import smartworkflow.gui.lifa.miu.util.APPLConstants;
import smartworkflow.gui.lifa.miu.util.ConfigurationManager;
import smartworkflow.gui.lifa.miu.util.editor.Grammar;
import smartworkflow.gui.lifa.miu.util.editor.Parsers;
import smartworkflow.gui.lifa.miu.util.exceptions.ApplException;
import smartworkflow.gui.lifa.miu.view.util.ChatDialogModel;

import com.mxgraph.swing.mxGraphComponent;


/**
 *
 * @author Ndadji Maxime
 */
public class PreviewAstDialog extends ChatDialogModel{
	private static final long serialVersionUID = 1L;
    private JLabel label;
    private JPanel panel;
    private JScrollPane scroll;
    private String document;
    private Grammar<String, String> grammar;
    
    public PreviewAstDialog (ConfigurationManager config, boolean modal, JFrame parent, String document, Grammar<String, String> grammar){
        super(ChatDialogModel.CANCEL_BUTTON, config, parent, config.getLangValue("preview"), modal, false);
        this.setAlwaysOnTop(false);
        this.setSize(340, 490);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.document = document;
        this.grammar = grammar;
        this.initComponents();
    }

    private void initComponents() {
        listener = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if(command.equalsIgnoreCase(CANCEL_KEY)){
                    closeDialog();
                    return;
                }
            }
        };
        
        panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 120));
        
        label = new JLabel(config.getLangValue("preview"));
        addStep(label);
        
        stepTitle.setPreferredSize(new Dimension(300, 25));
        stateLabel.setPreferredSize(new Dimension(300, 25));
        
        JPanel graphPanel = new JPanel();
        graphPanel.setPreferredSize(new Dimension(300, 300));
        graphPanel.setLayout(new BorderLayout());
        mxGraphComponent graphComponent = Parsers.astToDerMxGraphWithoutBud(document, grammar);
        graphComponent.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, config.getTheme().getBgColor()));
        graphPanel.add(graphComponent);
        panel.add(graphPanel);
        
        label = new JLabel("");
        label.setPreferredSize(new Dimension(300, 10));
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, config.getTheme().getBgColor()));
        panel.add(label);
        
        scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        mainPanel.add(scroll, "1");
        
        cancelButton.addActionListener(listener);
        cancelButton.setText(config.getLangValue("close"));
        cancelButton.setPreferredSize(new Dimension((cancelButton.getText().length()) * 11, 30));
        
        this.remove(this.stepPanel);
        try {
            setStep(1);
        } catch (ApplException ex) {
            
        }
    }
    
    public void setError(String error){
        stateLabel.setForeground(Color.red);
        stateLabel.setText(error);
        if(error != null && !error.isEmpty())
            stateLabel.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/error.png")));
    }
}
