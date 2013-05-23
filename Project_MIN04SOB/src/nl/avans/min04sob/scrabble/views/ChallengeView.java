package nl.avans.min04sob.scrabble.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nl.avans.min04sob.scrabble.core.CoreController;
import nl.avans.min04sob.scrabble.core.CorePanel;
import nl.avans.min04sob.scrabble.core.CoreView;
	public class ChallengeView   implements Observer{
		// extends??
		private JTextField Opponent;
	//	http://www.oracle.com/technetwork/articles/javase/index-142890.html
		private JPanel chpanel = new JPanel();
		private JPanel tochallenge = new JPanel();
		private JPanel response=new JPanel();
		private JButton accept = new JButton("accept");
		private JButton decline = new JButton("decline");
		private JButton oke = new JButton("oke");
		private JButton oke2 =new JButton("oke");
		private JFrame jf = new JFrame("popup");
		private JTextField usernameField = new JTextField(10);
			
		public ChallengeView()
		{
			jf.setTitle("Challenge");
			jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jf.pack();
		}
		
		public void showChallenge()//gui 
		{
			jf.setResizable(false);
			jf.setVisible(true);
			jf.setContentPane(chpanel);
			chpanel.setPreferredSize(new Dimension(180,70));
		 
			chpanel.add(new JLabel("You have received a challenge"));
			chpanel.add(accept);
			chpanel.add(decline);

			accept.setFont(new Font("Serif", Font.ITALIC, 14));
			decline.setFont(new Font("Serif", Font.ITALIC, 14));
		}
		
		public void toChallenge()//gui
		{	
			jf.setVisible(true);
			jf .setContentPane(tochallenge);
			tochallenge.setPreferredSize(new Dimension(180,70));
			tochallenge.add(usernameField);
			jf.add(oke);
		}
		
		public void response(String msg)//gui
		{
			jf.setResizable(false);
			jf.setVisible(true);
			jf.setContentPane(response);
			response.setPreferredSize(new Dimension(130,70));
			response.add(new JLabel("msg"));
			response.add(oke2);
			oke2.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					jf.dispose();
				}});
			
		}
		public String getUsername() 
		{
			return usernameField.getText();
		}
		public void addActionListenerAccept(ActionListener listener) {
			accept.addActionListener(listener);
		}
		
		
		public void addActionListenerOke(ActionListener listener) {
			oke.addActionListener(listener);
		}
		 
		public void addActionListenerDecline(ActionListener listener) {
			decline.addActionListener(listener);
		}

		@Override
		public void update(Observable arg0, Object arg1) {
			 int x = (int) arg1;
			 if(x==1)
			 {
				 showChallenge();
			 }
			 if(x==2)
			 {
				 response("challenge denied");
			 }
			 if(x==3)
			 {
				 response("challenge accepted");
			 }
		}
		public  JFrame javaFrame()		{
			return jf;
		}
	}
	///