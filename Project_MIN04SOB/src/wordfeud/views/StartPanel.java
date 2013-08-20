package wordfeud.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;

import javax.swing.ImageIcon;

import wordfeud.core.mvc.CorePanel;

@SuppressWarnings("serial")
public class StartPanel extends CorePanel {

	private ImageIcon startIcon;

	public StartPanel() {
		this.setPreferredSize(new Dimension(1000, 680));
		this.setBackground(new Color(33, 36, 46));
		this.setVisible(true);
		startIcon = new ImageIcon(this.getClass().getResource(
				"/images/startup.png"));
		revalidate();
		repaint();

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(startIcon.getImage(), 148, 128, null);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

}
