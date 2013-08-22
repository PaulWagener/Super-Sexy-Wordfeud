package wordfeud.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import wordfeud.core.mvc.CoreController;
import wordfeud.models.ModeratorModel;
import wordfeud.views.AcceptDeclineView;

public class AcceptDeclineController extends CoreController {

	private AcceptDeclineView adView;
	private ModeratorModel modModel;

	public AcceptDeclineController() {
		modModel = new ModeratorModel();
		this.addModel(modModel);
		adView = new AcceptDeclineView();

		this.addView(adView);
		fillWordList();
		addListeners();
	}

	public void acceptWord() {
		String word = adView.getSelectedWord();
		modModel.acceptWord(word);
		fillWordList();
		checkWordList();
	}
	
	private void checkWordList() {
		adView.checkWordList();
	}

	@Override
	public void addListeners() {
		adView.addAcceptActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				acceptWord();
			}
		});

		adView.addDeniedActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deniedWord();
			}
		});

		adView.addBackActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				adView.removeWindow();
			}
		});

		adView.addListSelectionListener(new ListSelectionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void valueChanged(ListSelectionEvent e) {
				JList<String> list = (JList<String>) e.getSource();
				if (list.getSelectedValue() == null) {
					adView.setButtonsEnabled(false);
				} else {
					adView.setButtonsEnabled(true);
				}
			}
		});
	}

	public void deniedWord() {
		String word = adView.getSelectedWord();
		modModel.denyWord(word);
		fillWordList();
		checkWordList();
	}

	public void fillWordList() {
		String[] words = modModel.getRequestedWordList();
		adView.fillWordList(words);
		adView.revalidate();
		adView.repaint();

	}

	@Override
	public void initialize() {
	}

}
