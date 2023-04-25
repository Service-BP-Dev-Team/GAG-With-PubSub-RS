package test;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import casemanagement.Case;
import gag.Artefact;
import gag.ServiceNode;
import gag.Term;
import gag.behaviour.GAGProcess;
import gag.behaviour.GAGProcessList;
import gag.behaviour.GAGSpecification;
import gag.behaviour.SubscriptionTable;
import gag.son.SONAdaptator;

import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;

public class DialogSendCase extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textFirstName;
	private JTextField textName;
	private JTextField textLevel;
	private JTextField textPathContent;
	private JTextArea textMemoirTitle;
	private SONAdaptator orchestrator;
    private static String receiver ="department";
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogSendCase dialog = new DialogSendCase();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogSendCase() {
		setTitle("Submission");
		setBounds(100, 100, 487, 534);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		
		JLabel lblNom = new JLabel("First name");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblNom, 78, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblNom, 52, SpringLayout.WEST, contentPanel);
		contentPanel.add(lblNom);
		
		textFirstName = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textFirstName, -3, SpringLayout.NORTH, lblNom);
		contentPanel.add(textFirstName);
		textFirstName.setColumns(10);
		
		JLabel lblPrnom = new JLabel("Name");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblPrnom, 32, SpringLayout.SOUTH, lblNom);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblPrnom, 0, SpringLayout.WEST, lblNom);
		contentPanel.add(lblPrnom);
		
		textName = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.WEST, textName, 102, SpringLayout.EAST, lblPrnom);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textName, -48, SpringLayout.EAST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textFirstName, 0, SpringLayout.WEST, textName);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textFirstName, 0, SpringLayout.EAST, textName);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textName, -3, SpringLayout.NORTH, lblPrnom);
		contentPanel.add(textName);
		textName.setColumns(10);
		
		JLabel lblNiveau = new JLabel("Level");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblNiveau, 38, SpringLayout.SOUTH, lblPrnom);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblNiveau, 0, SpringLayout.WEST, lblNom);
		contentPanel.add(lblNiveau);
		
		textLevel = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.EAST, textLevel, 0, SpringLayout.EAST, textFirstName);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textLevel, 104, SpringLayout.EAST, lblNiveau);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textLevel, -3, SpringLayout.NORTH, lblNiveau);
		contentPanel.add(textLevel);
		textLevel.setColumns(10);
		
		JLabel lblThme = new JLabel("Theme");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblThme, 47, SpringLayout.SOUTH, lblNiveau);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblThme, 0, SpringLayout.WEST, lblNom);
		contentPanel.add(lblThme);
		
		textMemoirTitle = new JTextArea();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textMemoirTitle, -5, SpringLayout.NORTH, lblThme);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textMemoirTitle, 0, SpringLayout.WEST, textFirstName);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, textMemoirTitle, -98, SpringLayout.SOUTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textMemoirTitle, 0, SpringLayout.EAST, textFirstName);
		contentPanel.add(textMemoirTitle);
		
		JLabel lblPicesJointes = new JLabel("Attachments");
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblPicesJointes, 0, SpringLayout.WEST, lblNom);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, lblPicesJointes, -49, SpringLayout.SOUTH, contentPanel);
		contentPanel.add(lblPicesJointes);
		
		textPathContent = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textPathContent, 24, SpringLayout.SOUTH, textMemoirTitle);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textPathContent, 0, SpringLayout.WEST, textFirstName);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textPathContent, 214, SpringLayout.EAST, lblPicesJointes);
		contentPanel.add(textPathContent);
		textPathContent.setColumns(10);
		
		JButton btnChoisir = new JButton("Choose");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, btnChoisir, 21, SpringLayout.SOUTH, textMemoirTitle);
		sl_contentPanel.putConstraint(SpringLayout.WEST, btnChoisir, 1, SpringLayout.EAST, textPathContent);
		contentPanel.add(btnChoisir);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Send");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						sendCaseToDepartment();
						setVisible(false);
						dispose();
					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public Case getCase() {
		Case cas= new Case();
		cas.setFirstName(textFirstName.getText());
		cas.setName(textName.getText());
		cas.setLevel(textLevel.getText());
		cas.setMemoirTitle(textMemoirTitle.getText());
		return cas;
	}

	public SONAdaptator getOrchestrator() {
		return orchestrator;
	}

	public void setOrchestrator(SONAdaptator orchestrator) {
		this.orchestrator = orchestrator;
	}
	
	public void sendCaseToDepartment(){
		Case cas= getCase();
		Artefact at= new Artefact();
		at.setSons(new ArrayList<Artefact>());
		
		Artefact a = new Artefact();
		at.getSons().add(a);
		ServiceNode departementService= new ServiceNode();
		departementService.setName("Department service");
		departementService.setLocation(DialogSendCase.receiver);
		Term dossier= new Term();
		dossier.setName("file");
		dossier.setValue(cas);
		Term traitement= new Term();
		traitement.setName("processing");
		ArrayList<Term> departementServiceIn= new ArrayList<Term>();
		departementServiceIn.add(dossier);
		ArrayList<Term> departementServiceOut= new ArrayList<Term>();
		departementServiceOut.add(traitement);
		departementService.setIn(departementServiceIn);
		departementService.setOut(departementServiceOut);
		a.setNode(departementService);
		GAGProcess process = new GAGProcess();
		process.setArtefact(at);
		process.setSubscriptionTable(new SubscriptionTable());
		process.setGagSpecification(new GAGSpecification());
		process.setOrchestrator(orchestrator);
		GAGProcessList caseList= new GAGProcessList();
		caseList.getList().add(process);
		orchestrator.setProcessList(caseList);
		orchestrator.invoke(departementService, new SubscriptionTable());
	    orchestrator.hasChangedAndInformListener();
	}
}
