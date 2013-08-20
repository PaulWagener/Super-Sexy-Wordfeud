package wordfeud.core.mvc;

import java.awt.Component;
import java.beans.PropertyChangeEvent;

import javax.swing.JFrame;

import wordfeud.core.Event;
import wordfeud.models.AccountModel;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class CoreWindow extends JFrame implements CoreView {

	public CoreWindow() {
		initialize();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public CoreWindow(int closeAction) {
		initialize();
		setDefaultCloseOperation(closeAction);
	}

	public CoreWindow(String title) {
		super(title);
		initialize();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public CoreWindow(String title, int closeAction) {
		super(title);
		initialize();
		setDefaultCloseOperation(closeAction);
	}

	public void add(Component component, String constraint) {
		getContentPane().add(component, constraint);
	}

	public void initialize() {
		setVisible(true);
		getContentPane().setLayout(
				new MigLayout("", "[::2000px,grow]", "[::2000px,grow]"));
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case Event.LOGIN:
			AccountModel loggedInAccount = (AccountModel) evt.getNewValue();
			setTitle("Wordfeud (ingelogd als " + loggedInAccount.getUsername()
					+ ")");
			break;
		case Event.LOGOUT:
			setTitle("Wordfeud - Uitgelogd");
			break;
		default:
			break;
		}
	}

	@Override
	public void remove(Component component) {
		getContentPane().remove(component);
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}
}