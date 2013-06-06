package legwoord;

import java.util.Arrays;

import org.apache.commons.lang3.reflect.TypeUtilsTest.This;

import nl.avans.min04sob.scrabble.models.AccountModel;
import nl.avans.min04sob.scrabble.models.BoardModel;

public class WoordLeggenTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AccountModel acc =  new AccountModel();
		acc.login("water", "water".toCharArray());
		BoardModel bm = new BoardModel();
		bm.setBoardToDefault();
		LegWoordMethodes lwm = new LegWoordMethodes(651, acc, bm, null, false);
		
		//System.out.println(lwm.firstTurn());
		//System.out.println(lwm.getNextTurnId());
		//System.out.println(lwm.thisPlayersTurn());
		System.out.println(lwm.getNextTurnUsername());
		
		//om arrayvergelijken te testen
		/*
		Object[][] oldBoardData = {
				{new String("1"), new String("2"), new String("3"), null, null, null, null, null, null, null, null, null, null, null, null},
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
		Object[][] newBoardData = {
				{new String("1"), new String("2"), new String("3"), new String("4"), null, null, null, null, null, null, null, null, null, null, null},
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
		
		
		
		Object[][] playedTiles = lwm.compareFields(oldBoardData, newBoardData);
		
		System.out.println( "old : " + Arrays.deepToString(oldBoardData));
		System.out.println( "old : " + Arrays.deepToString(newBoardData));
		System.out.println( "old : " + Arrays.deepToString(playedTiles));
		*/
	}

}
