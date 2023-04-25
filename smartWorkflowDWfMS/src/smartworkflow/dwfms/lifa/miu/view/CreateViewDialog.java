/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import smartworkflow.dwfms.lifa.miu.util.APPLConstants;
import smartworkflow.dwfms.lifa.miu.util.ConfigurationManager;
import smartworkflow.dwfms.lifa.miu.util.exceptions.ApplException;
import smartworkflow.dwfms.lifa.miu.view.util.ChatDialogModel;


/**
 *
 * @author Ndadji Maxime
 */
public class CreateViewDialog extends ChatDialogModel{
	private static final long serialVersionUID = 1L;
    private boolean create = false;
    private JLabel label;
    private JTextField name;
    @SuppressWarnings("rawtypes")
	private JList list;
    private JPanel panel;
    private JScrollPane scroll;
    private ArrayList<String> view = new ArrayList<String>(), symbols;
    
    public CreateViewDialog (ConfigurationManager config, boolean modal, JFrame parent, ArrayList<String> symbols){
        super(ChatDialogModel.OK_CANCEL_BUTTON, config, parent, config.getLangValue("define_view"), modal, false);
        this.setAlwaysOnTop(false);
        this.setSize(340, 490);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.symbols = symbols;
        this.initComponents();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void initComponents() {
        listener = new ActionListener(){
            @SuppressWarnings("deprecation")
			@Override
            public void actionPerformed(ActionEvent e) {
                String command = e.getActionCommand();
                if(command.equalsIgnoreCase(CANCEL_KEY)){
                    create = false;
                    closeDialog();
                    return;
                }
                
                
                if(command.equalsIgnoreCase(OK_KEY)){
                    Object[] objs = list.getSelectedValues();
                    for(Object obj : objs)
                        view.add((String)obj);
                    if(!view.isEmpty()){
                        if(name.getText() != null && !name.getText().trim().isEmpty()){
                            if(name.getText().matches("[a-z]([a-zA-Z0-9]{3,})")){
                                view.add(0, name.getText());
                                create = true;
                                closeDialog();
                            }else
                                setError(config.getLangValue("view_name_pattern"));
                        }else
                            setError(config.getLangValue("enter_view_name"));
                    }else{
                        setError(config.getLangValue("choose_symbol"));
                    }
                }
            }
        };
        
        panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 120));
        
        label = new JLabel(config.getLangValue("specify_infos"));
        addStep(label);
        
        stepTitle.setPreferredSize(new Dimension(300, 25));
        stateLabel.setPreferredSize(new Dimension(300, 25));
        
        label = new JLabel("");
        label.setPreferredSize(new Dimension(300, 10));
        panel.add(label);
        
        label = new JLabel(config.getLangValue("view_name")+" :");
        label.setFont(config.getTheme().getMenuFont());
        label.setPreferredSize(new Dimension(300, 25));
        panel.add(label);
        
        name = new JTextField();
        name.setPreferredSize(new Dimension(300, 30));
        name.setFont(config.getTheme().getAreasFont());
        panel.add(name);
        
        label = new JLabel(config.getLangValue("symbols")+" :");
        label.setFont(config.getTheme().getMenuFont());
        label.setPreferredSize(new Dimension(300, 25));
        panel.add(label);
        
        list = new JList(symbols.toArray());
        list.setPreferredSize(new Dimension(300, 55));
        list.setFont(config.getTheme().getAreasFont());
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        panel.add(new JScrollPane(list));
        
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
    
    public void setError(String error){
        stateLabel.setForeground(Color.red);
        stateLabel.setText(error);
        if(error != null && !error.isEmpty())
            stateLabel.setIcon(new ImageIcon(getClass().getResource(APPLConstants.RESOURCES_FOLDER+"images/error.png")));
    }

    public ArrayList<String> getView() {
        return view;
    }
}
