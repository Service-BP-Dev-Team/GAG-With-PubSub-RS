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
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import smartworkflow.dwfms.lifa.miu.models.util.ModelModel;
import smartworkflow.dwfms.lifa.miu.util.APPLConstants;
import smartworkflow.dwfms.lifa.miu.util.Theme;
import smartworkflow.dwfms.lifa.miu.util.editor.LocalEditionWorkflow;
import smartworkflow.dwfms.lifa.miu.util.observer.adapters.ObserverAdapter;
import smartworkflow.dwfms.lifa.miu.view.util.ChatButton;
import smartworkflow.dwfms.lifa.miu.view.util.FrameModel;

/**
 *
 * @author Ndadji Maxime
 */
public class MainView extends FrameModel{
	private static final long serialVersionUID = 1L;
    
    private JSplitPane splitV, splitH;
    private JPanel mainPanel;
    private JToolBar toolBar;
    private JMenuBar menuBar;
    private LocalWorkflowPanel localWorkflowPanel;
    private RemoteWorkflowPanel remoteWorkflowPanel;
    private ControlPanel controlPanel;
    private JTabbedPane userPane, mainPane, filePane;
    private Updater updater;
    private ActionListener listener;
    private LocalEditionWorkflow<String, String> workflow;

    public MainView() {
        this.setTitle(ModelModel.getConfig().getLangValue("appl_title"));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize((Integer.parseInt(ModelModel.getConfig().getWindowInfos().get("width")) < 900 ? 900 : Integer.parseInt(ModelModel.getConfig().getWindowInfos().get("width"))), 
                (Integer.parseInt(ModelModel.getConfig().getWindowInfos().get("height")) < 650 ? 650 : Integer.parseInt(ModelModel.getConfig().getWindowInfos().get("height"))));
        this.setLocationRelativeTo(null);
        ImageIcon icon = new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/logo.png"));
        this.setIconImage(icon.getImage());
        this.setMinimumSize(new Dimension(900, 650));
        this.setResizable(true);
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                HashMap<String, String> wInfos = new HashMap<String, String>();
                wInfos.put("width", (getSize().width < 900 ? 900 : getSize().width)+"");
                wInfos.put("height", (getSize().height < 650 ? 650 : getSize().height)+"");
                
                try {
                    ModelModel.getConfig().setWindowInfos(wInfos);
                } catch (Exception ex) {
                    
                }
            } 
        });
        
        updater = new Updater();
        
        this.initComponents();
        
        this.setVisible(true);
    }
    
    @Override
    protected final void initComponents() {
        this.initListeners();
        
        this.initPanels();
        
        this.initMenus();
        
        this.initToolBar();
    }
    
    public void initListeners() {
        listener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
            
        };
    }

    public void initPanels() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        splitV = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitH = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        splitH.setDividerSize(2);
        splitV.setDividerSize(2);
        
        userPane = new JTabbedPane();
        userPane.setPreferredSize(new Dimension(248, 300));
        userPane.setMinimumSize(new Dimension(248, 25));
        userPane.setFont(new Font("Cambria", Font.PLAIN, 12));
        userPane.setBorder(BorderFactory.createEtchedBorder());
        
        localWorkflowPanel = new LocalWorkflowPanel();
        localWorkflowPanel.addUpdater(updater);
        userPane.add(ModelModel.getConfig().getLangValue("local_workflows"), localWorkflowPanel);
        
        remoteWorkflowPanel = new RemoteWorkflowPanel();
        remoteWorkflowPanel.addUpdater(updater);
        userPane.add(ModelModel.getConfig().getLangValue("remote_workflows"), remoteWorkflowPanel);
        
        
        userPane.setMnemonicAt(0, KeyEvent.VK_L);
        userPane.setMnemonicAt(1, KeyEvent.VK_R);
        
        filePane = new JTabbedPane();
        filePane.setPreferredSize(new Dimension(248, 250));
        filePane.setMinimumSize(new Dimension(248, 25));
        filePane.setFont(new Font("Cambria", Font.PLAIN, 12));
        filePane.setBorder(BorderFactory.createEtchedBorder());
        filePane.setTabPlacement(JTabbedPane.TOP);
        
        splitH.add(userPane);
        
        mainPane = new JTabbedPane();
        mainPane.setPreferredSize(new Dimension(500, 500));
        mainPane.setMinimumSize(new Dimension(500, 0));
        mainPane.setFont(new Font("Cambria", Font.PLAIN, 13));
        mainPane.setBorder(BorderFactory.createEtchedBorder());
        
        controlPanel = new ControlPanel();
        controlPanel.addUpdater(updater);
        mainPane.add(ModelModel.getConfig().getLangValue("control_panel"), controlPanel);
        
        splitV.add(splitH);
        splitV.add(mainPane);
        
        splitV.setContinuousLayout(true);
        splitH.setContinuousLayout(true);
        
        mainPanel.add(splitV);
        
        this.add(mainPanel);
    }

    public void initMenus() {
        menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY.brighter()));
        
        JMenu menu = new JMenu(ModelModel.getConfig().getLangValue("file"));
        menu.setFont(ModelModel.getConfig().getTheme().getMenuFont());
        
        menuBar.add(menu);
        
        menu = new JMenu(ModelModel.getConfig().getLangValue("run_workflow"));
        menu.setFont(ModelModel.getConfig().getTheme().getMenuFont());
        
        menuBar.add(menu);
        
        menu = new JMenu(ModelModel.getConfig().getLangValue("help"));
        menu.setFont(ModelModel.getConfig().getTheme().getMenuFont());
        
        menuBar.add(menu);
        
        this.setJMenuBar(menuBar);
    }

    private void initToolBar() {
        toolBar = new JToolBar();
        toolBar.setPreferredSize(new Dimension(32, 32));
        toolBar.setBackground(ModelModel.getConfig().getTheme().getToolBarColor());
        
        toolBar.addSeparator(new Dimension(7, 7));
        
        ChatButton button = new ChatButton(ModelModel.getConfig().getTheme());
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("control_panel") +"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/home.png")));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        button.setActionCommand("control");
        button.addActionListener(listener);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(15, 15));
        
        button = new ChatButton(ModelModel.getConfig().getTheme());
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("switch_user") +"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/users.png")));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        button.setActionCommand("switch");
        button.addActionListener(listener);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(3, 3));
        
        button = new ChatButton(ModelModel.getConfig().getTheme());
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("create_workflow") +"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/add_big.png")));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        button.setActionCommand("create");
        button.addActionListener(listener);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(3, 3));
        
        button = new ChatButton(ModelModel.getConfig().getTheme());
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("edit_workflow") +"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/edit.png")));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        button.setActionCommand("edit");
        button.addActionListener(listener);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(3, 3));
        
        button = new ChatButton(ModelModel.getConfig().getTheme());
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("delete_workflow") +"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/trash.png")));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        button.setActionCommand("delete");
        button.addActionListener(listener);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(15, 15));
        
        button = new ChatButton(ModelModel.getConfig().getTheme());
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("connect_to_workflow") +"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/connect.png")));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        button.setActionCommand("connect");
        button.addActionListener(listener);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(3, 3));
        
        button = new ChatButton(ModelModel.getConfig().getTheme());
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("save_workflow") +"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/save.png")));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        button.setActionCommand("save");
        button.addActionListener(listener);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(3, 3));
        
        button = new ChatButton(ModelModel.getConfig().getTheme());
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("synchronize") +"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/synchronize.png")));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        button.setActionCommand("synchronize");
        button.addActionListener(listener);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(3, 3));
        
        button = new ChatButton(ModelModel.getConfig().getTheme());
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("global_view_workflow") +"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/global.png")));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        button.setActionCommand("global");
        button.addActionListener(listener);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(15, 15));
        
        button = new ChatButton(ModelModel.getConfig().getTheme());
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("gui_manage") +"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/gallery.png")));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        button.setActionCommand("gui");
        button.addActionListener(listener);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(15, 15));
        
        button = new ChatButton(ModelModel.getConfig().getTheme());
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("mail_tool") +"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/email.png")));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        button.setActionCommand("mail");
        button.addActionListener(listener);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(3, 3));
        
        button = new ChatButton(ModelModel.getConfig().getTheme());
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("appl_info_tool") +"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/info.png")));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        button.setActionCommand("infos");
        button.addActionListener(listener);
        toolBar.add(button);
        
        toolBar.addSeparator(new Dimension(3, 3));
        
        button = new ChatButton(ModelModel.getConfig().getTheme());
        button.setToolTipText("<html><body><font family=\""+ModelModel.getConfig().getTheme().getToolTipFont().getFamily()+"\" size=\""+ModelModel.getConfig().getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(ModelModel.getConfig().getTheme().getToolTipColor())+"\">"
                +ModelModel.getConfig().getLangValue("help_tool") +"</font></body></html>");
        button.setPreferredSize(new Dimension(25, 25));
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/help.png")));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        button.setActionCommand("help");
        button.addActionListener(listener);
        toolBar.add(button);
        
        
        
        this.add(toolBar, BorderLayout.NORTH);
    }
    
    public void setCurrentWorkflow(LocalEditionWorkflow<String, String> workflow){
        this.workflow = workflow;
        updateMainView();
        if(mainPane.getComponentCount() >= 3)
            mainPane.setSelectedIndex(2);
    }
    
    public void updateMainView(){
        mainPane.removeAll();
        mainPane.validate();
        mainPane.repaint();
        mainPane.updateUI();
        
        mainPane.add(ModelModel.getConfig().getLangValue("control_panel"), controlPanel);
        if(workflow != null){
            LocalGrammarPanel lgp = new LocalGrammarPanel(workflow);
            lgp.addUpdater(updater);
            mainPane.add(ModelModel.getConfig().getLangValue("wf_local_grammar"), lgp);
            
            LocalEditPanel lep = new LocalEditPanel(workflow);
            lep.addUpdater(updater);
            mainPane.add(ModelModel.getConfig().getLangValue("wf_local_edit"), lep);
            
            /*LocalEditDetailsPanel ledp = new LocalEditDetailsPanel(workflow);
            ledp.addUpdater(updater);
            mainPane.add(ModelModel.getConfig().getLangValue("wf_local_edit_details"), ledp);*/
            
            GlobalWorkflowDetailsPanel gwdp = new GlobalWorkflowDetailsPanel();
            mainPane.add(ModelModel.getConfig().getLangValue("wf_local_edit_details"), gwdp);
        }
        
        mainPane.validate();
        mainPane.repaint();
        mainPane.updateUI();
    }
    
    public class Updater extends ObserverAdapter{

        @Override
        public void updateLocalWorkflowCreated() {
            userPane.setSelectedIndex(0);
            localWorkflowPanel.refresh();
        }

        @Override
        public void updateUserChanged() {
            remoteWorkflowPanel.refresh();
            localWorkflowPanel.refresh();
            workflow = null;
            updateMainView();
        }

        @Override
        public void updateDistributedWorkflowCreated() {
            remoteWorkflowPanel.refresh();
            localWorkflowPanel.refresh();
        }

        @Override
        public void updateEditWorkflow(LocalEditionWorkflow<String, String> workflow) {
            setCurrentWorkflow(workflow);
        }
        
    }
}
