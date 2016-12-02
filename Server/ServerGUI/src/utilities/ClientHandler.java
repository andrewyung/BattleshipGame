package utilities;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class ClientHandler implements Observer{

	Socket socket1;
	Socket socket2;
	ClientSocketListener csl1;
	ClientSocketListener csl2;
	Observable ob1;
	Observable ob2;
	
	Message message;
	
	Thread th1;
	Thread th2;
	
	BattleshipHandler battleshipHandler;
	int play = 0;
	
	/**
	 * Default constructor
	 */
	public ClientHandler()
	{
	}
	
	/**
	 * Start streams and link Observer/Observable
	 * @param socket1 - socket to be used for inputting/outputting
	 * @param socket2 - socket to be used for inputting/outputting
	 */
	public void runListeners(Socket socket1, Socket socket2)
	{	
		csl1 = new ClientSocketListener();
		csl1.addObserver(this);
		csl1.createOutputStream(socket1);
		
		csl2 = new ClientSocketListener();
		csl2.addObserver(this);
		csl2.createOutputStream(socket2);
		
		th1 = new Thread(csl1);
		th1.start();
		
		th2 = new Thread(csl2);
		th2.start(); 
	}
	
	/**
	 * update method overridden from implemented Observer
	 * State and/or data is notified from ClientSocketListener
	 */
	@Override
	public synchronized void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		if (arg1 instanceof Message)
		{
			Message message = (Message) arg1;
			if (message.getCode() != null)
			{
				if (message.getCode().equals("Close"))
				{
					System.out.println("Closing ClientHandler");
					close();
				}
				else if (message.getCode().equals("Request"))
				{
					message = new Message("Request");
					sendBothClients(message);
				}
				else if (message.getCode().equals("Play") || message.getCode().equals("NoPlay"))
				{
					if (message.getCode().equals("Play"))
					{
						play++;
					}
					else if (message.getCode().equals("NoPlay"))
					{
						if (play >= 0)
						{
							play--;
						}
						else
						{
							play = 0;
						}
						message = new Message("NoPlay");
						sendBothClients(message);
					}
					
					if (play == 2)
					{
						message = new Message("Start");
						sendBothClients(message);
						play = 0;
						
						battleshipHandler = new BattleshipHandler(csl1, csl2);
						csl1.addObserver(battleshipHandler);
						csl2.addObserver(battleshipHandler);
					}
				}
			}
			else
			{
				sendBothClients(message);
			}
		}
	}
	
	private void sendBothClients(Message message)
	{
		csl1.sendMessage(message);
		csl2.sendMessage(message);
	}
		
	private void close()
	{
			if (!th1.isInterrupted())
			{
				th1.interrupt();
				csl1.close();
			}

			if (!th2.isInterrupted())
			{
				th2.interrupt();
				csl2.close();
			}
	}
}
