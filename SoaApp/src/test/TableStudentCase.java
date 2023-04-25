package test;

import javax.swing.JCheckBox;

import test.TableCase.ButtonEditor;
import test.TableCase.ButtonRenderer;

public class TableStudentCase extends TableCase{

	public TableStudentCase() {

	}

	public TableStudentCase(Object[][] data, Object[] title) {
		super(data, title,0);
		int nbrColumn = this.getColumnCount();
		
		if (nbrColumn >= 1) {
			
			this.getColumnModel().getColumn(nbrColumn - 1).setCellRenderer(new ButtonRenderer(ButtonRenderer.VIEWCASETYPE));
			this.getColumnModel().getColumn(nbrColumn - 1).setCellEditor(new ButtonEditor(new JCheckBox(),ButtonEditor.VIEWCASETYPE));
			
			this.getColumnModel().getColumn(nbrColumn - 1).setPreferredWidth(40);
			
			this.getColumnModel().getColumn(0).setWidth(5);
			this.getColumnModel().getColumn(0).setPreferredWidth(5);
			}
		
		
			this.setRowHeight(40);
		

	}
}
