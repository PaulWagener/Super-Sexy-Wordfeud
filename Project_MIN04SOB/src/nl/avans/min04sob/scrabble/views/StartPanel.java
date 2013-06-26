package nl.avans.min04sob.scrabble.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;

import javax.swing.ImageIcon;

import nl.avans.min04sob.scrabble.core.mvc.CorePanel;

@SuppressWarnings("serial")
public class StartPanel extends CorePanel {

	private ImageIcon startIcon;

	public StartPanel() {
		this.setPreferredSize(new Dimension(1000, 680));
		this.setBackground(new Color(37, 41, 53));
		this.setVisible(true);
		startIcon = new ImageIcon(this.getClass().getResource(
				"/images/startup.png"));
		revalidate();
		repaint();

	}

	public void paintComponent(Graphics g) {
		g.setColor(new Color(37, 41, 53));
		g.drawRect(0, 0, this.getWidth(), this.getHeight());
		g.drawImage(startIcon.getImage(), 148, 128, null);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

	}

}
