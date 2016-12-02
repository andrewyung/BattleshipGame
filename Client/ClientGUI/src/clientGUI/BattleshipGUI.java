package clientGUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.SystemColor;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import utilities.ClientGUIListener;
import utilities.Message;

public class BattleshipGUI extends JPanel implements Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int fieldSize = 10;
	private JButton[][] buttonField;
	private JButton[][] oppbuttonField;
	private ClientGUIListener cl;
	private final int fieldButtonSize = 30;
	private JTextPane statusField;
	private ArrayList<String> unitPosition;
	private ArrayList<String> shipPositions;
	private String lastHit;
	
	private boolean tookTurn;
	private int setShipStatus;
	
	public BattleshipGUI(ClientGUIListener cl, String username)
	{
		this.cl = cl;
		setShipStatus = 0;
		unitPosition = new ArrayList<>();
		shipPositions = new ArrayList<>();
		buttonField = new JButton[fieldSize][fieldSize];
		oppbuttonField = new JButton[fieldSize][fieldSize];
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		add(createOpponentField());
		JLabel label = new JLabel("Opponent");
		label.setLayout(new FlowLayout());
		label.setPreferredSize(new Dimension(70, 300));
		JPanel legend = new JPanel();
		legend.setLayout(new GridLayout(2,1));
		JTextPane hit = new JTextPane();
		hit.setBackground(Color.RED);
		hit.setText("Hit");
		JTextPane miss = new JTextPane();
		miss.setBackground(Color.GREEN);
		miss.setText("Miss");
		legend.add(miss);
		legend.add(hit);
		label.add(legend);
		add(label);
		
		add(createStatusPanel());
		add(createField());

		label = new JLabel(username);
		add(label);
	}
	
	private JPanel createStatusPanel()
	{
		JPanel statusPanel = new JPanel();
		statusPanel.setPreferredSize(new Dimension(400, 50));
		statusField = new JTextPane();
		statusField.setBackground(SystemColor.LIGHT_GRAY);
		statusField.setEditable(false);
		statusPanel.add(statusField);
		return statusPanel;
	}
	
	private JPanel createField()
	{
		JPanel gameField = new JPanel();
		gameField.setLayout(new GridLayout(10,10));
		for (int i = 0; i < fieldSize; i++)
		{
			for (int j = 0; j < fieldSize; j++)
			{
				buttonField[i][j] = new JButton();
				buttonField[i][j].setPreferredSize(new Dimension(fieldButtonSize,fieldButtonSize));
				buttonField[i][j].setBackground(Color.BLUE);
				buttonField[i][j].addActionListener(new ButtonListener());
				buttonField[i][j].setName(i + "" + j);
				gameField.add(buttonField[i][j]);
			}
		}
		
		return gameField;
	}
	
	private JPanel createOpponentField()
	{
		JPanel oppgameField = new JPanel();
		oppgameField.setLayout(new GridLayout(10,10));
		for (int i = 0; i < fieldSize; i++)
		{
			for (int j = 0; j < fieldSize; j++)
			{
				oppbuttonField[i][j] = new JButton();
				oppbuttonField[i][j].setPreferredSize(new Dimension(fieldButtonSize,fieldButtonSize));
				oppbuttonField[i][j].setBackground(Color.BLUE);
				oppbuttonField[i][j].addActionListener(new ButtonListener());
				oppbuttonField[i][j].setName(i + "" + j);
				oppgameField.add(oppbuttonField[i][j]);
			}
		}
		
		return oppgameField;
	}
	
	/**
	 * Starts the game of allowing clients to select ship positions
	 * @return JPanel which allows game to let Client only select on their own field
	 */
	public JPanel createGame()
	{
		startShipPlacement();
		return this;
	}
	
	private void startShipPlacement()
	{
		for (int i = 0; i < fieldSize; i++)
		{
			for (int j = 0; j < fieldSize; j++)
			{
				oppbuttonField[i][j].setEnabled(false);
			}
		}
		statusField.setText("Place your aircraft carrier(5 squares). \nPick origin and then adjacent tiles on your field");
	}
	
	private void checkShipSize()
	{
		if (unitPosition.size() == 5 && setShipStatus == 0)
		{
			nextStatus();

			statusField.setText("Place your battleship(4 squares). \nPick origin and then adjacent tiles on your field");
		}
		else if (unitPosition.size() == 4 && setShipStatus == 1)
		{
			nextStatus();

			statusField.setText("Place your cruiser(3 squares). \nPick origin and then adjacent tiles on your field");
		}
		else if (unitPosition.size() == 3 && setShipStatus == 2)
		{
			nextStatus();
			
			statusField.setText("Place your submarine(3 squares). \nPick origin and then adjacent tiles on your field");
		}
		else if (unitPosition.size() == 3 && setShipStatus == 3)
		{
			nextStatus();

			statusField.setText("Place your destroyer(2 squares). \nPick origin and then adjacent tiles on your field");
		}
		else if (unitPosition.size() == 2 && setShipStatus == 4)
		{
			shipPositions.addAll(unitPosition);
			
			startGame();
		}
	}
	
	private void nextStatus()
	{
		shipPositions.addAll(unitPosition);
		unitPosition = new ArrayList<>();
		setShipStatus++;
	}
	
	private void startGame()
	{
		for (int i = 0; i < fieldSize; i++)
		{
			for (int j = 0; j < fieldSize; j++)
			{
				buttonField[i][j].setEnabled(false);
			}
		}
		statusField.setText("Waiting for both players to be ready");
		cl.sendField(shipPositions);	
	}
	
	private boolean checkAdjacent(int i, int j)
	{
		for (int size = 0; size < unitPosition.size(); size++)
		{
			String value = unitPosition.get(size);
			int existingI = Integer.parseInt(value.substring(0, 1));
			int existingJ = Integer.parseInt(value.substring(1, 2));
			if ( (existingI + 1) == i || (existingI - 1) == i)
			{
					return true;
			}
			else if ( (existingJ + 1) == j || (existingJ - 1) == j)
			{
					return true;
			}
		}
		return false;
	}
	
	private void setShip(int i, int j)
	{
		buttonField[i][j].setEnabled(false);
		buttonField[i][j].setBackground(Color.GRAY);
	}
	
	/**
	 * update method overridden from implemented Observer.
	 * State and/or data from ClientGUIListener is notified into this method.
	 */
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		if (arg instanceof Message)
		{
			Message message = (Message) arg;
			System.out.println(message.getCode());
			if (message.getCode() != null)
			{
				if (message.getCode().equals("StartGame"))
				{
					statusField.setText("Both ready");
				}
				else if (message.getCode().equals("Turn"))
				{
					statusField.setText("Your turn. Select an opponent square to hit");
					for (int i = 0; i < fieldSize; i++)
					{
						for (int j = 0; j < fieldSize; j++)
						{
							oppbuttonField[i][j].setEnabled(true);
						}
					}
				}
				else if (message.getCode().equals("NoTurn"))
				{
					
					statusField.setText("Waiting for opponent to hit");
					for (int i = 0; i < fieldSize; i++)
					{
						for (int j = 0; j < fieldSize; j++)
						{
							oppbuttonField[i][j].setEnabled(false);
						}
					}
				}
				else if (message.getCode().equals("Hit"))
				{
					if (tookTurn)
					{
						for (int i = 0; i < fieldSize; i++)
						{
							for (int j = 0; j < fieldSize; j++)
							{
								if (oppbuttonField[i][j].getName().equals(lastHit))
								{
									oppbuttonField[i][j].setBackground(Color.RED);
								}
								oppbuttonField[i][j].setEnabled(false);
							}
						}
						tookTurn = false;
					}
					else
					{
						for (int i = 0; i < fieldSize; i++)
						{
							for (int j = 0; j < fieldSize; j++)
							{
								if (buttonField[i][j].getName().equals(message.getHitVal()))
								{
									buttonField[i][j].setBackground(Color.RED);
								}
							}
						}
					}
				}
				else if (message.getCode().equals("Miss"))
				{
					if (tookTurn)
					{
						for (int i = 0; i < fieldSize; i++)
						{
							for (int j = 0; j < fieldSize; j++)
							{
								if (oppbuttonField[i][j].getName().equals(lastHit))
								{
									oppbuttonField[i][j].setBackground(Color.GREEN);
								}
								oppbuttonField[i][j].setEnabled(false);
							}
						}
						tookTurn = false;
					}
					else
					{
						for (int i = 0; i < fieldSize; i++)
						{
							for (int j = 0; j < fieldSize; j++)
							{
								if (buttonField[i][j].getName().equals(message.getHitVal()))
								{
									buttonField[i][j].setBackground(Color.RED);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/***************************INNER CLASSES************************************/
	
	public class ButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < fieldSize; i++)
			{
				for (int j = 0; j < fieldSize; j++)
				{
					if (e.getSource() == buttonField[i][j])
					{
						String origin = null;
						try{
							origin = unitPosition.get(0).toString();
						} catch (IndexOutOfBoundsException e1){
						}
						if (unitPosition.size() == 0)
						{
							unitPosition.add(i + "" + j);
							setShip(i, j);
						}
						else if (origin.charAt(0) == buttonField[i][j].getName().charAt(0) || origin.charAt(1) == buttonField[i][j].getName().charAt(1))
						{
							if (checkAdjacent(i, j))
							{
								if (unitPosition.size() <= 1)
								{
									unitPosition.add(i + "" + j);
									setShip(i, j);
									checkShipSize();
								}
								else
								{
									if (origin.charAt(0) == buttonField[i][j].getName().charAt(0) && unitPosition.get(1).toString().charAt(0) == buttonField[i][j].getName().charAt(0))
									{
										unitPosition.add(i + "" + j);
										setShip(i, j);
										checkShipSize();
									}
									else if (origin.charAt(1) == buttonField[i][j].getName().charAt(1) && unitPosition.get(1).toString().charAt(1) == buttonField[i][j].getName().charAt(1))
									{
										unitPosition.add(i + "" + j);
										setShip(i, j);
										checkShipSize();
									}
								}
							}
						}
					}
					else if (e.getSource() == oppbuttonField[i][j])
					{
						for (int x = 0; x < fieldSize; x++)
						{
							for (int y = 0; y < fieldSize; y++)
							{
								oppbuttonField[x][y].setEnabled(false);
							}
						}
						Message hit = new Message(oppbuttonField[i][j].getName(), false);
						cl.sendMessage(hit);
						lastHit = oppbuttonField[i][j].getName();
						tookTurn = true;
					}
				}
			}
		}
	}
}
