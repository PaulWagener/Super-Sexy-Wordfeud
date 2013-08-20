package wordfeud.views;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JLabel;

import wordfeud.core.mvc.CorePanel;

import net.miginfocom.swing.MigLayout;




public class ResignPanel extends CorePanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6226885860193943779L;
	private JButton yesButton;
	private JButton noButton;
	private JLabel areYouSureLabel;
	
	
	public ResignPanel() {
		this.setName("");
		setLayout(new MigLayout("", "[][][134.00px][134.00px][]", "[14px][][][]"));
		

		areYouSureLabel = new JLabel();
		add(areYouSureLabel, "cell 2 0 2 1,alignx left,aligny top");
		
		yesButton = new JButton("Ja");
		add(yesButton, "cell 2 2,alignx center");
		
		noButton = new JButton("Nee");
		add(noButton, "cell 3 2,alignx center");
		
	}

	public void addNoResignActionListener(ActionListener listener) {
		noButton.addActionListener(listener);
	}
	
	public void addResignActionListener(ActionListener listener) {
		yesButton.addActionListener(listener);
	}
	
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {		
	}
	
	public void setResignLabelName(String labelname) {
		areYouSureLabel.setText(labelname);
	}
}
