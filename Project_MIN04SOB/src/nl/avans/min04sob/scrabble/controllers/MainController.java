package nl.avans.min04sob.scrabble.controllers;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import nl.avans.min04sob.scrabble.core.mvc.CoreController;
import nl.avans.min04sob.scrabble.core.mvc.CoreWindow;
import nl.avans.min04sob.scrabble.misc.InvalidMoveException;
import nl.avans.min04sob.scrabble.misc.PlaySound;
import nl.avans.min04sob.scrabble.misc.ScrabbleTableCellRenderer;
import nl.avans.min04sob.scrabble.models.AccountModel;
import nl.avans.min04sob.scrabble.models.BoardModel;
import nl.avans.min04sob.scrabble.models.ChatModel;
import nl.avans.min04sob.scrabble.models.CompetitionModel;
import nl.avans.min04sob.scrabble.models.GameModel;
import nl.avans.min04sob.scrabble.models.Tile;
import nl.avans.min04sob.scrabble.views.BoardPanel;
import nl.avans.min04sob.scrabble.views.ChatPanel;
import nl.avans.min04sob.scrabble.views.MenuView;
import nl.avans.min04sob.scrabble.views.SelectSwapView;
import nl.avans.min04sob.scrabble.views.StartPanel;

public class MainController extends CoreController {

	private CoreWindow frame;
	private MenuView menu;
	private AccountModel account;

	private AccountController accountcontroller;
	private BoardPanel currGamePanel;
	private ChatPanel chatPanel;
	private ChatModel chatModel;
	private BoardModel boardModel;
	private CompetitionModel competitionModel;
	private StartPanel startPanel;

	private GameModel currentGame;

	private CoreWindow swapWindow;
	private SelectSwapView swapView;

	private PlaySound ps;
	
	public MainController() {

		initialize();
		addListeners();
		
		addView(menu);
		addView(chatPanel);
		addView(frame);
		addModel(boardModel);
		addModel(competitionModel);
		addModel(account);

		frame.setJMenuBar(menu);
		frame.setPreferredSize(new Dimension(1000, 680));
		frame.pack();
		frame.setLocationRelativeTo(null);

		startUp();
	}

	private void addButtonListeners() {
		currGamePanel.addRefreshActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});

		currGamePanel.addResignActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ResignController(currentGame);
			}
		});

		currGamePanel.addNextActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentGame.getCurrentobserveturn() < currentGame
						.getNumberOfTotalTurns() + 1) {
					currGamePanel.enableNextButton();
					currentGame.setCurrentobserveturn(currentGame
							.getCurrentobserveturn() + 1);
					currGamePanel.enablePreviousButton();
					currGamePanel.update();

					currentGame.updateboardfromdatabasetoturn(currentGame
							.getCurrentobserveturn());

					currentGame.getBoardModel().update();

					updatelabels(currentGame.getCurrentobserveturn());
				} else {
					currGamePanel.disableNextButton();
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
					currGamePanel.enableNextButton();
					for (int x = 0; currentGame.getCurrentobserveturn() > x
							|| currentGame.getCurrentobserveturn() == x; x++) {
						currentGame.updateboardfromdatabasetoturn(x);

					}
					updatelabels(currentGame.getCurrentobserveturn());
				} else {
					currGamePanel.disablePreviousButton();
				}

			}

		});
		// swappen
		currGamePanel.addSwapActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Tile[] letters = currentGame.getPlayerTiles();
				selectSwap(letters);
			}
		});

		currGamePanel.addPlayActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					BoardModel newBoard = new BoardModel();
					for (int vertical = 0; vertical < 15; vertical++) {
						for (int horizontal = 0; horizontal < 15; horizontal++) {
							newBoard.setValueAt(
									currGamePanel.getNewBoard()[vertical][horizontal],
									vertical, horizontal);
						}

					}
					currentGame.playWord(newBoard);
					currentGame.setPlayerTiles();
					currGamePanel.infoBox("Woord gelegd", "Woord gelegd");
					openGame(currentGame);
				} catch (InvalidMoveException e) {
					currGamePanel.infoBox(e.getMessage(), "Ongeldige zet");
				}
			}
		});
	}

	@Override
	public void addListeners() {

		menu.viewChallengeItemActionListener(new ActionListener() { // uitdagingen
																	// bekijken

			@Override
			public void actionPerformed(ActionEvent e) {
				// crtl.challengers();
				new ChallengeController(account);
				// new ChallengeController(account.getUsername());

			}
		});
		menu.adddoChallengeItemActionListener(new ActionListener() { // uitdagen

			@Override
			public void actionPerformed(ActionEvent e) {
				new CompetitionController(account, competitionModel)
						.openChallengeView();
				// crtl.toChallenge();

			}
		});
		menu.addChangePassItemActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// changePass();
				AccountController accountController = new AccountController(
						account);
				accountController.setChangePassPanel();
			}
		});
		menu.Accountaanmaken(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AccountController accountController = new AccountController(
						account);
				accountController.loginToRegister();
			}
		});
		menu.seeCompetitionsItem(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CompetitionController(account, competitionModel)
						.openCompetitionScores();
			}
		});

		menu.joinCompetitionItem(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				new CompetitionController(account, competitionModel)
						.openJoinCompetitionView();

				// invController = new InviteController();
				// invController.setButtonsJoin();
			}
		});
		menu.viewPlayers(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				accountcontroller = new AccountController(account);
				accountcontroller.adminChangePass();

			}
		});

		menu.viewWords(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AcceptDeclineController();
			}
		});

		menu.deleteFromCompetitionItem(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CompetitionController(account, competitionModel)
						.openDeleteFromCompetitionView();
			}
		});

		menu.deleteCompetitionItem(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				new CompetitionController(account, competitionModel)
						.openDeleteCompetitionView();

			}
		});

		menu.createCompetitionItem(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new CompetitionController(account, competitionModel)
						.openCreateCompetitionView();
			}
		});

		addLoginListener();

		menu.addLogoutItemActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				account.logout();
				closePanels();
				frame.getContentPane().add(startPanel,
						"cell 0 0 10 6,alignx left,aligny top");
				frame.revalidate();
				frame.repaint();
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
				currGamePanel.playerView();
				chatPanel.playerView();
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
				currGamePanel.observerView();
				chatPanel.observerView();
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

	private void refresh() {
		boardModel.removeMutatable();
		openGame(currentGame);
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

	private void startUp() {
		accountcontroller = new AccountController(account);
		accountcontroller.addView(menu);
		accountcontroller.addView(chatPanel);
		frame.setIconImage(menu.getImageForIcon());
		frame.getContentPane().add(startPanel,
				"cell 0 0 10 6,alignx left,aligny top");
		frame.revalidate();
		frame.repaint();
	}

	@Override
	public void initialize() {
		frame = new CoreWindow("Wordfeud", JFrame.EXIT_ON_CLOSE);
		// changePassPanel = new ChangePassPanel();
		menu = new MenuView();

		startPanel = new StartPanel();

		// competitioncontroller = new CompetitionController();
		account = new AccountModel();

		currGamePanel = new BoardPanel();
		competitionModel = new CompetitionModel();
		boardModel = new BoardModel();
		currGamePanel.setRenderer(new ScrabbleTableCellRenderer(boardModel));
		chatPanel = new ChatPanel();

	}

	public void closePanels() {
		frame.remove(startPanel);
		frame.remove(currGamePanel);
		frame.remove(chatPanel);
		frame.repaint();
	}

	protected void openGame(GameModel selectedGame) {
		addModel(selectedGame);
		removeModel(chatModel);

		setCurrentGame(selectedGame);
		chatModel = new ChatModel(selectedGame, account);
		addModel(chatModel);
		removeModel(boardModel);

		closePanels();

		currGamePanel = selectedGame.getBoardPanel();
		//boardModel = selectedGame.getBoardModel();
		boardModel = selectedGame.getBoardFromDatabase();
		addModel(boardModel);
		addView(currGamePanel);
		currentGame.setBoardModel(boardModel);
		currGamePanel.setRenderer(new ScrabbleTableCellRenderer(boardModel));
		currGamePanel.setModel(boardModel);

		updatelabels(selectedGame.getCurrentobserveturn());
		
		//selectedGame.setplayertilesfromdatabase(selectedGame.getNumberOfTotalTurns());
		selectedGame.setPlayerTiles();
		

		//selectedGame.getBoardFromDatabase();
		selectedGame.update();
		if (!(selectedGame.hasButtons())) {

			addButtonListeners();
			selectedGame.setButtons(true);
		}
		
		if(selectedGame.yourturn()){
			currGamePanel.playerView();
		} else {
			currGamePanel.observerView();	
		}
		openPanels();

		initChat();
	}
	
	private void initChat() {
		chatPanel.getChatFieldSend().setEnabled(true);
		chatPanel.empty();
		ArrayList<String> messages = chatModel.getMessages();
		for (String message : messages) {
			chatPanel.addToChatField(message);
		}
		chatModel.update();
	}

	public void openPanels() {
		frame.getContentPane().add(currGamePanel,
				"cell 4 0 6 6,growx,aligny top");

		frame.getContentPane().add(chatPanel,
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
			ps = new PlaySound();
			ps.playSound("send.wav", false);
		}
	}

	private void setCurrentGame(GameModel selectedGame) {
		currentGame = selectedGame;
	}
	
	private void setTurnLabel() {
		if (currentGame.isObserver()) {
			currGamePanel.setLabelPlayerTurn(" van "
					+ currentGame.getChallenger().getUsername());
		} else {
			if (currentGame.yourturn()) {
				currGamePanel.setLabelPlayerTurn("aan jouw ("
						+ account.getUsername() + ")");
			} else {
				currGamePanel.setLabelPlayerTurn(currentGame.getOpponent()
						.getUsername());
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

	// selectSwap
	public void selectSwap(Tile[] letters) {
		swapWindow = new CoreWindow();
		swapView = new SelectSwapView(letters);
		swapWindow.getContentPane().add(swapView);
		swapWindow.setResizable(false);
		swapWindow.setTitle("letters wisselen");
		swapWindow.pack();

		swapView.addButtonListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				List<Tile> selectedTiles = swapView.getSelectedTiles();
				currentGame.swap((ArrayList<Tile>) selectedTiles);
				swapWindow.dispose();
			}
		});
	}

}
