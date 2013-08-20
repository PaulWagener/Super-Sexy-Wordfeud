package wordfeud.misc;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import wordfeud.models.BoardModel;
import wordfeud.models.Tile;


public class TileTable extends JTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8289529085270487084L;
	private StringBuilder toolTip;

	@Override
	public String getToolTipText(MouseEvent event) {
		toolTip =  new StringBuilder();
		Point p = event.getPoint();
		// Locate the renderer under the event location
		int colIndex = columnAtPoint(p);
		int rowIndex = rowAtPoint(p);

		TableModel model = getModel();
		Tile tile = (Tile) model.getValueAt(rowIndex, colIndex);

		if(model instanceof BoardModel){
			int multiplier = ((BoardModel) model).getMultiplier(new Point(rowIndex, colIndex));
			switch (multiplier) {
			case BoardModel.DL:
				toolTip.append("(DL) ");
				break;
			case BoardModel.TL:
				toolTip.append("(TL) ");
				break;
			case BoardModel.DW:
				toolTip.append("(DW) ");
				break;
			case BoardModel.TW:
				toolTip.append("(TW) ");
				break;
			}
		}
		

		if (tile != null) {
			toolTip.append("Waarde:" + tile.getValue());
		}

		if(toolTip.length() == 0){
			return null;
		}
		
		return toolTip.toString();

	}
}
