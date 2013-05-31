package nl.avans.min04sob.scrabble.controllers;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import nl.avans.min04sob.scrabble.core.CoreController;
import nl.avans.min04sob.scrabble.core.CoreWindow;
import nl.avans.min04sob.scrabble.models.AccountModel;
import nl.avans.min04sob.scrabble.models.ChallengeModel;
import nl.avans.min04sob.scrabble.models.CompetitionModel;
import nl.avans.min04sob.scrabble.views.CompetitionView;

public class CompetitionController extends CoreController {

	private CompetitionModel competitionModel;
	private CompetitionView competitionView;
	private CoreWindow window;
	private AccountModel accountModel;
	private ChallengeModel challengeModel;

	public CompetitionController(AccountModel user)
	{

		accountModel = user;
		competitionModel = new CompetitionModel();
		competitionView = new CompetitionView();
		challengeModel = new ChallengeModel(accountModel);
		
		addView(competitionView);
		addModel(competitionModel);
		
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
	}

	@Override
	public void addListeners() {
		// TODO Auto-generated method stub
	}
	
	public void openCompetitionView(){
		window = new CoreWindow();
		window.add(competitionView);
		
		window.setPreferredSize(new Dimension(400,320));
		window.setResizable(false);
		window.pack();
		
		competitionView.addBackListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				window.dispose();
			}	
		});
		//uitdagen
		competitionView.addActionButtonListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				try {
					challengeModel.controle(competitionView.getSelectedPlayer());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		
		competitionView.addCompetitionListListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount()==1){
					int id = competitionView.getSelectedCompetition().getCompId();
					getParticipants(id);
				}
			}
		});
		
		competitionView.setText("Ingeschreven competities", "Spelers in competitie", "Speler uitdagen", true);
		
		getCompetitions(accountModel.toString());
	}
	
	public void openJoinCompetitionView() {
		window = new CoreWindow();
		window.add(competitionView);
		
		window.setPreferredSize(new Dimension(400,320));
		window.setResizable(false);
		window.pack();
		
		competitionView.addBackListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				window.dispose();
			}	
		});
		//joinCompetition
		competitionView.addActionButtonListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(competitionView.getSelectedCompetition() != null){
					int id = competitionView.getSelectedCompetition().getCompId();
					getParticipants(id);
					//competitionView.removeIndex(competitionView.getIndex());
				}
				else{
					System.out.println("selecteer een competitie");
				}
			}
			
		}); 
		
		competitionView.addCompetitionListListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount()==1){
					int id = competitionView.getSelectedCompetition().getCompId();
					getParticipants(id);
				}
			}
		});
		
		competitionView.setText("Beschikbare competities", "Spelers in competitie", "Competitie deelnemen", true);
		
		getAvailable(accountModel.toString());
		
	}
	
	public void openCompetitionScores(){
		window = new CoreWindow();
		window.add(competitionView);
		
		window.setPreferredSize(new Dimension(400,320));
		window.setResizable(false);
		window.pack();
		
		competitionView.addBackListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				window.dispose();
			}	
		});
		
		competitionView.addCompetitionListListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount()==1){
					int id = competitionView.getSelectedCompetition().getCompId();
					getParticipants(id);
				}
			}
		});
		
		competitionView.setText("Competities", "Spelers in competitie", "",false);
		
		getAllCompetitions();
		
	}
	//wordt niet gebruikt
	public void openDeleteCompetitionView() {
		window = new CoreWindow();
		window.add(competitionView);
		
		window.setPreferredSize(new Dimension(400,320));
		window.setResizable(false);
		window.pack();
		
		competitionView.addBackListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				window.dispose();
			}	
		});
		
		competitionView.setText("Competities", "Spelers in competitie", "Verwijder Competitie",true);
		
		getAllCompetitions();	
	}
	//wordt niet gebruikt
	public void openDeleteFromCompetitionView() {
		window = new CoreWindow();
		window.add(competitionView);
		
		window.setPreferredSize(new Dimension(400,320));
		window.pack();
		
		competitionView.addBackListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				window.dispose();
			}	
		});
		
		competitionView.setText("Ingeschreven Competities", "Spelers in competitie", "Verwijderen uit Competitie",true);
		
		getAvailable(accountModel.toString());	
	}
	
	public void getCompetitions(String username){
		competitionView.fillCompetitions(accountModel.getCompetitions(username));
	}
	
	public void getParticipants(int competition_id){
		competitionView.fillPlayerList(competitionModel.getUsersFromCompetition(competition_id));
	}
	
	public void getAvailable(String username){
		competitionView.fillCompetitions(accountModel.getAvailableCompetitions(username));
	}
	
	public void getAllCompetitions(){
		competitionView.fillCompetitions(competitionModel.getAllCompetitions());
	}

}
