package wordfeud.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import wordfeud.core.mvc.CoreController;
import wordfeud.models.GameModel;
import wordfeud.views.ResignPanel;

public class ResignController extends CoreController {

	private ResignPanel resignPanel;
	private JFrame frame;
	private GameModel gameModel;
	private String labelName;

	public ResignController(GameModel game) {
		initialize();
		addListeners();

		gameModel = game;

		frame.setAlwaysOnTop(true);
		frame.add(resignPanel);

		addView(resignPanel);
		addModel(gameModel);

		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}

	@Override
	public void addListeners() {
		resignPanel.addResignActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doResign();
			}
		});

		resignPanel.addNoResignActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeFrame();
			}
		});
	}

	private void closeFrame() {
		frame.dispose();
		frame = null;
	}

	private void doResign() {
		gameModel.resign();
		closeFrame();
	}

	public String getLabelName() {
		return labelName;
	}

	@Override
	public void initialize() {
		frame = new JFrame();
		setLabelName();
		resignPanel = new ResignPanel();
		resignPanel.setResignLabelName(getLabelName());
	}

	public void setLabelName() {
		labelName = "Weet je zeker dat je de huidige game wilt opgeven?";
	}

}
