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

public class ClientGUIListener extends Observable implements Runnable{
	
	String ip;
	int port;
	
	Object object;
	Socket socket;
	
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	byte checkingbyte;
	
	/**
	 * Default constructor
	 */
	public ClientGUIListener()
	{
		checkingbyte = new Byte((byte) 0);
	}
	
	/**
	 * Method to connect with the given ip and port.
	 * @param ip - ip for socket to connect
	 * @param port - port for socket to connect
	 * @param username - username of the client
	 * @return true if connected
	 */
	public boolean connect(String ip, int port, String username)
	{
		this.ip = ip;
		this.port = port;
		try {
			socket = new Socket(ip, port);
	
			oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			
			sendMessage(new Message("System: ", username + " connected"));
		return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Send Message object through output stream
	 * @param message - Message containing a notify code or message
	 * @return true if sent
	 */
	public boolean sendMessage(Message message)
	{
		try {
			oos.write(checkingbyte);
			oos.writeObject(message);
			oos.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Send ship positions as an ArrayList through output stream
	 * @param al - ArrayList of ship positions
	 * @return boolean - true if sent
	 */
	public boolean sendField(ArrayList<String> al)
	{
		try {
			oos.write(checkingbyte);
			oos.writeObject(al);
			oos.flush();
			System.out.println("Sent");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Method overridden from implemented Runnable.
	 * Listens for new objects from the stream
	 */
	@Override
	public void run() {
		
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
				}
			} catch (SocketException e) {
				stop();
				break;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (EOFException e){
				stop();
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	private void stop()
	{
		System.out.println("Ive disconnected");
		setChanged();

		Message message = new Message("Close");
		notifyObservers(message);
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
