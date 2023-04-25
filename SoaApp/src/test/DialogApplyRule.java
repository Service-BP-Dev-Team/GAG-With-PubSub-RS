package test;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import javax.swing.border.EmptyBorder;

import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.border.MatteBorder;

import casemanagement.Case;
import gag.Artefact;
import gag.Rule;
import gag.Term;
import gag.behaviour.GAGProcess;
import gag.behaviour.GAGSpecification;

import java.awt.Color;

public class DialogApplyRule extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JComboBox selectTask;
	private JPanel panelParameter;
	private JComboBox selectRule;
	private GAGProcess process;
	private Hashtable<String, JTextArea> areaParameter;
	private Artefact artefact;
	private Rule rule;
	private boolean canceled=true;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogApplyRule dialog = new DialogApplyRule();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogApplyRule() {
		setBounds(100, 100, 501, 595);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);

		JLabel lblTche = new JLabel("Task");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblTche, 112, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblTche, 48, SpringLayout.WEST, contentPanel);
		contentPanel.add(lblTche);

		selectTask = new JComboBox();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, selectTask, -3, SpringLayout.NORTH, lblTche);
		sl_contentPanel.putConstraint(SpringLayout.WEST, selectTask, 89, SpringLayout.EAST, lblTche);
		sl_contentPanel.putConstraint(SpringLayout.EAST, selectTask, -62, SpringLayout.EAST, contentPanel);
		contentPanel.add(selectTask);

		panelParameter = new JPanel();
		panelParameter.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		sl_contentPanel.putConstraint(SpringLayout.NORTH, panelParameter, 218, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, panelParameter, -25, SpringLayout.SOUTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, panelParameter, 53, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, panelParameter, -61, SpringLayout.EAST, contentPanel);
		contentPanel.add(panelParameter);

		JLabel lblRgleDeGestion = new JLabel("Business rules");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblRgleDeGestion, 30, SpringLayout.SOUTH, lblTche);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblRgleDeGestion, 0, SpringLayout.WEST, lblTche);
		contentPanel.add(lblRgleDeGestion);

		selectRule = new JComboBox();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, selectRule, -3, SpringLayout.NORTH, lblRgleDeGestion);
		sl_contentPanel.putConstraint(SpringLayout.WEST, selectRule, 0, SpringLayout.WEST, selectTask);
		sl_contentPanel.putConstraint(SpringLayout.EAST, selectRule, 0, SpringLayout.EAST, selectTask);
		contentPanel.add(selectRule);

		selectTask.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				ArrayList<Artefact> tasks = process.getArtefact().getCurrentTask();
				String name = (String) selectTask.getSelectedItem();
				for(int i=0;i<tasks.size();i++) {
					if(tasks.get(i).getNode().getName().equals(name)) {
						setArtefact(tasks.get(i));
					}
				}
				validateRules();
			}
		});
		selectRule.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				validateFormulaire();
			}
		});
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						setVisible(false);
						setCanceled(false);
						Rule r=getRule();
						r.print();
						Artefact a= getArtefact();
						GAGProcess gagprocess=getProcess();
						a.match(r, gagprocess.getSubscriptionTable(), gagprocess.getOrchestrator());
						gagprocess.getOrchestrator().hasChangedAndInformListener();
					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						setVisible(false);
						setCanceled(true);
						
					}
				});
			}
		}
	}

	public JComboBox getSelectTask() {
		return selectTask;
	}

	public void setSelectTask(JComboBox selectTask) {
		this.selectTask = selectTask;
	}

	public JPanel getPanelParameter() {
		return panelParameter;
	}

	public void setPanelParameter(JPanel panelParameter) {
		this.panelParameter = panelParameter;
	}

	public JComboBox getSelectRule() {
		return selectRule;
	}

	public void setSelectRule(JComboBox selectRule) {
		this.selectRule = selectRule;
	}

	public GAGProcess getProcess() {
		return process;
	}

	public void setProcess(GAGProcess process) {
		this.process = process;
		this.validateProcess();
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}

	public void validateProcess() {
		selectTask.removeAllItems();
		Case cas = (Case) (process.getArtefact().retrieveOneTermByName("file").getValue());
		setTitle(cas.getName() + " " + cas.getFirstName());
		ArrayList<Artefact> tasks = process.getArtefact().getCurrentTask();
		System.out.println("taille  " + tasks.size());
		for (int i = 0; i < tasks.size(); i++) {

			selectTask.addItem(tasks.get(i).getNode().getName());

		}
		selectTask.validate();

		validateRules();

	}

	public void validateRules() {
		selectRule.removeAllItems();
		String name = (String) selectTask.getSelectedItem();
		ArrayList<Rule> rules = process.findPossibleRuleForATaskByName(name);
		for (int i = 0; i < rules.size(); i++) {

			selectRule.addItem(rules.get(i).getName());

		}
		selectRule.validate();

		validateFormulaire();

	}

	public void validateFormulaire() {

		panelParameter.removeAll();
		String name = (String) selectRule.getSelectedItem();
		if (name != null) {
			//System.out.println("nom  regle" + name);
			Rule r = null;
			GAGSpecification gag = process.getGagSpecification();
			for (int i = 0; i < gag.getRules().size(); i++) {
				if (gag.getRules().get(i).getName().equals(name)) {
					r = gag.getRules().get(i);
					break;
				}
			}
			this.rule=r;
			//r.print();
			GridLayout grdLayout = new GridLayout(r.getParameters().size(), 2);
			panelParameter.setLayout(grdLayout);
			areaParameter = new Hashtable<String, JTextArea>();
			for (int i = 0; i < r.getParameters().size(); i++) {
				panelParameter.add(new JLabel(r.getParameters().get(i).getName()));
				JTextArea area = new JTextArea();
				areaParameter.put(r.getParameters().get(i).getName(), area);
				panelParameter.add(area);
			}
		}
		panelParameter.validate();
	}

	public Artefact getArtefact() {
		return artefact;
	}

	public void setArtefact(Artefact artefact) {
		this.artefact = artefact;
	}

	public Rule getRule() {
		Rule r= rule.clone();
		for(String key:areaParameter.keySet()) {
			Term t= new Term();
			t.setValue(areaParameter.get(key).getText());
			t.setName(key);
			r.setAParameter(t);
		}
		return r;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	
	
	
}
