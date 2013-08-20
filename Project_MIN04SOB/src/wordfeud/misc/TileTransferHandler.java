package wordfeud.misc;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.TableModel;

import wordfeud.models.Tile;


public class TileTransferHandler extends TransferHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4763693992565251566L;
	private static final DataFlavor tileFlavor = new DataFlavor(Tile.class,
			"Tile");

	public TileTransferHandler() {
	}

	@Override
	public boolean canImport(TransferSupport support) {
		Tile sourceTile;
		try {
			sourceTile = (Tile) support.getTransferable().getTransferData(
					tileFlavor);
			if (sourceTile != null && !sourceTile.isMutatable()) {
				return false;
			}
			// support.setShowDropLocation(true);
			JTable table = (JTable) support.getComponent();
			TableModel model = table.getModel();
			int row = table.getSelectedRow();
			int col = table.getSelectedColumn();
			Tile tile = (Tile) model.getValueAt(row, col);

			if (tile == null) {
				return true;
			}

			if (tile.equals(sourceTile)) {
				return true;
			}
		} catch (UnsupportedFlavorException | IOException e) {
			// e.printStackTrace();
		}

		return false;
		// return tile.isMutatable();
	}

	@Override
	protected Transferable createTransferable(JComponent source) {
		JTable sourceTable = (JTable) source;
		int row = sourceTable.getSelectedRow();
		int col = sourceTable.getSelectedColumn();

		Tile sourceTile = (Tile) sourceTable.getModel().getValueAt(row, col);
		return sourceTile;
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if (data != null) {
			Tile sourceTile;

			try {
				JTable table = (JTable) source;
				int sourceRow = table.getSelectedRow();
				int sourceCol = table.getSelectedColumn();
				TableModel model = table.getModel();

				sourceTile = (Tile) data.getTransferData(tileFlavor);
				
				
				if(sourceTile.isBlanc()){
						
						String letter = JOptionPane.showInputDialog(null, "Enter your letter ",
								"letter needed ", JOptionPane.WARNING_MESSAGE);
						if (letter.length() < 2) {
							sourceTile.setLetter(letter);
						}
				}
				
				
				if (sourceTile != null && sourceTile.isMutatable()) {
					model.setValueAt(null, sourceRow, sourceCol);
				} else {
					// Put the sourceTile Back
					model.setValueAt(sourceTile, sourceRow, sourceCol);
				}
				table.clearSelection();

			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getSourceActions(JComponent c) {
		return MOVE;
	}

	@Override
	public boolean importData(TransferSupport support) {
		if (!canImport(support)) {
			return false;
		}
		JTable table = (JTable) support.getComponent();
		Tile newTile;
		try {
			newTile = (Tile) support.getTransferable().getTransferData(
					tileFlavor);
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
			return false;
		}

		TableModel model = table.getModel();
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();

		model.setValueAt(newTile, row, col);

		return super.importData(support);
	}
}
