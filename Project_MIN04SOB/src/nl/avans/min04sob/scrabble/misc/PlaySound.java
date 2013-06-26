package nl.avans.min04sob.scrabble.misc;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class PlaySound {

	public void playSound(String filename, boolean shouldLoop) {
		try {
			URI bounceURI = null;
			try {
				URL bounceURL = this.getClass().getResource(
						"/sounds/" + filename);
				bounceURI = bounceURL.toURI();
			} catch (URISyntaxException ex) {
				ex.printStackTrace();
			}
			if (bounceURI != null) {
				File f = new File(bounceURI);
				AudioInputStream audioInputStream = AudioSystem
						.getAudioInputStream(f.getAbsoluteFile());
				Clip clip = AudioSystem.getClip();

				clip.open(audioInputStream);
				if (shouldLoop) {
					clip.loop(Clip.LOOP_CONTINUOUSLY);
				} else {
					clip.start();
				}
			}
		} catch (Exception ex) {
			System.out.println("Error with playing sound.");
			ex.printStackTrace();
		}
	}
	
}
