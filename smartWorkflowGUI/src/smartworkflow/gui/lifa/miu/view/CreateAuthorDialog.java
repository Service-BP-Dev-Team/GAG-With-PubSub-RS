/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import smartworkflow.gui.lifa.miu.util.APPLConstants;
import smartworkflow.gui.lifa.miu.util.ConfigurationManager;
import smartworkflow.gui.lifa.miu.util.Theme;
import smartworkflow.gui.lifa.miu.util.editor.UserInfos;
import smartworkflow.gui.lifa.miu.util.exceptions.ApplException;
import smartworkflow.gui.lifa.miu.view.util.ChatButton;
import smartworkflow.gui.lifa.miu.view.util.ChatDialogModel;


/**
 *
 * @author Ndadji Maxime
 */
public class CreateAuthorDialog extends ChatDialogModel{
	private static final long serialVersionUID = 1L;
    private boolean create = false;
    private JLabel label;
    private JTextField login, host;
    private JPasswordField password;
    private JCheckBox globalWorkflow, synchroDecide;
    private char echoChar;
    private JPanel panel;
    private JScrollPane scroll;
    @SuppressWarnings("rawtypes")
	private UserInfos uInfos;
    private ArrayList<ArrayList<String>> views;
    @SuppressWarnings("rawtypes")
	private JComboBox view;
    
    public CreateAuthorDialog (ConfigurationManager config, boolean modal, JFrame parent, ArrayList<ArrayList<String>> views){
        super(ChatDialogModel.OK_CANCEL_BUTTON, config, parent, config.getLangValue("define_coauthor"), modal, false);
        this.setAlwaysOnTop(false);
        this.setSize(340, 540);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.views = views;
        this.initComponents();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void initComponents() {
        listener = new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if(command.equalsIgnoreCase(CANCEL_KEY)){
                    create = false;
                    closeDialog();
                    return;
                }
                
                if(command.equalsIgnoreCase("show_pass")){
                    if(password.getEchoChar() == '\0'){
                        password.setEchoChar(echoChar);
                        ((ChatButton)e.getSource()).setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/eye.png")));
                    }else{
                        password.setEchoChar('\0');
                        ((ChatButton)e.getSource()).setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/eye_closed.png")));
                    }
                    return;
                }
                
                if(command.equalsIgnoreCase(OK_KEY)){
                    try{
                        char[] p = password.getPassword();
                        String pass = "";
                        for(char c : p)
                            pass += c;
                        if(login.getText() == null || login.getText().trim().isEmpty() || pass.isEmpty())
                            throw new Exception();
                        uInfos = new UserInfos();
                        uInfos.setLogin(APPLConstants.encryptMessage(login.getText()));
                        uInfos.setPassword(APPLConstants.encryptMessage(pass));
                        uInfos.setClientHost(host.getText());
                        uInfos.setCanSeeGlobalWorkflow(globalWorkflow.isSelected());
                        uInfos.setCanDecideSynchro(synchroDecide.isSelected());
                        uInfos.setView(views.get(view.getSelectedIndex()));
                        
                        create = true;
                        closeDialog();
                    }catch(Throwable ex){
                        create = false;
                        stateLabel.setForeground(Color.red);
                        stateLabel.setText(config.getLangValue("ids_required"));
                        stateLabel.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/error.png")));
                    }
                }
            }
        };
        
        panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 220));
        
        label = new JLabel(config.getLangValue("specify_infos"));
        addStep(label);
        
        stepTitle.setPreferredSize(new Dimension(300, 25));
        stateLabel.setPreferredSize(new Dimension(300, 25));
        
        label = new JLabel("");
        label.setPreferredSize(new Dimension(300, 10));
        panel.add(label);
        
        label = new JLabel(config.getLangValue("login")+" :");
        label.setFont(config.getTheme().getMenuFont());
        label.setPreferredSize(new Dimension(300, 25));
        panel.add(label);
        
        login = new JTextField();
        login.setPreferredSize(new Dimension(300, 30));
        login.setFont(config.getTheme().getAreasFont());
        panel.add(login);
        
        label = new JLabel(config.getLangValue("password")+" :");
        label.setFont(config.getTheme().getMenuFont());
        label.setPreferredSize(new Dimension(300, 25));
        panel.add(label);
            
        password = new JPasswordField();
        password.setPreferredSize(new Dimension(245, 30));
        password.setFont(config.getTheme().getAreasFont());
        echoChar = password.getEchoChar();
        panel.add(password);
        
        ChatButton button = new ChatButton(config.getTheme());
        button.setActionCommand("show_pass");
        button.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/eye.png")));
        button.setPreferredSize(new Dimension(49, 30));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setAreaFilled(false);
        button.setToolTipText("<html><body><font family=\""+config.getTheme().getToolTipFont().getFamily()+"\" size=\""+config.getTheme().getToolTipSize()+"\" color=\""+Theme.extractRGB(config.getTheme().getToolTipColor())+"\">"
                +config.getLangValue("show_pass") +"</font></body></html>");
        button.addActionListener(listener);
        panel.add(button);
        
        label = new JLabel(config.getLangValue("host")+" :");
        label.setFont(config.getTheme().getMenuFont());
        label.setPreferredSize(new Dimension(300, 25));
        panel.add(label);
        
        host = new JTextField();
        host.setPreferredSize(new Dimension(300, 30));
        host.setFont(config.getTheme().getAreasFont());
        panel.add(host);
        
        label = new JLabel(config.getLangValue("associate_view")+" :");
        label.setFont(config.getTheme().getMenuFont());
        label.setPreferredSize(new Dimension(300, 25));
        panel.add(label);
        
        view = new JComboBox();
        for(ArrayList<String> v : views)
            view.addItem(v.get(0));
        view.setPreferredSize(new Dimension(300, 30));
        view.setBorder(config.getTheme().getComboBorder());
        view.setFont(config.getTheme().getAreasFont());
        panel.add(view);
        
        globalWorkflow = new JCheckBox(config.getLangValue("global_workflow_view"));
        globalWorkflow.setFont(config.getTheme().getSecondTitleFont());
        globalWorkflow.setPreferredSize(new Dimension(300, 25));
        panel.add(globalWorkflow);
        
        synchroDecide = new JCheckBox(config.getLangValue("synchro_decide"));
        synchroDecide.setFont(config.getTheme().getSecondTitleFont());
        synchroDecide.setPreferredSize(new Dimension(300, 25));
        panel.add(synchroDecide);
        
        label = new JLabel("");
        label.setPreferredSize(new Dimension(300, 10));
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, config.getTheme().getBgColor()));
        panel.add(label);
        
        scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        mainPanel.add(scroll, "1");
        
        okButton.addActionListener(listener);
        okButton.setText(config.getLangValue("save"));
        okButton.setPreferredSize(new Dimension((okButton.getText().length()) * 11, 30));
        
        cancelButton.addActionListener(listener);
        cancelButton.setText(config.getLangValue("close"));
        cancelButton.setPreferredSize(new Dimension((cancelButton.getText().length()) * 11, 30));
        
        this.remove(this.stepPanel);
        try {
            setStep(1);
        } catch (ApplException ex) {
            
        }
    }
    
    public boolean isCreate() {
        return create;
    }

    @SuppressWarnings("rawtypes")
	public UserInfos getuInfos() {
        return uInfos;
    }
    
    public void setError(String error){
        stateLabel.setForeground(Color.red);
        stateLabel.setText(error);
        if(error != null && !error.isEmpty())
            stateLabel.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/error.png")));
    }
}
