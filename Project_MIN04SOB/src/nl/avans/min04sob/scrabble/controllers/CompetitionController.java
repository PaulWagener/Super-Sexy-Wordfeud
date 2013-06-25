package nl.avans.min04sob.scrabble.controllers;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import nl.avans.min04sob.scrabble.core.mvc.CoreController;
import nl.avans.min04sob.scrabble.core.mvc.CoreWindow;
import nl.avans.min04sob.scrabble.misc.DuplicateCompetitionException;
import nl.avans.min04sob.scrabble.models.AccountModel;
import nl.avans.min04sob.scrabble.models.ChallengeModel;
import nl.avans.min04sob.scrabble.models.CompetitionModel;
import nl.avans.min04sob.scrabble.views.CompetitionScoreView;
import nl.avans.min04sob.scrabble.views.CompetitionView;
import nl.avans.min04sob.scrabble.views.CreateCompetitionView;

public class CompetitionController extends CoreController {

	private CompetitionModel competitionModel;
	private CompetitionView competitionView;
	private CoreWindow window;
	private CoreWindow window1;
	private CoreWindow window2;
	private AccountModel accountModel;
	private ChallengeModel challengeModel;
	private CompetitionScoreView competitionScoreView;
	private int index; // competitie id meegeven
	private CreateCompetitionView createCompetitionView;

	public CompetitionController(AccountModel user, CompetitionModel comp) {

		accountModel = user;
		competitionModel = comp;
		competitionView = new CompetitionView();
		challengeModel = new ChallengeModel(accountModel);
		competitionScoreView = new CompetitionScoreView();
		createCompetitionView = new CreateCompetitionView();

		addView(competitionView);
		addModel(competitionModel);
		addView(competitionScoreView);
		addView(createCompetitionView);

	}

	@Override
	public void addListeners() {
		// TODO Auto-generated method stub
	}

	public void getAllCompetitions() {
		competitionView.fillCompetitions(competitionModel.getAllCompetitions());
	}

	public void getAllCompetitionsScore() {
		competitionScoreView.fillCompetitions(competitionModel
				.getAllCompetitions());
	}

	public void getAvailable(String username) {
		competitionView.fillCompetitions(accountModel
				.getAvailableCompetitions(username));
	}

	public void getCompetitions(String username) {
		competitionView
				.fillCompetitions(accountModel.getCompetitions());
	}

	public int getCompID() { // competition ID meegeven
		return index;
	}

	public void getParticipants(int competition_id) {
		competitionView.fillPlayerList(competitionModel
				.getUsersFromCompetition(competition_id, accountModel.getUsername()));
	}
	
	public void getChallengeAbleUsers(int competition_id) {
		competitionView.fillPlayerList(challengeModel.getChallengeAblePlayers(competition_id, accountModel.getUsername()));
	}
	


	@Override
	public void initialize() {
		// TODO Auto-generated method stub
	}

	public void openCompetitionScores() {
		window1 = new CoreWindow();
		window1.add(competitionScoreView);
		window1.setResizable(false);
		window1.setTitle("Competitie Scores");
		window1.setPreferredSize(new Dimension(1000,300));
		window1.pack();
		window1.setLocationRelativeTo(null);
		getAllCompetitionsScore();
		competitionScoreView.addBackListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				window1.dispose();
				window1 = null;
			}
		});

		competitionScoreView.addCompetitionListListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					CompetitionModel selectedComp = competitionScoreView.getSelectedCompetition();
					competitionScoreView.emptyTable();
					for (Object[] row : selectedComp.getRanking(selectedComp.getCompId())) {
						competitionScoreView.addRow(row);
					}
				}
			}
		});

		

	}

	public void openChallengeView() {
		window = new CoreWindow();
		window.add(competitionView);
		window.setTitle("Speler uitdagen");
		window.setMinimumSize(new Dimension(550,450));
		window.setResizable(true);
		window.pack();
		window.setLocationRelativeTo(null);
		competitionView.addBackListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				window.dispose();
				window = null;
			}
		});
		// uitdagen
		competitionView.addActionButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int id = competitionView.getSelectedCompetition().getCompId();
				challengeModel.check(accountModel.getUsername(),
						competitionView.getSelectedPlayer().getUsername(), id);
				competitionView.clearPlayerList();
				getChallengeAbleUsers(id);
			}
		});

		competitionView.addCompetitionListListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					int id = competitionView.getSelectedCompetition()
							.getCompId();
					getChallengeAbleUsers(id);
				}
			}
		});

		competitionView.setText("Ingeschreven competities",
				"Spelers in competitie", "Speler uitdagen", true);

		getCompetitions(accountModel.toString());
	}

	// wordt niet gebruikt
	public void openDeleteCompetitionView() {
		window = new CoreWindow();
		window.add(competitionView);
		window.setTitle("Competitie verwijderen");

		window.setMinimumSize(new Dimension(550,450));
		window.setResizable(true);
		window.pack();
		window.setLocationRelativeTo(null);
		competitionView.addBackListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				window.dispose();
				window = null;
			}
		});

		competitionView.addCompetitionListListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					int id = competitionView.getSelectedCompetition()
							.getCompId();
					getParticipants(id);
				}
			}
		});

		competitionView.setText("Competities", "Spelers in competitie",
				"Verwijder Competitie", true);
		competitionView.disableList();
		getAllCompetitions();
	}

	// wordt niet gebruikt
	public void openDeleteFromCompetitionView() {
		window = new CoreWindow();
		window.add(competitionView);
		window.setTitle("Verwijderen uit competitie");

		window.setMinimumSize(new Dimension(550,450));
		window.pack();
		window.setLocationRelativeTo(null);
		competitionView.addBackListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				window.dispose();
				window = null;
			}
		});

		competitionView.addCompetitionListListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					int id = competitionView.getSelectedCompetition()
							.getCompId();
					getParticipants(id);
				}
			}
		});

		competitionView.setText("Ingeschreven Competities",
				"Spelers in competitie", "Verwijderen uit Competitie", true);
		competitionView.disableList();
		getAvailable(accountModel.toString());
	}

	public void openJoinCompetitionView() {
		window = new CoreWindow();
		window.add(competitionView);
		window.setTitle("Competitie deelnemen");

		window.setMinimumSize(new Dimension(550,450));
		window.setResizable(true);
		window.pack();
		window.setLocationRelativeTo(null);
		competitionView.addBackListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				window.dispose();
				window = null;
			}
		});
		// joinCompetition
		competitionView.addActionButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (competitionView.getSelectedCompetition() != null) {
					int id = competitionView.getSelectedCompetition()
							.getCompId();
					getParticipants(id);
					competitionModel.join(id, accountModel.getUsername());
					competitionView.clearCompList();
					competitionView.clearPlayerList();
					getAvailable(accountModel.getUsername());
					competitionView.clearPlayerList();
				} else {
					System.out.println("selecteer een competitie");
				}
			}

		});

		competitionView.addCompetitionListListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					int id = competitionView.getSelectedCompetition()
							.getCompId();
					getParticipants(id);
				}
			}
		});

		competitionView.setText("Beschikbare competities",
				"Spelers in competitie", "Competitie deelnemen", true);
		competitionView.disableList();
		getAvailable(accountModel.toString());

	}

	public void openCreateCompetitionView(){
		//createCompetitionView= new CreateCompetitionView();
		window2 = new CoreWindow();
		window2.add(createCompetitionView);
		window2.setTitle("Competitie aanmaken");

		window2.setPreferredSize(new Dimension(250, 130));
		window2.setResizable(false);
		window2.pack();
		window2.setLocationRelativeTo(null);
		createCompetitionView.addBackButtonListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				window2.dispose();

			}
		});

		createCompetitionView.addCreateButtonListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String desc = createCompetitionView.getDiscription();
				try {
					competitionModel.createCompetition(accountModel, desc);
					window2.dispose();
				} catch (DuplicateCompetitionException dupE) {
					System.out.println("Deze moet dus nog worden afgehandeld");
					dupE.printStackTrace();
				}
			}
		});

	}

}
