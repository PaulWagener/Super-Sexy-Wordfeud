package legwoord;

import java.util.Arrays;

import org.apache.commons.lang3.reflect.TypeUtilsTest.This;

import nl.avans.min04sob.scrabble.misc.InvalidMoveException;
import nl.avans.min04sob.scrabble.models.AccountModel;
import nl.avans.min04sob.scrabble.models.BoardModel;

public class WoordLeggenTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AccountModel acc =  new AccountModel();
		acc.login("water", "water".toCharArray());
		BoardModel bmo = new BoardModel();
		BoardModel bmn = new BoardModel();
		bmo.setBoardToDefault();
		bmn.setBoardToDefault();
		LegWoordMethodes lwm = new LegWoordMethodes(651, acc, null, null, false);
		
		//System.out.println(lwm.firstTurn());
		//System.out.println(lwm.getNextTurnId());
		//System.out.println(lwm.thisPlayersTurn());
		//System.out.println(lwm.getNextTurnUsername());
		
		
		
		
		Object[][] oldBoardData = {
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
		Object[][] newBoardData1 = {
				{null, null, null, null, null, null, null, new String("h"), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new String("h"), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new String("h"), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new String("h"), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new String("h"), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new String("h"), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new String("h"), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new String("h"), null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
		};
		
		Object[][] newBoardData2 = {
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
				{null, null, null, null, null, null, null, new String("h"), null, null, null, null, null, null, null},
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
				bmo.setValueAt(oldBoardData[vertical][horizontal], vertical, horizontal);	
			}
			
		}
		for(int vertical = 0; vertical < 15 ; vertical++){
			for(int horizontal = 0;horizontal < 15; horizontal++){
				bmn.setValueAt(newBoardData2[vertical][horizontal], vertical, horizontal);	
			}
			
		}
		
		try {
			lwm.checkValidMove(bmo, bmn);
		} catch (InvalidMoveException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		//om arrayvergelijken te testen
				/*
		
		Object[][] playedTiles = lwm.compareFields(oldBoardData, newBoardData);
		
		System.out.println( "old : " + Arrays.deepToString(oldBoardData));
		System.out.println( "old : " + Arrays.deepToString(newBoardData));
		System.out.println( "old : " + Arrays.deepToString(playedTiles));
		*/
	}

}
