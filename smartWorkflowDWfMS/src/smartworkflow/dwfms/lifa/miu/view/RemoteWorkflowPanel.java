/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeSelectionModel;

import smartworkflow.dwfms.lifa.miu.controllers.util.ControllerModel;
import smartworkflow.dwfms.lifa.miu.models.util.ModelModel;
import smartworkflow.dwfms.lifa.miu.remote.interfaces.CommunicatorInterface;
import smartworkflow.dwfms.lifa.miu.util.APPLConstants;
import smartworkflow.dwfms.lifa.miu.util.ServerInfos;
import smartworkflow.dwfms.lifa.miu.util.Theme;
import smartworkflow.dwfms.lifa.miu.util.editor.LocalEditionWorkflow;
import smartworkflow.dwfms.lifa.miu.view.util.ChatButton;
import smartworkflow.dwfms.lifa.miu.view.util.PanelModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Ndadji Maxime
 */
public class RemoteWorkflowPanel extends PanelModel{
	private static final long serialVersionUID = 1L;
    private JToolBar toolBar, topToolBar;
    private ActionListener toolBarListener;
    private JPanel mainPanel;
    private ServerInfos svInfos;
    private JTextField host;
    private JTree jtree;
    private ArrayList<HashMap<String, String>> workflows;

    public RemoteWorkflowPanel(ControllerModel controller) {
        super(controller);
        this.initComponents();
    }

    public RemoteWorkflowPanel() {
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
                if(command.equals("connect")){
                    connect();
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
                
                if(command.equals("refresh")){
                    refresh();
                    return;
                }
                
                if(command.equals("switch")){
                    if(ModelModel.authentify())
                        //notifier.notifyUserChanged();
                    return;
                }
            }
        };
    }
    
    public void gview(){
        
    }
    
    public void delete(){
        if(jtree != null){
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
        }
    }
    
    public void add(){
        WorkflowCreationDialog dialog = new WorkflowCreationDialog(ModelModel.getConfig(), true, null);
        dialog.showDialog();
        //if(dialog.isCreate())
            //notifier.notifyDistributedWorkflowCreated();
    }
    
    @SuppressWarnings("unchecked")
	public void connect(){
        if(jtree != null){
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
                            CommunicatorInterface communicator = ModelModel.getConfig().getCommunicator(wf.get("server"));
                            String encWf = communicator.getLocalWorkflowFor(wf, encLogin, 
                                    APPLConstants.encryptMessage(ModelModel.getConfig().getUser().get("password")));
                            if(encWf != null){
                                final GsonBuilder builder = new GsonBuilder();
                                final Gson gson = builder.create();
                                LocalEditionWorkflow<String, String> locWf = gson.fromJson(APPLConstants.decryptMessage(encWf), LocalEditionWorkflow.class);
                                ModelModel.getConfig().saveNewLocalEdition(locWf);
                                refresh();
                                //notifier.notifyLocalWorkflowCreated();
                            }else
                                JOptionPane.showMessageDialog(null, ModelModel.getConfig().getLangValue("projection_error_message"), ModelModel.getConfig().getLangValue("projection_error"), 
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }catch(Throwable ex){
                    JOptionPane.showMessageDialog(null, ModelModel.getConfig().getLangValue("projection_error_message"), ModelModel.getConfig().getLangValue("projection_error"), 
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
                +ModelModel.getConfig().getLangValue("connect_to_workflow")+"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/connect_small.png")));
        button.setActionCommand("connect");
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
        
        
        
        topToolBar = new JToolBar(JToolBar.HORIZONTAL);
        topToolBar.setPreferredSize(new Dimension(100, 25));
        topToolBar.setFloatable(false);
        topToolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY.brighter()));
        svInfos = ModelModel.getConfig().getServerInfos();
        
        JLabel server = new JLabel(ModelModel.getConfig().getLangValue("server")+" :");
        server.setPreferredSize(new Dimension(90, 25));
        server.setHorizontalAlignment(JLabel.CENTER);
        server.setFont(ModelModel.getConfig().getTheme().getMenuFont());
        topToolBar.add(server);
        
        host = new JTextField(svInfos.get("host"));
        host.setPreferredSize(new Dimension(160, 25));
        host.setFont(ModelModel.getConfig().getTheme().getAreasFont());
        topToolBar.add(host);
        
        button = new ChatButton();
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("refresh")+"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/search_small.png")));
        button.setActionCommand("refresh");
        button.addActionListener(toolBarListener);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        topToolBar.add(button);
        
        button = new ChatButton();
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("switch_user")+"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/users_small.png")));
        button.setActionCommand("switch");
        button.addActionListener(toolBarListener);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        topToolBar.add(button);
        
        this.add(topToolBar, BorderLayout.NORTH);
    }

    private void updateMainPanel(ArrayList<HashMap<String, String>> workflows) {
        mainPanel.removeAll();
        mainPanel.validate();
        mainPanel.repaint();
        mainPanel.updateUI();
        
        jtree = null;
        if(workflows != null && !workflows.isEmpty()){
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(ModelModel.getConfig().getLangValue("remote_workflows"));
            this.workflows = workflows;
            for(HashMap<String, String> wf : workflows){
                DefaultMutableTreeNode wfp = new DefaultMutableTreeNode(wf.get("name"));
                try{
                    DefaultMutableTreeNode id = new DefaultMutableTreeNode(ModelModel.getConfig().getLangValue("identifier")+" : "+
                            APPLConstants.decryptMessage(wf.get("id")));
                    wfp.add(id);
                    DefaultMutableTreeNode creator = new DefaultMutableTreeNode(ModelModel.getConfig().getLangValue("created_by")+" : "+
                            APPLConstants.decryptMessage(wf.get("creator")));
                    wfp.add(creator);
                    DefaultMutableTreeNode server = new DefaultMutableTreeNode(ModelModel.getConfig().getLangValue("server")+" : "+
                            wf.get("server"));
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
            CommunicatorInterface communicator = ModelModel.getConfig().getCommunicator(host.getText());
            ArrayList<HashMap<String, String>> workf = communicator.getPotentialWorkflow(APPLConstants.encryptMessage(ModelModel.getConfig().getUser().get("login")), 
                    APPLConstants.encryptMessage(ModelModel.getConfig().getUser().get("password")));
            ArrayList<LocalEditionWorkflow<String, String>> locEdits = ModelModel.getConfig().getLocalEditions();
            if(locEdits != null && workf != null){
                ArrayList<HashMap<String, String>> wf = new ArrayList<HashMap<String, String>>();
                boolean isIn;
                for(HashMap<String, String> w : workf){
                    isIn = false;
                    for(LocalEditionWorkflow<String, String> ed : locEdits){
                        if(ed.getWorkflowID().equals(APPLConstants.decryptMessage(w.get("id"))) &&
                                ed.getOwnerLogin().equals(w.get("creator")) &&
                                ed.getWorkflowServer().equals(w.get("server")) &&
                                ed.getWorkflowName().equals(w.get("name"))){
                            isIn = true;
                            break;
                        }
                    }
                    if(!isIn)
                        wf.add(w);
                }
                updateMainPanel(wf);
            }else
                updateMainPanel(workf);
        }catch(Throwable e){
            updateMainPanel(null);
        }
    }
}
