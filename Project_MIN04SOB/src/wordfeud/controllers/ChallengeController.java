package wordfeud.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import wordfeud.core.mvc.CoreController;
import wordfeud.models.AccountModel;
import wordfeud.views.ChallengeView;


public class ChallengeController extends CoreController {

	private ChallengeView challengeView;
	private JFrame frame;
	private AccountModel account;

	public ChallengeController(AccountModel user) {
		account = user;
		initialize();
		addListeners();

		frame.setAlwaysOnTop(true);
		frame.add(challengeView);

		addView(challengeView);
		this.fillChallengeList();
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setTitle("Uitdagingen Beheren");
	}

	@Override
	public void addListeners() {
		challengeView.addActionListenerAccept(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				acceptChallenge();
				challengeView.clearChallengeList();
				challengeView.fillChallengeList(account.getChallenges());
			}
		});
		challengeView.addActionListenerDecline(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				declineChallenge();
				challengeView.clearChallengeList();
				challengeView.fillChallengeList(account.getChallenges());
			}
		});

		challengeView.addActionListenerBack(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goBack();
			}
		});

	}

	private void fillChallengeList() {
		challengeView.fillChallengeList(account.getChallenges());
		frame.repaint();
	}

	private void acceptChallenge() {
		challengeView.getSelectedChallenge().accept();
	}

	private void declineChallenge() {
		challengeView.getSelectedChallenge().decline();
	}

	private void goBack() {
		frame.dispose();
		frame = null;
	}

	@Override
	public void initialize() {
		frame = new JFrame();
		challengeView = new ChallengeView();
	}

}
