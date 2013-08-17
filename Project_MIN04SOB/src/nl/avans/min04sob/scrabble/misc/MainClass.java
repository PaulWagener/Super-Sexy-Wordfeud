package nl.avans.min04sob.scrabble.misc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import nl.avans.min04sob.scrabble.controllers.MainController;

public class MainClass implements Runnable {

	
	public static final ExecutorService executor = Executors.newFixedThreadPool(10);

	public static void main(String[] args) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
		}
		
		executor.submit(new MainController());
	}

	@Override
	public void run() {
		new MainController();
		executor.shutdown();
	}
}
