/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import smartworkflow.gui.lifa.miu.util.APPLConstants;
import smartworkflow.gui.lifa.miu.util.ConfigurationManager;
import smartworkflow.gui.lifa.miu.util.ServerInfos;
import smartworkflow.gui.lifa.miu.util.exceptions.ApplException;
import smartworkflow.gui.lifa.miu.view.util.ChatDialogModel;

/**
 *
 * @author Ndadji Maxime
 */
public class SettingServerDialog extends ChatDialogModel{
	private static final long serialVersionUID = 1L;
    private JLabel label;
    private JTextField host;
    private boolean connected = false;
    private ServerInfos svInfos;
    private JPanel panel;
    private JScrollPane scroll;
    
    public SettingServerDialog(final ConfigurationManager config, boolean modal, JFrame parent){
        super(ChatDialogModel.OK_CANCEL_BUTTON, config, parent, config.getLangValue("sv_manage"), modal, false);
        this.setSize(340, 350);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.initComponents();
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                
            }
            
        });
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
                
                if(command.equalsIgnoreCase(OK_KEY)){
                    svInfos.put("host", host.getText());
                    svInfos.put("url", "rmi://"+host.getText()+":"+APPLConstants.PORT);
                    try {
                        config.setServerInfos(svInfos);
                    } catch (ApplException ex) {

                    }
                    
                    if(tryConnection())
                        closeDialog();
                }
            }
        };
        
        svInfos = config.getServerInfos();
        panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 200));
        
        label = new JLabel(config.getLangValue("specify_server_infos"));
        addStep(label);
        
        stepTitle.setPreferredSize(new Dimension(300, 25));
        stateLabel.setPreferredSize(new Dimension(300, 25));
        
        label = new JLabel("");
        label.setPreferredSize(new Dimension(270, 10));
        panel.add(label);
        
        label = new JLabel(config.getLangValue("host")+" :");
        label.setFont(config.getTheme().getMenuFont());
        label.setPreferredSize(new Dimension(300, 25));
        panel.add(label);
        
        host = new JTextField(svInfos.get("host"));
        host.setPreferredSize(new Dimension(300, 30));
        host.setFont(config.getTheme().getAreasFont());
        panel.add(host);
        
        label = new JLabel("");
        label.setPreferredSize(new Dimension(300, 10));
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, config.getTheme().getBgColor()));
        panel.add(label);
        
        scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        mainPanel.add(scroll, "1");
        
        okButton.addActionListener(listener);
        okButton.setText(config.getLangValue("connect"));
        okButton.setPreferredSize(new Dimension((okButton.getText().length()) * 11, 30));
        
        cancelButton.addActionListener(listener);
        cancelButton.setPreferredSize(new Dimension((cancelButton.getText().length()) * 11, 30));
        this.remove(this.stepPanel);
        try {
            setStep(1);
        } catch (ApplException ex) {
            
        }
        
        this.tryConnection();
    }

    private boolean tryConnection() {
        try{
            config.initServer();
            stateLabel.setForeground(Color.GREEN.darker());
            stateLabel.setText(config.getLangValue("connection_established"));
            stateLabel.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/success.png")));
            connected = true;
            return true;
        }catch(Exception ex){
            stateLabel.setForeground(Color.red);
            stateLabel.setText(config.getLangValue("connection_failed"));
            stateLabel.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/error.png")));
            connected = false;
            return false;
        }
    }
    
    public boolean tryLocalConnection(){
        try {
            String localhost = InetAddress.getLocalHost().getHostAddress();
            svInfos.put("host", localhost);
            svInfos.put("url", "rmi://"+localhost+":"+APPLConstants.PORT);
            config.setServerInfos(svInfos);
            return tryConnection();
        } catch (Exception ex) {

        }
        return false;
    }

    public boolean isConnected() {
        return connected;
    }
}
