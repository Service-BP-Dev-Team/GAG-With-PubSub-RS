package soaApp.view;

import gag.Rule;
import gag.Subscription;
import gag.Term;
import gag.behaviour.GAGProcess;
import gag.behaviour.GAGSpecification;
import gag.behaviour.SubscriptionTable;
import gag.son.SONAdaptator;
import inria.smarttools.core.component.PropertyMap;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComboBox;

import soaApp.SoaApp;
import test.Fenetre;
import test.Test;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class EditorFrame extends JDialog implements IEditorView {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtAncienEtat;
	private JTextField txtEtatCourant;
	private JTextField txtMsgEnvoye;
	
	
	private SoaApp editor;
	private JTextField txtProvenance;
	private JTextField txtLabelProvenance;
	private JTextField textIdDestinataire;
	private JTextField lblNouveauMsg;
	
	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		try {
			EditorFrame dialog = new EditorFrame();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/
	/**
	 * Create the dialog.
	 */
	public EditorFrame(SoaApp editorp) {
		editor = editorp;
		this.setTitle("Pair " + editor.getIdName());
		
		setBounds(100, 100, 520, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{68, 55, 183, 86, 55, 0};
		gbl_contentPanel.rowHeights = new int[]{20, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JButton btnConnexion = new JButton("Connexion");
			if (editor.getIdName().equals("A")){
				btnConnexion.setVisible(true);
			} 
			else {
				btnConnexion.setVisible(false);
				}
			btnConnexion.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (editor.getIdName().equals("A")){
						editor.connectTo(editor.getIdName(), "D", "D","1","ON","OFF",new PropertyMap());
						//btnConnexion.setVisible(false);
					} 
					
				}
			});
			GridBagConstraints gbc_btnConnexion = new GridBagConstraints();
			gbc_btnConnexion.insets = new Insets(0, 0, 5, 5);
			gbc_btnConnexion.gridx = 2;
			gbc_btnConnexion.gridy = 0;
			contentPanel.add(btnConnexion, gbc_btnConnexion);
		}
		{
			JLabel lblAncienEtat = new JLabel("Ancien Etat");
			GridBagConstraints gbc_lblAncienEtat = new GridBagConstraints();
			gbc_lblAncienEtat.anchor = GridBagConstraints.WEST;
			gbc_lblAncienEtat.insets = new Insets(0, 0, 5, 5);
			gbc_lblAncienEtat.gridx = 1;
			gbc_lblAncienEtat.gridy = 1;
			contentPanel.add(lblAncienEtat, gbc_lblAncienEtat);
		}
		{
			txtAncienEtat = new JTextField();
			txtAncienEtat.setText("xxxxxxx");
			GridBagConstraints gbc_txtAncienEtat = new GridBagConstraints();
			gbc_txtAncienEtat.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtAncienEtat.anchor = GridBagConstraints.NORTHWEST;
			gbc_txtAncienEtat.insets = new Insets(0, 0, 5, 5);
			gbc_txtAncienEtat.gridx = 2;
			gbc_txtAncienEtat.gridy = 1;
			contentPanel.add(txtAncienEtat, gbc_txtAncienEtat);
			txtAncienEtat.setColumns(10);
		}
		{
			JLabel lblEtatCourant = new JLabel("Etat courant");
			GridBagConstraints gbc_lblEtatCourant = new GridBagConstraints();
			gbc_lblEtatCourant.insets = new Insets(0, 0, 5, 5);
			gbc_lblEtatCourant.anchor = GridBagConstraints.WEST;
			gbc_lblEtatCourant.gridx = 1;
			gbc_lblEtatCourant.gridy = 2;
			contentPanel.add(lblEtatCourant, gbc_lblEtatCourant);
		}
		{
			txtEtatCourant = new JTextField();
			txtEtatCourant.setText("xxxxxxx");
			txtEtatCourant.setColumns(10);
			GridBagConstraints gbc_txtEtatCourant = new GridBagConstraints();
			gbc_txtEtatCourant.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtEtatCourant.anchor = GridBagConstraints.NORTHWEST;
			gbc_txtEtatCourant.insets = new Insets(0, 0, 5, 5);
			gbc_txtEtatCourant.gridx = 2;
			gbc_txtEtatCourant.gridy = 2;
			contentPanel.add(txtEtatCourant, gbc_txtEtatCourant);
		}
		{
			lblNouveauMsg = new JTextField();
			GridBagConstraints gbc_lblNouveauMsg = new GridBagConstraints();
			gbc_lblNouveauMsg.insets = new Insets(0, 0, 5, 5);
			gbc_lblNouveauMsg.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblNouveauMsg.gridx = 3;
			gbc_lblNouveauMsg.gridy = 2;
			contentPanel.add(lblNouveauMsg, gbc_lblNouveauMsg);
			lblNouveauMsg.setColumns(10);
		}
		{
			JLabel lblMsgEnvoye = new JLabel("Msg \u00E0 Envoyer");
			GridBagConstraints gbc_lblMsgEnvoye = new GridBagConstraints();
			gbc_lblMsgEnvoye.anchor = GridBagConstraints.EAST;
			gbc_lblMsgEnvoye.insets = new Insets(0, 0, 5, 5);
			gbc_lblMsgEnvoye.gridx = 1;
			gbc_lblMsgEnvoye.gridy = 3;
			contentPanel.add(lblMsgEnvoye, gbc_lblMsgEnvoye);
		}
		{
			txtMsgEnvoye = new JTextField();
			GridBagConstraints gbc_txtMsgEnvoye = new GridBagConstraints();
			gbc_txtMsgEnvoye.insets = new Insets(0, 0, 5, 5);
			gbc_txtMsgEnvoye.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtMsgEnvoye.gridx = 2;
			gbc_txtMsgEnvoye.gridy = 3;
			contentPanel.add(txtMsgEnvoye, gbc_txtMsgEnvoye);
			txtMsgEnvoye.setColumns(10);
		}
		{
			txtLabelProvenance = new JTextField();
			txtLabelProvenance.setText("xxx");
			GridBagConstraints gbc_txtLabelProvenance = new GridBagConstraints();
			gbc_txtLabelProvenance.insets = new Insets(0, 0, 5, 5);
			gbc_txtLabelProvenance.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtLabelProvenance.gridx = 3;
			gbc_txtLabelProvenance.gridy = 3;
			contentPanel.add(txtLabelProvenance, gbc_txtLabelProvenance);
			txtLabelProvenance.setColumns(10);
		}
		{
			txtProvenance = new JTextField();
			txtProvenance.setText("xxx");
			GridBagConstraints gbc_txtProvenance = new GridBagConstraints();
			gbc_txtProvenance.insets = new Insets(0, 0, 5, 0);
			gbc_txtProvenance.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtProvenance.gridx = 4;
			gbc_txtProvenance.gridy = 3;
			contentPanel.add(txtProvenance, gbc_txtProvenance);
			txtProvenance.setColumns(10);
		}
		{
			JLabel lblDestinataire = new JLabel("Destinataire");
			GridBagConstraints gbc_lblDestinataire = new GridBagConstraints();
			gbc_lblDestinataire.anchor = GridBagConstraints.EAST;
			gbc_lblDestinataire.insets = new Insets(0, 0, 0, 5);
			gbc_lblDestinataire.gridx = 1;
			gbc_lblDestinataire.gridy = 4;
			contentPanel.add(lblDestinataire, gbc_lblDestinataire);
		}
		{
			textIdDestinataire = new JTextField();
			GridBagConstraints gbc_textIdDestinataire = new GridBagConstraints();
			gbc_textIdDestinataire.fill = GridBagConstraints.HORIZONTAL;
			gbc_textIdDestinataire.insets = new Insets(0, 0, 0, 5);
			gbc_textIdDestinataire.gridx = 2;
			gbc_textIdDestinataire.gridy = 4;
			contentPanel.add(textIdDestinataire, gbc_textIdDestinataire);
			textIdDestinataire.setColumns(10);
		}
		{
			JButton btnMerge = new JButton("Merge");
			btnMerge.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					switch (editor.getIdName()){
						case "A":
							txtMsgEnvoye.setText(txtEtatCourant.getText()+"#AAAcd#");
							textIdDestinataire.setText("C ou D");
							txtProvenance.setVisible(false);
							lblNouveauMsg.setVisible(false);
							txtLabelProvenance.setVisible(false);
							
							break;
						case "B":
							txtMsgEnvoye.setText(txtEtatCourant.getText()+"#BBB#");
							textIdDestinataire.setText("D");
							txtProvenance.setVisible(false);
							lblNouveauMsg.setVisible(false);
							txtLabelProvenance.setVisible(false);
							break;
						case "C":
							txtMsgEnvoye.setText(txtEtatCourant.getText()+"#CCC#");
							textIdDestinataire.setText("A");
							txtProvenance.setVisible(false);
							lblNouveauMsg.setVisible(false);
							txtLabelProvenance.setVisible(false);
							break;
						case "D":
							txtMsgEnvoye.setText(txtEtatCourant.getText()+"#DDD#");
							textIdDestinataire.setText("A ou B");
							txtProvenance.setVisible(false);
							lblNouveauMsg.setVisible(false);
							txtLabelProvenance.setVisible(false);
							break;
					}
					
					
					
					
				}
			});
			GridBagConstraints gbc_btnMerge = new GridBagConstraints();
			gbc_btnMerge.insets = new Insets(0, 0, 0, 5);
			gbc_btnMerge.gridx = 3;
			gbc_btnMerge.gridy = 4;
			contentPanel.add(btnMerge, gbc_btnMerge);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btForwardTo = new JButton("ForwardTo");
				btForwardTo.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
					//editor.outForwardTo(textIdDestinataire.getText(), txtMsgEnvoye.getText()); // editor.getIdName(): j'espere que getIdName retourne le nom du pair
					txtAncienEtat.setText(txtEtatCourant.getText().toString());
					txtEtatCourant.setText(txtMsgEnvoye.getText());
					/* effacement des label...*/
					txtMsgEnvoye.setText("");
					//txtProvenance.setText("");
					//textIdDestinataire.setText("");
					//lblNouveauMsg.setText("");
					//txtLabelProvenance.setText("");
					txtProvenance.setVisible(false);
					lblNouveauMsg.setVisible(false);
					txtLabelProvenance.setVisible(false);
					}
				});
				btForwardTo.setActionCommand("OK");//?
				buttonPane.add(btForwardTo);
				getRootPane().setDefaultButton(btForwardTo);
			}
			{
				JButton btnReturnto = new JButton("ReturnTo");
				btnReturnto.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						//editor.outReturnTo(textIdDestinataire.getText(), txtMsgEnvoye.getText()); // editor.getIdName(): j'espere que getIdName retourne le nom du pair
						txtAncienEtat.setText(txtEtatCourant.getText().toString());
						txtEtatCourant.setText(txtMsgEnvoye.getText());
						/* effacement des label...*/
						txtMsgEnvoye.setText("");
						//txtProvenance.setText("");
						//textIdDestinataire.setText("");
						//lblNouveauMsg.setText("");
						//txtLabelProvenance.setText("");
						txtProvenance.setVisible(false);
						lblNouveauMsg.setVisible(false);
						txtLabelProvenance.setVisible(false);
						
					}
				});
				buttonPane.add(btnReturnto);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		this.setVisible(true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.rendInvisibleLesInfoProvenanceDoc();
		
		if(editorp.getIdName().equals("departement")){
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {

                       Thread.sleep(10000);
           			Test.testTerms();
        			Test.testService();
        			System.out.println();
        			System.out.println();
        			Test.departmentGAGSpecification();
        			GAGProcess p=new GAGProcess();
        			GAGSpecification spec= new GAGSpecification();
        			Rule rdept=Test.departmentGAGSpecification();
        			spec.getRules().add(rdept);
        			p.setArtefact(Test.startArtefact());
        			p.setGagSpecification(spec);
        			SubscriptionTable LS = new SubscriptionTable();
        			p.setSubscriptionTable(LS);
        			Subscription sub= new Subscription();
        			sub.setLocation("etudiant");
        			sub.setVariableName("traitement");
        			LS.addSubscription(sub);
        			Rule rd=rdept.clone();
        			Term param= new Term();
        			param.setName("traitementDepartement");
        			param.setValue("Hum");
        			rd.setAParameter(param);
        			rd.print();
        			SONAdaptator sona =new SONAdaptator();
        			//sona.setSoaApp(editor);
        			//p.getArtefact().match(rd, new ArrayList<Term>(), p.getSubscriptionTable(), sona);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		}
	}

	@Override
	public void frowardMessageArrived(String expeditor, String message) {
		
		txtAncienEtat.setText(txtEtatCourant.getText().toString());
		txtEtatCourant.setText(message);
		txtProvenance.setText(expeditor);
		txtLabelProvenance.setText("Provenance-->");
		lblNouveauMsg.setText("<---New Msg");
		//txtLabelProvenance.setBackground(arg0);
		rendVisibleLesInfoProvenanceDoc();
		
		
	}
	


	@Override
	public void returnMessageArrived(String expeditor, String message) {
		txtAncienEtat.setText(txtEtatCourant.getText().toString());
		txtEtatCourant.setText(message);
		txtProvenance.setText(expeditor);
		txtLabelProvenance.setText("Provenance-->");
		lblNouveauMsg.setText("<---New Msg");
		rendVisibleLesInfoProvenanceDoc();
				
	}

	@Override
	public void messageSend(String expeditor, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void frowardMessage(String expeditor, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returnMessage(String expeditor, String message) {
		// TODO Auto-generated method stub
		
	}
	
	public void rendVisibleLesInfoProvenanceDoc(){
		lblNouveauMsg.setVisible(true);
		txtLabelProvenance.setVisible(true);
		txtProvenance.setVisible(true);
		
	}
	public void rendInvisibleLesInfoProvenanceDoc(){
		lblNouveauMsg.setVisible(false);
		txtLabelProvenance.setVisible(false);
		txtProvenance.setVisible(false);
	}
	
	

}
