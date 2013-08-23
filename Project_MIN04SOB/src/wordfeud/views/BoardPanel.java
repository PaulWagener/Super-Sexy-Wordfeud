package wordfeud.views;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;
import wordfeud.core.Event;
import wordfeud.core.mvc.CorePanel;
import wordfeud.misc.TileTable;
import wordfeud.misc.TileTransferHandler;
import wordfeud.models.BoardModel;
import wordfeud.models.Tile;

public class BoardPanel extends CorePanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2899677237066103421L;
	private JButton resignButton;
	private JButton swapButton;
	private JButton passButton;
	private JButton playButton;
	private JButton nextButton;
	private JButton prevButton;
	private JButton refreshButton;

	private JTable playBoard;
	private JTable playerTilesField;

	private ActionListener resignActionListener;

	private final JLabel browseText;
	private final JLabel turnTextLabel;
	private final JLabel spelersLabel;

	private JLabel turnLabel;
	private JLabel opponentNameLabel;
	private JLabel opponentScoreLabel;
	private JLabel playerNameLabel;
	private JLabel playerScoreLabel;

	private DefaultTableModel playerStash;

	public BoardPanel() {
		playerStash = new DefaultTableModel(1, 8);
		setLayout(new MigLayout(
				"",
				"[75px:75px][75px:75px][100px:100px:100px][100px:100px:100px][100px:100px:100px][125px][100px][]",
				"[30px][30px][][:390px:390px][30px][30px][30px]"));
		/**
		 * 
		 * Main playing board
		 */

		playBoard = new TileTable();
		playBoard.setBorder(new LineBorder(new Color(0, 0, 0)));
		playBoard.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		playBoard.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		playBoard.getTableHeader().setReorderingAllowed(false);
		playBoard.getTableHeader().setResizingAllowed(false);
		playBoard.setRowHeight(30);
		playBoard.setEnabled(true);
		playBoard.validate();

		spelersLabel = new JLabel("Spelers");
		add(spelersLabel, "cell 5 0,alignx left");

		add(playBoard, "cell 0 0 5 4");

		playerNameLabel = new JLabel("<player>");
		add(playerNameLabel, "cell 5 1,alignx right");

		playerScoreLabel = new JLabel("<score>");
		add(playerScoreLabel, "cell 6 1");

		opponentNameLabel = new JLabel("<opponent>");
		add(opponentNameLabel, "cell 5 2,alignx right");

		opponentScoreLabel = new JLabel("<score>");
		add(opponentScoreLabel, "cell 6 2");

		playerTilesField = new TileTable();
		playerTilesField.setModel(playerStash);
		playerTilesField.setBorder(new LineBorder(new Color(0, 0, 0)));
		playerTilesField.setCursor(Cursor
				.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		playerTilesField.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		playerTilesField.getTableHeader().setReorderingAllowed(false);
		playerTilesField.getTableHeader().setResizingAllowed(false);
		playerTilesField.setRowHeight(30);
		playerTilesField.setEnabled(true);
		playerTilesField.setCellSelectionEnabled(true);
		playerTilesField.validate();
		playerTilesField.setDefaultRenderer(Tile.class,
				new DefaultTableCellRenderer());

		add(playerTilesField, "cell 0 4 5 1,growx,aligny top");

		playBoard.setDragEnabled(true);
		playBoard.setDropMode(DropMode.USE_SELECTION);
		playBoard.setTransferHandler(new TileTransferHandler());

		playerTilesField.setDragEnabled(true);
		playerTilesField.setDropMode(DropMode.USE_SELECTION);
		playerTilesField.setTransferHandler(new TileTransferHandler());

		playButton = new JButton();
		playButton.setEnabled(true);

		turnTextLabel = new JLabel("Beurt");
		add(turnTextLabel, "cell 5 4,alignx right");

		turnLabel = new JLabel("<player>");
		add(turnLabel, "cell 6 4");
		playButton.setText("Spelen");
		add(playButton, "flowx,cell 0 5 2 1,grow");

		swapButton = new JButton();
		swapButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		swapButton.setText("Wissel");
		add(swapButton, "cell 2 5,grow");

		passButton = new JButton();
		passButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		passButton.setText("Passen");
		add(passButton, "cell 3 5,grow");

		resignButton = new JButton();
		resignButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		resignButton.setText("Opgeven");
		add(resignButton, "cell 4 5,grow");

		nextButton = new JButton();
		nextButton.setFont(new Font("Dialog", Font.PLAIN, 12));

		prevButton = new JButton();
		prevButton.setFont(new Font("Dialog", Font.PLAIN, 12));

		refreshButton = new JButton("Refresh");
		add(refreshButton, "cell 1 6,grow");

		browseText = new JLabel("Bladeren");
		add(browseText, "cell 2 6,alignx right");
		prevButton.setText("Vorige");

		add(prevButton, "cell 3 6,grow");
		nextButton.setText("Volgende");

		add(nextButton, "cell 4 6,grow");
		observerView();

	}

	public void addPassActionListener(ActionListener listener) {
		passButton.addActionListener(listener);
	}

	// enable nextButton
	public void enableNextButton() {
		nextButton.setEnabled(true);
	}

	// disable nextButton
	public void disableNextButton() {
		nextButton.setEnabled(false);
	}

	public void atValue() {
		playerStash.setValueAt(new Tile("A", 12, Tile.MUTATABLE, 45), 0, 2);
	}

	public void addPlayActionListener(ActionListener listener) {
		playButton.addActionListener(listener);
	}

	public void addNextActionListener(ActionListener listener) {
		nextButton.addActionListener(listener);
	}

	public void addPreviousActionListener(ActionListener listener) {
		prevButton.addActionListener(listener);

	}

	public void addRefreshActionListener(ActionListener listener) {
		refreshButton.addActionListener(listener);
	}

	public void addResignActionListener(ActionListener listener) {
		resignActionListener = listener;
		resignButton.addActionListener(resignActionListener);
	}

	// swap button
	public void addSwapActionListener(ActionListener listener) {
		swapButton.addActionListener(listener);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case Event.MOVE:

			boolean hasTurn = (boolean) evt.getNewValue();
			if (hasTurn) {
				playerView();
			} else {
				observerView();
			}

			// refresh the board
			playBoard.updateUI();
			playBoard.revalidate();
			playBoard.repaint();
			setTurnLabel(hasTurn);
			break;

		case Event.OPPONENTSCORE:

			int opponentScore = (int) evt.getNewValue();
			setScoreOpponent(opponentScore);
			break;

		case Event.CHALLENGERSCORE:

			int challengerScore = (int) evt.getNewValue();
			setScoreChallenger(challengerScore);
			break;
			
		case Event.RESIGN:
			observerView();
			break;
		}

	}

	private void setTurnLabel(boolean hasTurn) {
		String name;
		if (hasTurn) {
			name = "Aan jouw (" + playerNameLabel.getText() + ")";
		} else {
			name = opponentNameLabel.getText();
		}

		turnLabel.setText(name);
	}

	public void setLabelPlayerTurn(String currTurnPlayerName) {
		this.turnLabel.setText(currTurnPlayerName);
	}

	public void setNameChallenger(String name) {
		this.playerNameLabel.setText(name);
	}

	public void setNameOpponent(String name) {
		this.opponentNameLabel.setText(name);
	}

	public void setScoreChallenger(int playerScore) {
		this.playerScoreLabel
				.setText(Integer.toString(playerScore) + " points");
	}

	public void setScoreOpponent(int OpponentScore) {
		this.opponentScoreLabel.setText(Integer.toString(OpponentScore)
				+ " points");
	}

	public void setModel(BoardModel bpm) {
		playBoard.setModel(bpm);
	}

	public void setOpponent(String name) {
		opponentNameLabel.setText(name);
	}

	public void setPlayer(String name) {
		playerNameLabel.setText(name);
	}

	public void setPlayerTiles(Tile[] playerTiles) {
		for (int y = 0; playerTiles.length > y; y++) {
			playerStash.setValueAt(playerTiles[y], 0, y);
		}
	}

	public void setRenderer(TableCellRenderer renderer) {
		playBoard.setDefaultRenderer(Tile.class, renderer);
	}

	public void observerView() {
		passButton.setEnabled(false);
		resignButton.setEnabled(false);
		swapButton.setEnabled(false);
		playButton.setEnabled(false);
		playerTilesField.setEnabled(false);
		playBoard.setEnabled(false);

	}

	public void playerView() {
		passButton.setEnabled(true);
		resignButton.setEnabled(true);
		swapButton.setEnabled(true);
		playButton.setEnabled(true);
		playerTilesField.setEnabled(true);
		playBoard.setEnabled(true);

	}

	public void infoBox(String infoMessage, String title) {
		JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + title,
				JOptionPane.INFORMATION_MESSAGE);

	}

	public void enablePreviousButton() {

		this.prevButton.setEnabled(true);
	}

	public void disablePreviousButton() {
		this.prevButton.setEnabled(false);
	}

	public Tile[][] getNewBoard() {
		Tile[][] newBoard = new Tile[15][15];
		for (int rowIndex = 0; rowIndex < 15; rowIndex++) {
			for (int columnIndex = 0; columnIndex < 15; columnIndex++) {
				newBoard[rowIndex][columnIndex] = (Tile) this.playBoard
						.getModel().getValueAt(rowIndex, columnIndex);
			}
		}
		return newBoard;
	}
}
