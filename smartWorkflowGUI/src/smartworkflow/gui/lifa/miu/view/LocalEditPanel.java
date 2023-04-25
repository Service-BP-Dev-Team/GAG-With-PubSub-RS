/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import smartworkflow.gui.lifa.miu.controllers.util.ControllerModel;
import smartworkflow.gui.lifa.miu.models.util.ModelModel;
import smartworkflow.gui.lifa.miu.util.APPLConstants;
import smartworkflow.gui.lifa.miu.util.Theme;
import smartworkflow.gui.lifa.miu.util.editor.LocalEditionWorkflow;
import smartworkflow.gui.lifa.miu.util.editor.Parsers;
import smartworkflow.gui.lifa.miu.util.observer.adapters.ObserverAdapter;
import smartworkflow.gui.lifa.miu.view.util.ChatButton;
import smartworkflow.gui.lifa.miu.view.util.PanelModel;

/**
 *
 * @author Ndadji Maxime
 */
public class LocalEditPanel extends PanelModel{
	private static final long serialVersionUID = 1L;
    private LocalEditionWorkflow<String, String> workflow;
    private JPanel grammar;
    private JPanel mainPanel;
    private JTextArea document;
    private JLabel stateLabel;
    private JToolBar toolBar;
    private ActionListener toolBarListener;
    
    public LocalEditPanel(ControllerModel controller) {
        super(controller);
        initComponents();
    }

    public LocalEditPanel() {
        initComponents();
    }

    public LocalEditPanel(LocalEditionWorkflow<String, String> workflow) {
        this.workflow = workflow;
        
        initComponents();
    }
    
    @Override
    protected final void initComponents() {
        this.setLayout(new BorderLayout());
        
        this.initListeners();
        
        this.initPanels();
        
        this.initToolBar();
    }

    public void initPanels() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        grammar = new JPanel();
        grammar.setPreferredSize(new Dimension(400, 300));
        grammar.setLayout(new BorderLayout());
        
        ArrayList<String> view = workflow.getView(), v = new ArrayList<String>();
        v.addAll(view);
        v.remove(0);
        
        document = new JTextArea();
        document.setPreferredSize(new Dimension(500, 200));
        document.setBorder(ModelModel.getConfig().getTheme().getComboBorder());
        document.setFont(ModelModel.getConfig().getTheme().getAreasFont());
        document.setText(workflow.getCurrentDocument());
        
        grammar.add(new JScrollPane(document));
        
        stateLabel = new JLabel("");
        stateLabel.setPreferredSize(new Dimension(200, 20));
        stateLabel.setFont(new Font("Cambria", Font.ITALIC, 13));
        stateLabel.setHorizontalAlignment(JLabel.LEFT);
        stateLabel.setForeground(Color.red);
        this.add(stateLabel, BorderLayout.SOUTH);
        
        mainPanel.add(grammar);
        
        
        this.add(mainPanel);
    }
    
    private void initListeners() {
        toolBarListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if(command.equals("preview")){
                    preview();
                    return;
                }
                
                if(command.equals("save")){
                    save();
                    return;
                }
            }
        };
    }
    
    private void initToolBar() {
        toolBar = new JToolBar(JToolBar.HORIZONTAL);
        toolBar.setPreferredSize(new Dimension(25, 25));
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY.brighter()));
        //toolBar.setBackground(ModelModel.getConfig().getTheme().getToolBarColor());
        
        toolBar.addSeparator(new Dimension(6, 6));
        
        ChatButton button = new ChatButton();
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("preview")+"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/theme_small.png")));
        button.setActionCommand("preview");
        button.addActionListener(toolBarListener);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(6, 6));
        
        button = new ChatButton();
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("save")+"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/save_small.png")));
        button.setActionCommand("save");
        button.addActionListener(toolBarListener);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(6, 6));
        
        button = new ChatButton();
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("synchronize")+"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/synchronize_small.png")));
        button.setActionCommand("synchronize");
        button.addActionListener(toolBarListener);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        toolBar.add(button);
        
        this.add(toolBar, BorderLayout.NORTH);
    }
    
    public void save(){
        
    }
    
    public void preview(){
        try{
            ArrayList<String> docs = Parsers.toArrayList(document.getText().replace("\n", "").replace("\t", "").replace(" ", ""));
            for(String doc : docs){
                if(Parsers.derToAstWithoutBud(doc, workflow.getGrammar()) == null)
                    throw new Exception("");
            }
            
            PreviewDerForestDialog dialog = new PreviewDerForestDialog(ModelModel.getConfig(), true, null, document.getText().replace("\n", "").replace("\t", "").replace(" ", ""));
            dialog.showDialog();
            
            
            stateLabel.setText("");
            stateLabel.setIcon(null);
        }catch(Exception e){
            stateLabel.setForeground(Color.red);
            stateLabel.setText(ModelModel.getConfig().getLangValue("invalid_document"));
            stateLabel.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/error.png")));
        }
    }
    
    public class Updater extends ObserverAdapter{

        public Updater() {
        }
        
    }
}
