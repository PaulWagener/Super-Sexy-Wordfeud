package wordfeud.controllers;

import wordfeud.core.mvc.CoreController;
import wordfeud.misc.ScrabbleTableCellRenderer;
import wordfeud.models.BoardModel;
import wordfeud.views.BoardPanel;

public class BoardController extends CoreController {
	private BoardPanel bpv;
	private BoardModel bpm;

	public BoardController(boolean observer) {
		bpm = new BoardModel();

		bpv = new BoardPanel();

		// bpm.setTile(14, 14, new TileModel("het werkt"));
		this.updateBoard();

		this.updatePlayerTiles();
		bpv.setModel(bpm);

		this.addView(bpv);

		initialize();

	}

	@Override
	public void addListeners() {
	}

	public BoardModel getBpm() {
		return bpm;
	}

	public BoardPanel getBpv() {
		return bpv;
	}

	@Override
	public void initialize() {
		ScrabbleTableCellRenderer renderer = new ScrabbleTableCellRenderer(bpm);
		bpv.setRenderer(renderer);
	}

	public void setBpm(BoardModel bpm) {
		this.bpm = bpm;
	}

	public void setBpv(BoardPanel bpv) {
		this.bpv = bpv;
	}

	public void updateBoard() {

		// bpv.updatetable(bpm.getDataValues());
	}

	public void updatePlayerTiles() {
		// bpv.updatePlayerTiles(bpm.getPlayerDataValues());
	}

}
