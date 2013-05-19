package nl.avans.min04sob.scrabble.views;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;
import nl.avans.min04sob.scrabble.core.CorePanel;
import nl.avans.min04sob.scrabble.core.TileTranfserHandler;
import nl.avans.min04sob.scrabble.models.BoardModel;
import nl.avans.min04sob.scrabble.models.Tile;

public class BoardPanelView extends CorePanel {
	JTable table;
	JTable playerTilesField;
	public BoardPanelView() {

		table = new JTable();
		JScrollPane scrollPane = new JScrollPane(table);
	
		Character[] blaat = new Character[] { ' ', ' ', ' ', ' ', ' ',' ',' ' };
		
		// Create a new table instance
		
		table.setBorder(new LineBorder(new Color(0, 0, 0)));
		table.setPreferredSize(new Dimension(0, 0));
		table.setPreferredScrollableViewportSize(new Dimension(0, 0));
		table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		
		table.setRowHeight(30);
		
		table.setEnabled(true);		
		table.validate();
		table.setDragEnabled(true);
		table.setDropMode(DropMode.USE_SELECTION);
		TileTranfserHandler handler = new TileTranfserHandler();
		table.setTransferHandler(handler);
		/*for (Character character : columnNames) {
			table.getColumn(character).setCellRenderer(renderer);
		}*/
		setLayout(new MigLayout("", "[-15.00px][][47.00px][60px][5px][73px][100px:400px][55.00px][292px][:430px:430px]", "[475px:475px][35px][:30px:30px][25px]"));

		// Add the table to a scrolling pane
		//JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, "cell 0 0 9 1,grow");
		
		/*
		ChatController chat = new ChatController(150, "player");
		chat.getchatpanel().setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		add(chat.getchatpanel(), "cell 9 0 1 3,grow");
		*/
		
		Tile[][] playerTiles = new Tile[][] {{ new Tile("a"), new Tile("b"), new Tile("c"), new Tile("d") }};
		Character[] columns = new Character[] { ' ', ' ', ' ', ' ' };
		playerTilesField = new JTable(playerTiles, columns);
		playerTilesField.setBorder(new LineBorder(new Color(0, 0, 0)));
		playerTilesField.setRowHeight(30);
		playerTilesField.setCellSelectionEnabled(true);
		
		playerTilesField.setDragEnabled(true);
		playerTilesField.setDropMode(DropMode.USE_SELECTION);
		playerTilesField.setTransferHandler(handler);
		
		add(playerTilesField, "cell 0 2 9 1,growx,aligny top");
				
						JButton play = new JButton();
						play.setText("Play");
						add(play, "cell 1 3,alignx left,aligny center");
				
				JButton pass = new JButton();
				pass.setText("Pas");
				add(pass, "cell 3 3,grow");
				
				JButton swap = new JButton();
				swap.setText("Swap");
				add(swap, "cell 5 3,grow");
				
				JButton resign = new JButton();
				resign.setText("Resign");
				
			
				add(resign, "cell 8 3,alignx center,growy");


	}
	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
	public void updatetable(String[][] newDataValues){
		for(int y = 0;newDataValues.length > y; y++){
			for(int x = 0;newDataValues[y].length > x;x++){
				table.setValueAt(newDataValues[y][x], y, x);
				
				
			}
		}		
	}
	public String[][] gettabledata(){
		String[][] gettabledata = new String[table.getRowCount()+1][table.getColumnCount()+1];
		for(int y = 0;table.getRowCount() > y; y++){
			for(int x = 0;table.getColumnCount() > x;x++){
				gettabledata[y][x] = table.getValueAt(y, x).toString();
			
			}
		}
		return gettabledata;
		
	}
	public void updatePlayerTiles(String[][] newPlayerDataValues){
		for(int y = 0; newPlayerDataValues[0].length > y+1;y++){
			playerTilesField.setValueAt(newPlayerDataValues[0][y], 0, y);
		}
		
	}
	
	public void setRenderer(TableCellRenderer renderer){
		table.setDefaultRenderer(Tile.class, renderer);
	}
	public void setModel(BoardModel bpm) {
		table.setModel(bpm);
	}
	
	
}