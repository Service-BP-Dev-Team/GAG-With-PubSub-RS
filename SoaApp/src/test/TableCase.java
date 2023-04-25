package test;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import gag.behaviour.GAGProcessList;
import gag.Artefact;
import gag.Rule;
import gag.behaviour.GAGProcess;

public class TableCase extends JTable {

	private GAGProcessList caseList;

	public TableCase() {

	}

	public TableCase(Object[][] data, Object[] title) {
		super(data, title);
		int nbrColumn = this.getColumnCount();
		int nbrRow=this.getRowCount();
		if (nbrColumn >= 2) {
			this.getColumnModel().getColumn(nbrColumn - 1).setCellRenderer(new ButtonRenderer(ButtonRenderer.TREATCASETYPE));
			this.getColumnModel().getColumn(nbrColumn - 2).setCellRenderer(new ButtonRenderer(ButtonRenderer.VIEWCASETYPE));
			this.getColumnModel().getColumn(nbrColumn - 1).setCellEditor(new ButtonEditor(new JCheckBox(),ButtonEditor.TREATCASETYPE));
			this.getColumnModel().getColumn(nbrColumn - 2).setCellEditor(new ButtonEditor(new JCheckBox(),ButtonEditor.VIEWCASETYPE));
			
			this.getColumnModel().getColumn(nbrColumn - 1).setPreferredWidth(40);
			this.getColumnModel().getColumn(nbrColumn - 2).setPreferredWidth(40);
			this.getColumnModel().getColumn(0).setWidth(5);
			this.getColumnModel().getColumn(0).setPreferredWidth(5);
			}
		
		
			this.setRowHeight(40);
		

	}
	public TableCase(Object[][] data, Object[] title,int type){
		super(data,title);
	}
	public GAGProcessList getCaseList() {
		return caseList;
	}

	public void setCaseList(GAGProcessList caseList) {
		this.caseList = caseList;
	}

	// CTRL + SHIFT + O pour générer les imports
	public static class ButtonRenderer extends JButton implements TableCellRenderer {
		public static int TREATCASETYPE=0;
		public static int VIEWCASETYPE=1;
		public int type;
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean isFocus,
				int row, int col) {
			// On écrit dans le bouton ce que contient la cellule
			setText((value != null) ? value.toString() : "");
			if(type==ButtonEditor.TREATCASETYPE) {
				setText("Process file");
			}else {
				setText("Show details");
			}
			// On retourne notre bouton
			return this;
		}

		ButtonRenderer(int type) {
			setPreferredSize(new Dimension(100, 200));
			this.type=type;
			if(type==TREATCASETYPE) {
				setIcon(new ImageIcon(ButtonRenderer.class.getResource("/icons/png/work.png")));
				setBackground(Color.ORANGE);
			}else {
				setIcon(new ImageIcon(ButtonRenderer.class.getResource("/icons/png/detail.png")));
				setBackground(Color.GREEN);
			}
		}
	}

	// CTRL + SHIFT + O pour générer les imports
	public static class ButtonEditor extends DefaultCellEditor {
		protected JButton button;
		private boolean isPushed;
		public static int TREATCASETYPE=0;
		public static int VIEWCASETYPE=1;
		private int type;
		private ButtonListener bListener;

		// Constructeur avec une CheckBox
		public ButtonEditor(JCheckBox checkBox,int type) {
			// Par défaut, ce type d'objet travaille avec un JCheckBox
			super(checkBox);
			// On crée à nouveau un bouton
			button = new JButton();
			button.setOpaque(true);
			// On lui attribue un listener
			bListener = new ButtonListener(type);
			button.addActionListener(bListener);
			this.type=type;
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			// On précise le numéro de ligne à notre listener
			bListener.setRow(row);
			// Idem pour le numéro de colonne
			bListener.setColumn(column);
			// On passe aussi le tableau en paramètre pour des actions potentielles
			bListener.setTable(table);
			// On réaffecte le libellé au bouton
			button.setText((value == null) ? "" : value.toString());
			if(bListener.type==ButtonEditor.TREATCASETYPE) {
				button.setText("Process file");
				button.setIcon(new ImageIcon(ButtonRenderer.class.getResource("/icons/png/work.png")));
				button.setBackground(Color.ORANGE);
			}else {
				button.setText("Show details");
			}
			
			// On renvoie le bouton
			return button;
		}

		// Notre listener pour le bouton
		class ButtonListener implements ActionListener {
			private int column, row;
			private JTable table;
			private int nbre = 0;
			public int type;
			
			public ButtonListener(int type) {
				this.type=type;
			}

			public ButtonListener() {
				
			}

			public void setColumn(int col) {
				this.column = col;
			}

			public void setRow(int row) {
				this.row = row;
			}

			public void setTable(JTable table) {
				this.table = table;
			}

			public void actionPerformed(ActionEvent event) {
				// On affiche un message, mais vous pourriez effectuer les traitements que vous
				// voulez
				System.out.println("coucou du bouton : " + ((JButton) event.getSource()).getText());
				// On affecte un nouveau libellé à une autre cellule de la ligne
				//table.setValueAt("New Value " + (++nbre), this.row, (this.column - 1));
				if(type==ButtonEditor.TREATCASETYPE) {
					DialogApplyRule dialogRule= new DialogApplyRule();
					
								TableCase tCase= (TableCase)table;
								dialogRule.setProcess(tCase.getCaseList().getList().get(row));
								dialogRule.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
								dialogRule.setVisible(true);
								if(!dialogRule.isCanceled()) {
									Rule r=dialogRule.getRule();
									r.print();
									Artefact a= dialogRule.getArtefact();
									GAGProcess gagprocess=dialogRule.getProcess();
									a.match(r, gagprocess.getSubscriptionTable(), gagprocess.getOrchestrator());
									gagprocess.notifyListeners();
								}
								//table.setValueAt("Traiter", this.row, (this.column-1));
								
							
					
				}
			}
		}
	}
}
