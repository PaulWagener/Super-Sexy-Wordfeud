
package nl.avans.min04sob.scrabble.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import nl.avans.min04sob.scrabble.core.CoreController;
import nl.avans.min04sob.scrabble.core.CoreWindow;
import nl.avans.min04sob.scrabble.core.ScrabbleTableCellRenderer;
import nl.avans.min04sob.scrabble.models.AccountModel;
import nl.avans.min04sob.scrabble.models.BoardModel;
import nl.avans.min04sob.scrabble.models.ChatModel;
import nl.avans.min04sob.scrabble.models.GameModel;
import nl.avans.min04sob.scrabble.models.StashModel;
import nl.avans.min04sob.scrabble.models.Tile;
import nl.avans.min04sob.scrabble.views.BoardPanel;
import nl.avans.min04sob.scrabble.views.ChatPanel;
import nl.avans.min04sob.scrabble.views.MenuView;

public class MainController extends CoreController {

	private CoreWindow frame;
	private MenuView menu;
	private AccountModel account;
	private ChallengeController crtl;
	private AccountController accountcontroller;
	private BoardPanel currGamePanel;
	private ChatPanel chatPanel;
	private ChatModel chatModel;
	private BoardModel boardModel;
	private GameModel currentGame;
	private InviteController invController;

	private Boolean observer;
	private CompetitionController competitioncontroller;
	private ResignController resigncontroller;

	public MainController() {

		initialize();
		addListeners();
		addView(menu);
		addView(chatPanel);
		addView(frame);
		addModel(boardModel);
		addModel(account);

		// Add the old messages first.
		// for (String message : chatModel.getMessages()) {
		// chatPanel.addToChatField(message);
		// }

		frame.setJMenuBar(menu);

		frame.pack();
	}

	private void addButtonListeners() {
		currGamePanel.addResignActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resigncontroller = new ResignController(currentGame);
			}
		});

		currGamePanel.addNextActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentGame.getCurrentobserveturn() < currentGame
						.getNumberOfTotalTurns() + 1) {
					currentGame.setCurrentobserveturn(currentGame
							.getCurrentobserveturn() + 1);

					currGamePanel.update();

					currentGame.updateboardfromdatabasetoturn(currentGame
							.getCurrentobserveturn());

					currentGame.getBoardModel().update();

					updatelabels(currentGame.getCurrentobserveturn());
				}
			}
		});
		currGamePanel.addPreviousActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (currentGame.getCurrentobserveturn() > 0) {
					currentGame.setCurrentobserveturn(currentGame
							.getCurrentobserveturn() - 1);
					currentGame.getBoardModel().setBoardToDefault();
					currGamePanel.update();
					for (int x = 0; currentGame.getCurrentobserveturn() > x
							|| currentGame.getCurrentobserveturn() == x; x++) {
						currentGame.updateboardfromdatabasetoturn(x);

					}
					updatelabels(currentGame.getCurrentobserveturn());

				}
			}

		});
	}

	@Override
	public void addListeners() {

		menu.viewChallengeItemActionListener(new ActionListener() {	//uitdagingen bekijken

			@Override
			public void actionPerformed(ActionEvent e) {
				//crtl.challengers();
				new ChallengeController2(account);
				//new ChallengeController(account.getUsername());


			}
		});
		menu.adddoChallengeItemActionListener(new ActionListener() { //uitdagen

			@Override
			public void actionPerformed(ActionEvent e) {
				new CompetitionController(account).openCompetitionView();
				//crtl.toChallenge();

			}
		});
		menu.addChangePassItemActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//changePass();
				AccountController accountController = new AccountController(account);
				accountController.setChangePassPanel();
			}
		});

		menu.seeCompetitionsItem(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CompetitionController(account).openCompetitionScores();
			}
		});

		menu.joinCompetitionItem(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				new CompetitionController(account).openJoinCompetitionView();

				//invController = new InviteController();
				//invController.setButtonsJoin();
			}
		});

		menu.viewWords(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AcceptDeclineController();
			}
		});
		
		menu.deleteFromCompetitionItem(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				new CompetitionController(account).openDeleteFromCompetitionView();
			}
		});

		menu.deleteCompetitionItem(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				new CompetitionController(account).openDeleteCompetitionView();
				
				//invController = new InviteController();
				//invController.setButtonsRemove();

			}
		});
		
		menu.createCompetitionItem(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new CompetitionController(account).openCreateCompetitionView();
			}
		});

		addLoginListener();

		menu.addLogoutItemActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				account.logout();
				closePanels();
				// addLoginListener();
			}
		});

		menu.addRegisterListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AccountController login = new AccountController(account);
				login.loginToRegister();
			}
		});

		menu.addOpenGamesListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem source = (JMenuItem) e.getSource();
				GameModel clickedGame = (GameModel) source
						.getClientProperty("game");
				openGame(clickedGame);
			}
		});

		menu.addViewGamesListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem source = (JMenuItem) e.getSource();
				GameModel clickedGame = (GameModel) source
						.getClientProperty("game");

				// TODO open game as observer
				openGame(clickedGame);
			}
		});

		chatPanel.addListenerChatField(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					sendChat();
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}

		});
		chatPanel.addListenerChatSendButton(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				sendChat();
			}
		});

	}

	private void addLoginListener() {
		menu.addLoginItemActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				accountcontroller = new AccountController(account);
				accountcontroller.addView(menu);
				accountcontroller.addView(chatPanel);
			}
		});
	}

	public void closePanels(){
		frame.remove(currGamePanel);
		frame.remove(chatPanel);
		frame.repaint();
	}

	@Override
	public void initialize() {
		observer = false;
		frame = new CoreWindow("Wordfeud", JFrame.EXIT_ON_CLOSE);
		// changePassPanel = new ChangePassPanel();
		menu = new MenuView();

		// competitioncontroller = new CompetitionController();
		account = new AccountModel();

		currGamePanel = new BoardPanel();

		boardModel = new BoardModel();
		currGamePanel.setRenderer(new ScrabbleTableCellRenderer(boardModel));
		currGamePanel.setModel(boardModel);

		boardModel.setValueAt(new Tile("B", 15, false), 8, 8);
		chatPanel = new ChatPanel();
		chatModel = null;

	}

	protected void openGame(GameModel selectedGame) {
		
		removeModel(chatModel);
		setCurrentGame(selectedGame);
		chatModel = new ChatModel(selectedGame, account);
		addModel(chatModel);
		removeModel(boardModel);
		//chatPanel.setEnabled(true);
		chatPanel.getChatFieldSend().setEnabled(true);
		//frame.remove(currGamePanel);
		closePanels();
		

		ArrayList<GameModel> games;
		StashModel stash = new StashModel();
		currGamePanel.setPlayerTiles(stash.getPlayerTiles(account, selectedGame));
		
		Tile[] letters = stash.getPlayerTiles(account, selectedGame);
		Tile[] newletters = new Tile[6];
		
		

			
			for(int counter = 0;newletters.length > counter; counter++){
				
				if(stash.letterleft()){
					String newletter = stash.getRandomLetter();
					if(!(letters.length > counter)){
							newletters[counter] = new Tile(newletter,selectedGame.getvalueforLetter(newletter),Tile.MUTATABLE );
					}else{
						newletters[counter] = letters[counter];
					}
				}
		}
		currGamePanel.setPlayerTiles(newletters);

		games = account.getObserverAbleGames();

		System.out.println("test");
		boolean yourTurn = selectedGame.yourturn();

		currGamePanel = new BoardPanel();
		boardModel = selectedGame.getBoardModel();
		currGamePanel.setRenderer(new ScrabbleTableCellRenderer(boardModel));
		currGamePanel.setModel(boardModel);
		AccountModel accountTurn;

		updatelabels(selectedGame.getCurrentobserveturn());
		
		// currGamePanel.setLabelScore(selectedGame.getCurrentValueForThisTurn());
		addModel(boardModel);
		selectedGame.setPlayerLetterFromDatabase();
		selectedGame.getBoardFromDatabase();
		selectedGame.update();

		addButtonListeners();

		//frame.getContentPane().add(currGamePanel, "cell 4 0 6 7,grow");
		//frame.revalidate();
		//frame.repaint();
		//chatPanel.setEnabled(true);
		openPanels();
		
		chatPanel.empty();
		ArrayList<String> messages = chatModel.getMessages();
		for (String message : messages) {
			chatPanel.addToChatField(message);
		}
		chatModel.update();
	}

	public void openPanels(){
		frame.add(currGamePanel,
				"cell 4 0 6 6,growx,aligny top");

		frame.add(chatPanel,
				"cell 0 0 4 6,alignx left,aligny top");
		frame.revalidate();
		frame.repaint();
	}

	public void sendChat() {
		String message = chatPanel.getChatFieldSendText();

		if (!message.equals("") && !message.equals(" ")) {

			chatModel.send(message);
			chatModel.update();

			// Empty the chat message box
			chatPanel.setChatFieldSendText("");
		}
	}
	
	private void setCurrentGame(GameModel selectedGame) {
		currentGame = selectedGame;
	}
	
	public void setTurnLabel() {
		if (currentGame.isObserver()) {
			if (currentGame.whosturn()) {
				currGamePanel.setLabelPlayerTurn(" van "
						+ currentGame.getChallenger().getUsername());
			} else {
				currGamePanel.setLabelPlayerTurn(" van "
						+ currentGame.getOpponent().getUsername());
			}
		}
	}

	private void updatelabels(int toTurn) {
		if (currentGame.isIamchallenger()) {
			currGamePanel.setLabelsNamesScores(currentGame.getChallenger()
					.getUsername(), currentGame.score(toTurn).split(",")[0],
					currentGame.getOpponent().getUsername(),
					currentGame.score(toTurn).split(",")[1]);

		} else {
			currGamePanel.setLabelsNamesScores(currentGame.getOpponent()
					.getUsername(), currentGame.score(toTurn).split(",")[0],
					currentGame.getChallenger().getUsername(), currentGame
							.score(toTurn).split(",")[1]);

		}
		setTurnLabel();
	}
}

