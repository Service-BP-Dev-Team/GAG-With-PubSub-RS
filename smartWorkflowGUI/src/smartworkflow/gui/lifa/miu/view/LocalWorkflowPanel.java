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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeSelectionModel;

import smartworkflow.gui.lifa.miu.controllers.util.ControllerModel;
import smartworkflow.gui.lifa.miu.models.util.ModelModel;
import smartworkflow.gui.lifa.miu.util.APPLConstants;
import smartworkflow.gui.lifa.miu.util.Theme;
import smartworkflow.gui.lifa.miu.util.editor.LocalEditionWorkflow;
import smartworkflow.gui.lifa.miu.view.util.ChatButton;
import smartworkflow.gui.lifa.miu.view.util.PanelModel;

/**
 *
 * @author Ndadji Maxime
 */
public class LocalWorkflowPanel extends PanelModel{
	private static final long serialVersionUID = 1L;
    private JToolBar toolBar;
    private ActionListener toolBarListener;
    private JPanel mainPanel;
    private JTree jtree;
    private ArrayList<LocalEditionWorkflow<String, String>> workflows;

    public LocalWorkflowPanel(ControllerModel controller) {
        super(controller);
        
        this.initComponents();
    }

    public LocalWorkflowPanel() {
        this.initComponents();
    }
    
    @Override
    protected final void initComponents() {
        this.setLayout(new BorderLayout());
        
        this.initListeners();
        
        this.initPanels();
        
        this.initMenus();
        
        this.initToolBar();
        
        refresh();
    }

    private void initListeners() {
        toolBarListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if(command.equals("edit")){
                    edit();
                    return;
                }
                
                if(command.equals("add")){
                    add();
                    return;
                }
                
                if(command.equals("delete")){
                    delete();
                    return;
                }
                
                if(command.equals("gview")){
                    gview();
                    return;
                }
            }
        };
    }
    
    public void gview(){
        
    }
    
    public void delete(){
        /*if(jtree != null){
            Object path = jtree.getLastSelectedPathComponent();
            if(path != null){
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)path;
                while(node.getLevel() > 1)
                    node = (DefaultMutableTreeNode)node.getParent();
                DefaultMutableTreeNode id = (DefaultMutableTreeNode)node.getFirstChild();
                String idVal = id.toString().split(" : ")[1];
                try{
                    idVal = APPLConstants.encryptMessage(idVal);
                    for(HashMap<String, String> wf : workflows){
                        if(idVal.equals(wf.get("id"))){
                            String encLogin = APPLConstants.encryptMessage(ModelModel.getConfig().getUser().get("login"));
                            if(wf.get("creator").equals(encLogin)){
                                CommunicatorInterface communicator = ModelModel.getConfig().getCommunicator(wf.get("server"));
                                if(JOptionPane.showConfirmDialog(null, ModelModel.getConfig().getLangValue("confirm_delete_message"), ModelModel.getConfig().getLangValue("confirm_delete"), 
                                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION){
                                    boolean deleted = communicator.deleteWorkflow(wf, encLogin, 
                                            APPLConstants.encryptMessage(ModelModel.getConfig().getUser().get("password")));
                                    if(deleted)
                                        refresh();
                                    else
                                        JOptionPane.showMessageDialog(null, ModelModel.getConfig().getLangValue("delete_error_message"), ModelModel.getConfig().getLangValue("delete_error"), 
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }else{
                                JOptionPane.showMessageDialog(null, ModelModel.getConfig().getLangValue("cannot_delete"), ModelModel.getConfig().getLangValue("delete_error"), 
                                        JOptionPane.ERROR_MESSAGE); 
                            }
                        }

                    }
                }catch(Throwable ex){
                    JOptionPane.showMessageDialog(null, ModelModel.getConfig().getLangValue("delete_error_message"), ModelModel.getConfig().getLangValue("delete_error"), 
                            JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(null, ModelModel.getConfig().getLangValue("select_element_message"), ModelModel.getConfig().getLangValue("select_element"), 
                        JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, ModelModel.getConfig().getLangValue("select_element_message"), ModelModel.getConfig().getLangValue("select_element"), 
                    JOptionPane.ERROR_MESSAGE);
        }*/
    }
    
    public void add(){
        WorkflowCreationDialog dialog = new WorkflowCreationDialog(ModelModel.getConfig(), true, null);
        dialog.showDialog();
        //if(dialog.isCreate())
            //notifier.notifyDistributedWorkflowCreated();
    }
    
    public void edit(){
        if(jtree != null){
            Object path = jtree.getLastSelectedPathComponent();
            if(path != null){
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)path;
                while(node.getLevel() > 1)
                    node = (DefaultMutableTreeNode)node.getParent();
                DefaultMutableTreeNode id = (DefaultMutableTreeNode)node.getFirstChild();
                String idVal = id.toString().split(" : ")[1];
                DefaultMutableTreeNode locId = id.getNextSibling();
                String locIdVal = locId.toString().split(" : ")[1];
                try{
                    for(LocalEditionWorkflow<String, String> wf : workflows){
                        if(idVal.equals(wf.getWorkflowID()) && locIdVal.equals(wf.getLocalID())){
                            //notifier.notifyEditWorkflow(wf);
                            break;
                        }
                    }
                }catch(Throwable ex){
                    JOptionPane.showMessageDialog(null, ModelModel.getConfig().getLangValue("edit_error_message"), ModelModel.getConfig().getLangValue("edit_error"), 
                            JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(null, ModelModel.getConfig().getLangValue("select_element_message"), ModelModel.getConfig().getLangValue("select_element"), 
                        JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(null, ModelModel.getConfig().getLangValue("select_element_message"), ModelModel.getConfig().getLangValue("select_element"), 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initPanels() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        this.add(mainPanel);
    }

    private void initMenus() {
        
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
                +ModelModel.getConfig().getLangValue("edit_workflow")+"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/edit_small.png")));
        button.setActionCommand("edit");
        button.addActionListener(toolBarListener);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(6, 6));
        
        button = new ChatButton();
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("create_workflow")+"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/add.png")));
        button.setActionCommand("add");
        button.addActionListener(toolBarListener);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(6, 6));
        
        button = new ChatButton();
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("delete_workflow")+"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/delete.png")));
        button.setActionCommand("delete");
        button.addActionListener(toolBarListener);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(6, 6));
        
        button = new ChatButton();
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("global_view_workflow")+"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/public.png")));
        button.setActionCommand("gview");
        button.addActionListener(toolBarListener);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(6, 6));
        
        this.add(toolBar, BorderLayout.SOUTH);
    }
    
    private void updateMainPanel(ArrayList<LocalEditionWorkflow<String, String>> workflows) {
        mainPanel.removeAll();
        mainPanel.validate();
        mainPanel.repaint();
        mainPanel.updateUI();
        
        jtree = null;
        if(workflows != null && !workflows.isEmpty()){
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(ModelModel.getConfig().getLangValue("local_workflows"));
            this.workflows = workflows;
            for(LocalEditionWorkflow<String, String> wf : workflows){
                DefaultMutableTreeNode wfp = new DefaultMutableTreeNode(wf.getWorkflowName());
                try{
                    DefaultMutableTreeNode id = new DefaultMutableTreeNode(ModelModel.getConfig().getLangValue("identifier")+" : "+
                            wf.getWorkflowID());
                    wfp.add(id);
                    DefaultMutableTreeNode locId = new DefaultMutableTreeNode(ModelModel.getConfig().getLangValue("local_identifier")+" : "+
                            wf.getLocalID());
                    wfp.add(locId);
                    DefaultMutableTreeNode creator = new DefaultMutableTreeNode(ModelModel.getConfig().getLangValue("created_by")+" : "+
                            APPLConstants.decryptMessage(wf.getOwnerLogin()));
                    wfp.add(creator);
                    DefaultMutableTreeNode server = new DefaultMutableTreeNode(ModelModel.getConfig().getLangValue("server")+" : "+
                            wf.getWorkflowServer());
                    wfp.add(server);
                }catch(Throwable e){
                    
                }
                root.add(wfp);
            }
            jtree = new JTree(root);
            jtree.setRootVisible(false);
            DefaultTreeSelectionModel selectMode = new DefaultTreeSelectionModel();
            selectMode.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            jtree.setSelectionModel(selectMode);
            jtree.setFont(new Font("Cambria", Font.PLAIN, 13));
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setViewportView(jtree);
            mainPanel.add(scrollPane);
        }else{
            JLabel label = new JLabel(ModelModel.getConfig().getLangValue("no_workflows"));
            label.setFont(ModelModel.getConfig().getTheme().getSecondTitleFont());
            label.setHorizontalAlignment(JLabel.CENTER);
            mainPanel.add(label);
        }
        
        mainPanel.validate();
        mainPanel.repaint();
        mainPanel.updateUI();
    }
    
    public void refresh(){
        try{
            ArrayList<LocalEditionWorkflow<String, String>> locEdits = ModelModel.getConfig().getLocalEditions();
            updateMainPanel(locEdits);
        }catch(Throwable e){
            updateMainPanel(null);
        }
    }
}
