package utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Observable;

public class ClientSocketListener extends Observable implements Runnable{
	
	Socket socket;

	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	int running = 1;
	Object object;
	byte checkingbyte;
	
	/**
	 * Default constructor
	 */
	public ClientSocketListener()
	{	
		checkingbyte = new Byte((byte) 0);
	}
	
	/**
	 * Creates an ObjectOutputStream from socket
	 * @param socket - socket which is connected
	 */
	public void createOutputStream(Socket socket)
	{ 
		this.socket = socket;
		try {
			oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Send message through output stream
	 * @param message - Message which may contain a user message or state
	 * @return true if sent
	 */
	public boolean sendMessage(Message message)
	{
		try {
			oos.write(checkingbyte);
			oos.writeObject(message);
			oos.flush();
			return true;
		} catch (SocketException e){
			
			setChanged();
			notifyObservers("Close");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Method overridden from implemented Runnable.
	 * Listens for new objects from the stream
	 */
	@Override
	public void run() 
	{	
		while (!Thread.currentThread().isInterrupted())
		{
			try {
				ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
				while ((ois.read()) != -1) 
				{
					if ((object = ois.readObject()) instanceof Message)
					{
						Message message = (Message) object;
						setChanged();
						notifyObservers(message);
					}
					else
					{
						setChanged();
						notifyObservers((ArrayList<?>) object);
					}
				}
			} catch (ClassNotFoundException e) {
			} catch (SocketException e) {
				System.out.println("Client has disconnected");
				Thread.currentThread().interrupt();
				setChanged();
				Message message = new Message("Close");
				notifyObservers(message);
			} catch (EOFException e) {
				System.out.println("Client reached end of file, disconnecting client");
				Thread.currentThread().interrupt();
				setChanged();
				Message message = new Message("Close");
				notifyObservers(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	/**
	 * Close streams and socket
	 */
	public void close()
	{
		try {
			ois.close();
			oos.close();
			socket.close();
		} catch (NullPointerException e) {
		} catch (SocketException e) {
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
