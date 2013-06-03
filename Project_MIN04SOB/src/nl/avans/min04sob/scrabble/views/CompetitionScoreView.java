package nl.avans.min04sob.scrabble.views;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import nl.avans.min04sob.scrabble.core.CorePanel;
import nl.avans.min04sob.scrabble.models.AccountModel;
import nl.avans.min04sob.scrabble.models.CompetitionModel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class CompetitionScoreView extends CorePanel {

	private JScrollPane scrollPane;
	private JScrollPane scrollPane1;
	private JList<CompetitionModel> competitionList;
	private JButton backButton;
	private JLabel competitionLabel;
	private JLabel playersLabel;
	private String[] columnNames;
	private Object[][] data;
	private JTable table;
	private DefaultTableModel tableModel;

	public CompetitionScoreView() {
		setLayout(new MigLayout("", "[200px:220px:220px,grow][800px:800px:800px]", "[][100px:100px:100px,grow][][100px:150px:100px,grow][100px:100px:25px]"));

		columnNames = new String[] { "account_naam",
				"aantal gespeelde webstrijden", "totaal aantal punten",
				"gemiddeld aantal punten per wedstrijd",
				"aantal webstrijden gewonnen/verloren", "bayesian-average" };

		competitionLabel = new JLabel("Competities");
		add(competitionLabel, "cell 0 0,alignx left");

		playersLabel = new JLabel("Scores van spelers in de competitie");
		add(playersLabel, "cell 1 0,alignx left");

		scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 1 1 3,grow");
		
		scrollPane1 = new JScrollPane();
		add(scrollPane1, "cell 1 1 1 3,grow");

		competitionList = new JList<CompetitionModel>();
		scrollPane.setViewportView(competitionList);

		tableModel = new DefaultTableModel();
		table = new JTable(tableModel);
		scrollPane1.setViewportView(table);
		setColumns();

		backButton = new JButton("Terug");
		add(backButton, "cell 0 4,alignx left,aligny top");
		
	}

	public void setColumns() {
		tableModel.addColumn("account_naam");
		tableModel.addColumn("aantal gespeelde wedstrijd");
		tableModel.addColumn("totaal aantal punten");
		tableModel.addColumn("gemiddeld aantal punten per wedstrijd");
		tableModel.addColumn("aantal webstrijden gewonnen/verloren");
		tableModel.addColumn("bayesian-average");		
	}
	
	public void addRow(Object[] dataRow){
		tableModel.addRow(dataRow);
	}

	public void addActionListenerAnnuleerButton(ActionListener listener) {
		backButton.addActionListener(listener);
	}

	public void fillAvailableCompetitions(
			CompetitionModel[] availableCompetitions) {
		competitionList.setListData(availableCompetitions);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

	}

	public CompetitionModel selectedCompetition() {
		return competitionList.getSelectedValue();
	}
	
	public void addBackListener(ActionListener listener){
		backButton.addActionListener(listener);
	}
	
	public void addCompetitionListListener(MouseAdapter listener){
		competitionList.addMouseListener(listener);
	}
	
	public void fillCompetitions(CompetitionModel[] comp) {
		competitionList.setListData(comp);
	}
	
	
	public CompetitionModel getSelectedCompetition(){
		return competitionList.getSelectedValue();
	}
}