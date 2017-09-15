import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

@SuppressWarnings("serial")
public class ChatFrame extends JFrame implements ActionListener, KeyListener {
	
	private JTextArea output;
	private JTextField input;
	private JTextField field;
	public JTextArea users;
	private JTextField privatfield;
	
	private JButton login;
	private JButton logout;
	
	public String name = System.getProperty("user.name");
	public Boolean loggedin = false;
	public Boolean javno = true;
	
	MessageRobot NewMessages;
	UsersRobot Users;

	public ChatFrame() {
		super();
		this.setTitle("ChitChat");
		NewMessages = new MessageRobot(this);
		Users = new UsersRobot(this);
		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());
		pane.setBackground(new Color(250, 235, 215));

		//Okence za vpis svojega vzdevka (avtomatsko nastavljeno na uporabniško ime)
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel nickname = new JLabel("Vzdevek:");
		nickname.setFont(new Font("Myanmar MN", Font.PLAIN, 13));
		field = new JTextField(name, 10);
		field.setEnabled(true);
		field.addKeyListener(this);
		panel.add(nickname);
		panel.add(field);
		GridBagConstraints panelConstraint = new GridBagConstraints();
		panelConstraint.fill = GridBagConstraints.HORIZONTAL;		
		panelConstraint.gridx = 0;
		panelConstraint.gridy = 0;
		panel.setBackground(new Color(250, 235, 215));
		pane.add(panel, panelConstraint);
	
		//Gumba za prijavo in odjavo
		login = new JButton("Prijava");
		panel.add(login);
		login.addActionListener(this);
		login.setForeground(new Color(0, 100, 0));
		login.setOpaque(true);
		login.setBackground(new Color(250, 235, 215));
		login.setFont(new Font("Myanmar MN", Font.PLAIN, 13));
		
		logout = new JButton("Odjava");
		panel.add(logout);
		logout.addActionListener(this);
		logout.setEnabled(false);
		logout.setForeground(new Color(220, 20, 60));
		logout.setOpaque(true);
		logout.setBackground(new Color(250, 235, 215));
		logout.setFont(new Font("Myanmar MN", Font.PLAIN, 13));
		
		//Okence v katerem se izpisuje pogovor
		this.output = new JTextArea();
		JScrollPane scrollpane = new JScrollPane(output);
		scrollpane.setPreferredSize(new Dimension(200,100));
		this.output.setEditable(false);
		GridBagConstraints outputConstraint = new GridBagConstraints();
		outputConstraint.fill = GridBagConstraints.BOTH;
		outputConstraint.weightx = 1;
		outputConstraint.weighty = 1;		
		outputConstraint.gridx = 0;
		outputConstraint.gridy = 1;
		pane.add(scrollpane, outputConstraint);
		output.setFont(new Font("Myanmar MN", Font.PLAIN, 13));
		
		//Okence za pisanje sporočil
		GridBagConstraints msgConstraint = new GridBagConstraints();
		msgConstraint.fill = GridBagConstraints.BOTH;
		msgConstraint.gridx = 0;
		msgConstraint.gridy = 2;
		JLabel msg = new JLabel (" Vpišite svoje sporočilo:");
		msg.setFont(new Font ("Myanmar MN", Font.PLAIN, 13));
		pane.add(msg, msgConstraint);
		this.input = new JTextField(30);
		GridBagConstraints inputConstraint = new GridBagConstraints();
		inputConstraint.fill = GridBagConstraints.BOTH;
		inputConstraint.weightx = 1;
		inputConstraint.weighty = 0;
		inputConstraint.gridx = 0;
		inputConstraint.gridy = 3;
		pane.add(input, inputConstraint);
		input.addKeyListener(this);
		input.setEditable(false);
		
		//Okence za izpis prijavljenih
		this.users = new JTextArea(10,25);
		JScrollPane scrollbar = new JScrollPane(users);
		scrollbar.setPreferredSize(new Dimension(350,400));
		this.users.setEditable(false);
		this.users.setLayout(new BoxLayout(users, BoxLayout.Y_AXIS)); 
		GridBagConstraints uporabnikiConstraint = new GridBagConstraints();
		uporabnikiConstraint.gridx = 1;
		uporabnikiConstraint.gridy = 1;
		uporabnikiConstraint.weightx = 1.0;
		uporabnikiConstraint.weighty = 1.0;
		uporabnikiConstraint.gridwidth = 2;
		uporabnikiConstraint.fill = GridBagConstraints.BOTH;
		pane.add(scrollbar, uporabnikiConstraint);
		users.setFont(new Font("Myanmar MN", Font.PLAIN, 13));
		
		//Napis nad prijavljenimi uporabniki
		GridBagConstraints userConstraint = new GridBagConstraints();
		userConstraint.fill = GridBagConstraints.BOTH;
		userConstraint.gridx = 1;
		userConstraint.gridy = 0;
		JLabel users = new JLabel("Prijavljeni uporabniki");
		users.setFont(new Font("Myanmar MN", Font.PLAIN, 13));
		pane.add(users, userConstraint);
		
		//Napis za izbiro zasebnega klepeta
		GridBagConstraints privatConstraint = new GridBagConstraints();
		privatConstraint.fill = GridBagConstraints.BOTH;
		privatConstraint.gridx = 1;
		privatConstraint.gridy = 2;
		privatConstraint.gridwidth =2;
		JLabel label = new JLabel("Za zasebni klepet vpiši ime, sicer pusti prazno.");
		label.setFont(new Font("Myanmar MN", Font.PLAIN, 13));
		pane.add(label, privatConstraint);
		
		//Gump za zasebni klepet
		GridBagConstraints privatNameConstraint = new GridBagConstraints();
		privatNameConstraint.fill = GridBagConstraints.BOTH;
		privatNameConstraint.gridx = 1;
		privatNameConstraint.gridy = 3;
		JLabel privat = new JLabel("Ime uporabnika: ");
		privat.setFont(new Font("Myanmar MN", Font.PLAIN, 13));
		pane.add(privat, privatNameConstraint);
		
		//Polje za vpis uporabnika, s katerim želimo zasebni klepet
		privatfield = new JTextField(5);
		privatfield.setEnabled(false);
		GridBagConstraints privat1Constraint = new GridBagConstraints();
		privat1Constraint.fill = GridBagConstraints.BOTH;
		privat1Constraint.gridx = 2;
		privat1Constraint.gridy = 3;
		pane.add(privatfield, privat1Constraint);
		//privatfield.addKeyListener(this);
	    
		// Avtomatska odjava ko zapremo okno
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				if (loggedin = true) {
					try {Http.logout(name);
				    } catch (IOException e1) {
					        e1.printStackTrace();
				    } catch (URISyntaxException e1) {
				    		e1.printStackTrace();
				    }
				}
			}
		});
		
	}
	
	public void addNewMessage(Message sporocilo) {
		this.output.append((sporocilo.getSender() + ": " + sporocilo.getText()) + "\n");
	}
	
	// pisanje sporočil
	/**
	 * @param person - the person sending the message
	 * @param message - the message content
	 * @throws BadLocationException 
	 */
	
	public void addMessage(String person, String message) throws BadLocationException {
		String chat = this.output.getText();
		this.output.setText(chat + person + ": " + message + "\n");
		this.input.setText("");
	}
	
	public String getName() {
		return this.field.getText();
	}
	
	public boolean getStatus() {
		return loggedin;
	}
	
	//prijava in odjava
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == login){
			try {Http.login(getName());
			    this.logout.setEnabled(true);
			    this.login.setEnabled(false);
	  	   		field.setEnabled(false);
	  	   		privatfield.setEnabled(true);
	  	   		input.setEditable(true);
	  	   		loggedin = true;
	  	   		NewMessages.activate();
		   }  catch (IOException e1) {
			   // TODO Auto-generated catch block
			   e1.printStackTrace();
		   } catch (URISyntaxException e1) {
			   // TODO Auto-generated catch block
			   e1.printStackTrace();
		   }
		}
		else if(e.getSource() == logout){
			try {Http.logout(getName());
				this.logout.setEnabled(false);
				this.login.setEnabled(true);
				privatfield.setEnabled(false);
	    	  	field.setEnabled(true);
	    	  	input.setEditable(false);
	    	  	loggedin = false; 
	    	  	input.setText("");
	    	  	output.setText("");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			  }
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getSource() == this.input) {
			if (e.getKeyChar() == '\n') {
				try{if (privatfield.getText().equals("")) {
						Http.sendMessage(getName(), true, null, this.input.getText());
						this.addMessage(getName(),this.input.getText());
			        } else {
			        	Http.sendMessage(getName(), false, this.privatfield.getText(), this.input.getText() + " (privatno sporocilo)");
			        	this.addMessage(getName(),this.input.getText() + " (privatno sporocilo)");
			        }
				
				} catch (URISyntaxException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}	
	}
	
	//Sprejemanje novih sporočil
		public void noNewMessages() {
			if (this.output.getText().equals("")) {
				this.output.append("Ni novih sporocil." + "\n");
			} else {
			}
		}	
		
		public void addNewMessage(String msg) {
			if (this.output.getText().equals("Ni novih sporocil." + "\n")) {
				this.output.setText("");
			}
			this.output.append(msg);
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
		}
		
		//Seznam prijavljenih uporabnikov
		
		public void deleteAll() {
			this.users.setText(null);
		}

		public void noActiveUsers() {
			this.users.append("Nihče ni prijavljen." + "\n");
		}
					
		public void addUser(User oseba) {
			this.users.append(oseba.getUsername() + " (" + oseba.getLastActive() + ")" + "\n");
		}	
	
}