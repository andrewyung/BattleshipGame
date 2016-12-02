package utilities;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class BattleshipHandler implements Observer{
	
	String player1;
	String player2;
	
	Message message;
	final int fieldSize = 10;
	ArrayList<String> player1Field;
	ArrayList<String> player2Field;
	
	ClientSocketListener csl1;
	ClientSocketListener csl2;
	
	int turn = 0;
	
	/**
	 * Constructor that initializes both ClientSocketListener
	 * @param csl1 - first ClientSocketListener
	 * @param csl2 - second ClientSocketListener
	 */
	public BattleshipHandler(ClientSocketListener csl1, ClientSocketListener csl2)
	{
		this.csl1 = csl1;
		this.csl2 = csl2;
	}

	/**
	 * update method overridden from implemented Observer.
	 * State and/or field is notified from ClientSocketListener
	 */
	@Override
	public synchronized void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		if (arg1 instanceof ArrayList)
		{
			if (player1 == null)
			{
				player1 = arg0.toString();
				player1Field = (ArrayList<String>) arg1;
			}
			else if (player2 == null)
			{
				player2 = arg0.toString();
				player2Field = (ArrayList<String>) arg1;
			}
			
			if (player1 != null && player2 != null)
			{
				message = new Message("StartGame");

				sendBothClients(message);
				//initialize fields
				setRandomTurn();
				runTurns();
			}
		}
		else if (arg1 instanceof Message)
		{
			message = (Message) arg1;
			if (((Message) arg1).getHitVal() != null)
			{
				if(player1.equals(arg0.toString()))
				{
					if (checkHit(message.getHitVal(), player2Field))
					{
						Message hitMessage = new Message("Hit");
						hitMessage.setHit(message.getHitVal());

						sendBothClients(hitMessage);
						checkWinner(player2Field);
					}
					else
					{
						Message hitMessage = new Message("Miss");
						hitMessage.setHit(message.getHitVal());

						sendBothClients(hitMessage);
					}
				}
				else if (player2.equals(arg0.toString()))
				{
					if (checkHit(message.getHitVal(), player1Field))
					{
						Message hitMessage = new Message("Hit");
						hitMessage.setHit(message.getHitVal());

						sendBothClients(hitMessage);
						checkWinner(player1Field);
					}
					else
					{
						Message hitMessage = new Message("Miss");
						hitMessage.setHit(message.getHitVal());
						
						sendBothClients(hitMessage);
					}
				}
				turn++;
				runTurns();
			}
		}
	}
	private void setRandomTurn()
	{
		turn = (int)(Math.random() * 2 + 1);
	}
	
	private void runTurns()
	{
		Message noTurn = new Message("NoTurn");
		Message yesTurn = new Message("Turn");
		if ((turn % 2) == 0)
		{
			csl1.sendMessage(noTurn);
			csl2.sendMessage(yesTurn);
		}
		else
		{
			csl2.sendMessage(noTurn);
			csl1.sendMessage(yesTurn);
		}
	}
	
	private boolean checkHit(String hit, ArrayList<String> playerField)
	{
		for (int i = 0; i < playerField.size(); i++)
		{
			if (hit.equals(playerField.get(i)))
			{
				playerField.remove(i);
				return true;
			}
		}
		return false;
	}
	
	private void sendBothClients(Message message)
	{
		csl1.sendMessage(message);
		csl2.sendMessage(message);
	}
	
	private void checkWinner(ArrayList<String> playerField)
	{
		if (playerField.size() == 0)
		{
			if ((turn % 2) == 0)
			{
				message = new Message("Won");
				csl2.sendMessage(message);

				message = new Message("Lost");
				csl1.sendMessage(message);
			}
			else
			{
				message = new Message("Won");
				csl1.sendMessage(message);

				message = new Message("Lost");
				csl2.sendMessage(message);
			}
		}
	}
}
