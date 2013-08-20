package wordfeud.views;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import wordfeud.controllers.ChallengeModel;
import wordfeud.core.mvc.CorePanel;

@SuppressWarnings("serial")
public class ChallengeView extends CorePanel {

	private JButton acceptButton;
	private JButton declineButton;
	private JButton backButton;
	private JLabel challengeLabel;
	private JList<ChallengeModel> challengeList;
	private JScrollPane scrollPane;

	public ChallengeView() {

		setLayout(new MigLayout("",
				"[150px:150px:150px,grow][100px:100px:100px,grow]",
				"[14px][40px:40px:40px,grow][40px:40px:40px,grow][90.00][grow]"));
		challengeLabel = new JLabel("CompetitieID, Speler");
		add(challengeLabel, "cell 0 0 2 1,alignx left,aligny top");

		scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 1 1 3,grow");

		challengeList = new JList<ChallengeModel>();
		scrollPane.setViewportView(challengeList);

		acceptButton = new JButton("Accepteer");
		add(acceptButton, "cell 1 1,alignx center");

		declineButton = new JButton("Weiger");
		add(declineButton, "cell 1 2,alignx center");

		backButton = new JButton("Ga terug");
		add(backButton, "cell 0 4");

	}

	public void addActionListenerAccept(ActionListener listener) {
		acceptButton.addActionListener(listener);
	}

	public void addActionListenerBack(ActionListener listener) {
		backButton.addActionListener(listener);
	}

	public void addActionListenerDecline(ActionListener listener) {
		declineButton.addActionListener(listener);
	}

	public void fillChallengeList(ChallengeModel[] challengeModels) {

		challengeList.setListData(challengeModels);
		if (challengeList.getModel().getSize() > 0) {
			acceptButton.setEnabled(true);
			declineButton.setEnabled(true);
		} else {
			acceptButton.setEnabled(false);
			declineButton.setEnabled(false);
		}
	}

	public void clearChallengeList() {
		challengeList.removeAll();
	}

	public ChallengeModel getSelectedChallenge() {
		return challengeList.getSelectedValue();
	}

	public JList<ChallengeModel> getList() {
		return challengeList;
	}

	public void removeIndex(int index) {
		challengeList.remove(index);
	}

	public int getIndex() {
		return challengeList.getSelectedIndex();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {

	}
}