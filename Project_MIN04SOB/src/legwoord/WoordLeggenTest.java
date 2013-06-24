package legwoord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.reflect.TypeUtilsTest.This;

import nl.avans.min04sob.scrabble.core.db.Db;
import nl.avans.min04sob.scrabble.core.db.Query;
import nl.avans.min04sob.scrabble.misc.InvalidMoveException;
import nl.avans.min04sob.scrabble.models.AccountModel;
import nl.avans.min04sob.scrabble.models.BoardModel;
import nl.avans.min04sob.scrabble.models.Tile;

public class WoordLeggenTest {

	
	
	
	
	
	
	public static void main(String[] args) {
		AccountModel acc =  new AccountModel();
		acc.login("water", "water".toCharArray());
		BoardModel bmo = new BoardModel();
		BoardModel bmn = new BoardModel();
		bmo.setBoardToDefault();
		bmn.setBoardToDefault();
		LegWoordMethodes lwm = new LegWoordMethodes(656, acc, null, null, false);
		lwm.putCharactersInDatabase(678);
		//System.out.println(lwm.firstTurn());
		//System.out.println(lwm.getNextTurnId());
		//System.out.println(lwm.thisPlayersTurn());
		//System.out.println(lwm.getNextTurnUsername());
		
		/*
		Tile[][] oldBoardData1 = {
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
				
		};
		
		Tile[][] oldBoardData2 = {
				{null, null, null, null, null, null, null, new Tile("b", 5, false, 9), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("r", 5, false, 79), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("o", 5, false, 70), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("e", 5, false, 30), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("k", 5, false, 49), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("j", 5, false, 47), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("e", 5, false, 31), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("s", 5, false, 83), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
				
		};
		Tile[][] newBoardData1 = {
				{null, null, null, null, null, null, null, new Tile("b", 5, false, 9), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("r", 5, false, 79), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("o", 5, false, 70), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("e", 5, false, 30), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("k", 5, false, 49), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("j", 5, false, 47), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("e", 5, false, 31), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("s", 5, false, 83), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
				
		};
		Tile[][] newBoardData2 = {
				{null, null, null, null, null, null, null, new Tile("b", 5, false, 9), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("r", 5, false, 79), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("o", 5, false, 70), null, null, null, null, null, null, null},
				{null, null, null, null, null, new Tile("b", 5, false, 10), new Tile("i", 5, false, 43), new Tile("e", 5, false, 30), new Tile("r", 5, false, 80), null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("k", 5, false, 49), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("j", 5, false, 47), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("e", 5, false, 31), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new Tile("s", 5, false, 83), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
		};
		
		
		for(int vertical = 0; vertical < 15 ; vertical++){
			for(int horizontal = 0;horizontal < 15; horizontal++){
				bmo.setValueAt(oldBoardData2[vertical][horizontal], vertical, horizontal);	
			}
			
		}
		for(int vertical = 0; vertical < 15 ; vertical++){
			for(int horizontal = 0;horizontal < 15; horizontal++){
				bmn.setValueAt(newBoardData2[vertical][horizontal], vertical, horizontal);	
			}
			
		}
		lwm.setboard(bmo);
		try{
			lwm.playWord(bmn);
		}catch(InvalidMoveException e){
			e.printStackTrace();
		}
		
		*/
		
		/*try {
			lwm.checkValidMove(bmo, bmn);
		} catch (InvalidMoveException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		//om arrayvergelijken te testen
		*/	
		
		//Tile[][] playedTiles = lwm.compareFields(oldBoardData, newBoardData1);
		
		//System.out.println( "old : " + Arrays.deepToString(oldBoardData));
		//System.out.println( "old : " + Arrays.deepToString(newBoardData1));
		//System.out.println( "old : " + Arrays.deepToString(playedTiles));
		
	}
	
	
}
