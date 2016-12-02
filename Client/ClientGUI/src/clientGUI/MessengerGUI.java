package clientGUI;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JTextArea;

import utilities.ClientGUIListener;
import utilities.Message;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.util.Observable;
import java.util.Observer;

public class MessengerGUI extends JFrame implements Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton sendButton;
	private JTextArea textField;
	private JPanel messagePanel;
	private JTextArea messagePane;
	private final int defaultPanelWidth = 400;
	private final int defaultPanelHeight = 700;

	private JTextField usernameInput;
	private JTextField ipAddress;
	private JTextField portNumber;
	private JButton btnNewConnectButton;
	private JButton gameButton;
	private  JScrollBar scrollbar;
	private final int connectPanelWidth = 150;
	private final int connectPanelheight = 200;
	private BattleshipGUI battleshipGUI;
	private ClientGUIListener cl;
	private Thread th;
	private Message message;
	private Dimension scrSize;
	private String gameMessage;
	
	/**
	 * Default constructor
	 */
	public MessengerGUI()
	{
	}
	
	/**
	 * Initializes values for JFrame frame object. As well as creating and showing the connect panel.
	 * @param cl - ClientGUIListner to be used to connect, send, and receive messages for frame
	 */
	public void createMainFrame(ClientGUIListener cl)
	{
		this.cl = cl;
		setTitle("Client");
		setResizable(false);
		setLayout(new GridLayout(1,2));
		
		scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((scrSize.width/2 - connectPanelWidth/2), (scrSize.height/2 - connectPanelheight/2), connectPanelWidth, connectPanelheight);
		add(showConnectPanel());

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowListener());
	}

	private JPanel showConnectPanel()
	{
		JPanel connectPanel = new JPanel();
		connectPanel.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
		connectPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		
		JLabel lblNewLabel = new JLabel("Username: ");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		connectPanel.add(lblNewLabel);
		lblNewLabel.setLabelFor(usernameInput);
		
		usernameInput = new JTextField();
		connectPanel.add(usernameInput);
		usernameInput.setColumns(10);
			
		JLabel lblNewLabel_2 = new JLabel("IP address: ");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		connectPanel.add(lblNewLabel_2);
		lblNewLabel_2.setLabelFor(ipAddress);
		
		ipAddress = new JTextField();
		connectPanel.add(ipAddress);
		ipAddress.setColumns(10);
		
		JLabel portLabel = new JLabel("Port: ");
		portLabel.setHorizontalAlignment(SwingConstants.CENTER);
		connectPanel.add(portLabel);
		portLabel.setLabelFor(portNumber);
		
		portNumber = new JTextField();
		connectPanel.add(portNumber);
		portNumber.setColumns(10);
		
		btnNewConnectButton = new JButton("Connect");
		btnNewConnectButton.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		btnNewConnectButton.addActionListener(new ButtonListener());
		connectPanel.add(btnNewConnectButton);
		setVisible(true);
		return connectPanel;
	}
	
	private JPanel createMessengerPanel()
	{
		messagePanel = new JPanel();
		messagePanel.setBounds(0, 0, this.getWidth()/2, this.getHeight());
		messagePanel.setLayout(new BorderLayout(0, 0));

		gameButton = new JButton("Play Battleship");
		gameButton.addActionListener(new ButtonListener());
		gameButton.setEnabled(false);
		
		messagePanel.add(gameButton, BorderLayout.NORTH);

		messagePane = new JTextArea();
		JScrollPane jspMessagePane = new JScrollPane(messagePane);
		scrollbar = jspMessagePane.getVerticalScrollBar();
		messagePane.setEditable(false);

		messagePanel.add(jspMessagePane);
		
		JPanel panel_1 = new JPanel();
		messagePanel.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		messagePane.setLineWrap(true);
		
		textField = new JTextArea();
		JScrollPane jspMessageArea = new JScrollPane(textField);
		jspMessageArea.setPreferredSize(new Dimension(300, 80));
		textField.addKeyListener(new ButtonListener());
		textField.setLineWrap(true);
		
		panel_1.add(jspMessageArea);
		
		sendButton = new JButton("Send");
		sendButton.setPreferredSize(new Dimension(80, 80));
		panel_1.add(sendButton);
		sendButton.addActionListener(new ButtonListener());
		
		setVisible(true);
		return messagePanel;
	}
	
	private JPanel createGamePanel()
	{
		setBounds(getX(), getY(), getWidth()*2, getHeight());
		
		battleshipGUI = new BattleshipGUI(cl, usernameInput.getText());
		battleshipGUI.setBounds(0, 0, getWidth()/2, getHeight());
		cl.addObserver(battleshipGUI);
		return battleshipGUI.createGame();
	}
	
	/**
	 * Returns the MessengerGUI frame.
	 * @return JFrame that is the MessengerGUI
	 */
	public JFrame getFrame()
	{
		return MessengerGUI.this;
	}
	
	/**
	 * update method overridden from implemented Observer.
	 * State and/or data from ClientGUIListener is passed into this method.
	 */
	@Override
	public void update(Observable arg0, Object arg1) 
	{
		if (arg1 instanceof Message)
		{
			Message message = (Message) arg1;

			if (message.getCode() != null)
			{
				if (message.getCode().equals("Close"))
				{
					int reconnect = JOptionPane.showConfirmDialog(null, "Other person disconnected. Do you want to reconnect?", "Reconnect?", JOptionPane.YES_NO_OPTION);
					if (reconnect == 0)
					{
						if(cl.connect(ipAddress.getText(), Integer.parseInt(portNumber.getText()), usernameInput.getText()))
						{	
							getContentPane().removeAll();
							add(createMessengerPanel());
							setBounds((scrSize.width/2 - defaultPanelWidth/2), (scrSize.height/2 - defaultPanelHeight/2), defaultPanelWidth, defaultPanelHeight);
							messagePane.setText("Connecting, please wait...");
							
							disableButtons();
							setVisible(true);
							
							th = new Thread(cl);
							th.start();
							System.out.println("Reconnected");
						}
						else
						{
							JOptionPane.showMessageDialog(null, "Unable to connect");
						}
					}
					else
					{
						cl.close();
						System.exit(0);
					}
				}
				else if (message.getCode().equals("Request"))
				{
					gameButton.setEnabled(false);
					int request = JOptionPane.showConfirmDialog(null, "Would you like to play battleship?", "Request", JOptionPane.YES_NO_OPTION);
					if (request == 0)
					{
						message = new Message("Play");
						cl.sendMessage(message);
						messagePane.setText(messagePane.getText() + "\nWaiting for other Client.");
					}
					else
					{
						message = new Message("NoPlay");
						cl.sendMessage(message);
					}
				}
				else if (message.getCode().equals("NoPlay"))
				{
					gameButton.setEnabled(true);
					messagePane.setText(messagePane.getText() + "\nA client has declined.");
				}
				else if (message.getCode().equals("Start"))
				{
					messagePane.setText(messagePane.getText() + "\nGame starting.");
					add(createGamePanel());
					gameButton.setVisible(false);
				}
				else if (message.getCode().equals("Won"))
				{
					gameMessage = "You have won";
					resetBattleship();
				}
				else if (message.getCode().equals("Lost"))
				{
					gameMessage = "You have lost";
					resetBattleship();
				}
			}
			else
			{
				if (message.getHitVal() == null)
				{
					messagePane.setText(messagePane.getText() + message.toString());
					scrollbar.setValue(scrollbar.getMaximum());
				}
			}
		}
		
		//Enable textField, sendButton and gameButton if connected
		if (!messagePane.getText().equals("Connecting, please wait...") || !messagePane.getText().equals("Waiting for other Client."))
		{
			sendButton.setEnabled(true);
			textField.setEditable(true);
			gameButton.setEnabled(true);
		}
	}
	
	private void resetBattleship()
	{
		int playAgain = JOptionPane.showConfirmDialog(null, gameMessage + "\nWould you like to continue?", "Continue?", JOptionPane.YES_NO_OPTION);
		if (playAgain == 0)
		{
			getFrame().getContentPane().removeAll();
			getFrame().add(createMessengerPanel());
			getFrame().setBounds((scrSize.width/2 - defaultPanelWidth/2), (scrSize.height/2 - defaultPanelHeight/2), defaultPanelWidth, defaultPanelHeight);
			
			message = new Message("Play");
			cl.sendMessage(message);
			messagePane.setText("Waiting for other Client.");
			
			disableButtons();
		}
		else
		{
			cl.close();
			System.exit(0);
		}
	}
	
	private void disableButtons()
	{
		gameButton.setEnabled(false);
		textField.setEditable(false);
		sendButton.setEnabled(false);
	}
	
	/*****************************INNER CLASSES*****************************/

	public class ButtonListener implements ActionListener, KeyListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource() == sendButton)
			{
				actionConnect();
			}
			else if (e.getSource() == btnNewConnectButton)
			{
				if (usernameInput.getText().isEmpty() || ipAddress.getText().isEmpty() || portNumber.getText().isEmpty())
				{
					JOptionPane.showMessageDialog(null, "All information has not been filled");
				}
				else
				{
					try{
						if(cl.connect(ipAddress.getText(), Integer.parseInt(portNumber.getText()), usernameInput.getText()))
						{	
							getFrame().getContentPane().removeAll();
							getFrame().add(createMessengerPanel());
							getFrame().setBounds((scrSize.width/2 - defaultPanelWidth/2), (scrSize.height/2 - defaultPanelHeight/2), defaultPanelWidth, defaultPanelHeight);
							
							messagePane.setText("Connecting, please wait...");

							disableButtons();
							
							th = new Thread(cl);
							th.start();
						}
						else
						{
							JOptionPane.showMessageDialog(null, "Unable to connect");
						}
						
					} catch (NumberFormatException e1)
					{
						JOptionPane.showMessageDialog(null, "Port number has to be a number");
					}
				}
			}
			else if (e.getSource() == gameButton)
			{
				messagePane.setText(messagePane.getText() + "\nRequesting battleship game from both clients");
				message = new Message("Request");
				cl.sendMessage(message);
			}
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			// TODO Auto-generated method stub
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
			{
				arg0.consume();
				actionConnect();
			}
		}
		
		public void actionConnect()
		{
			if (!textField.getText().isEmpty())
			{
				message = new Message(usernameInput.getText(), textField.getText());
				if (!cl.sendMessage(message))
				{
					JOptionPane.showMessageDialog(null, "Closing, connection failed.");
					System.exit(0);
				}
				else
				{
					textField.setText("");	
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public class WindowListener extends WindowAdapter
	{
		@Override
		public void windowClosing(WindowEvent windowEvent) {
	        int confirm = JOptionPane.showConfirmDialog(getFrame(), "Are you sure you want to close?", "Closing", JOptionPane.YES_NO_OPTION);
	        if (confirm == 0){
	        	cl.close();
	        	System.exit(0);
	        }
	        else
	        {
	        }
	    }
	}
}
