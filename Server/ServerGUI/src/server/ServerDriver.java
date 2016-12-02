package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import utilities.ClientHandler;
import utilities.Message;

public class ServerDriver {
	
	Message message;
	ServerSocket ss;
	Socket socket1;
	Socket socket2;
	ClientHandler ch;
	int SERVER_PORT = 5555;
	
	public static void main(String args[]) throws IOException
	{
		ServerDriver sd = new ServerDriver();
		sd.startServer();
		sd.listen();
	}
	
	private void startServer(){
		try {
			ss = new ServerSocket(SERVER_PORT);
			
			System.out.println("Server started");
		} catch (IOException e) {
			System.out.println("Unable to create server socket");
		}
	}
	
	private void listen(){
		while (true)
		{
			try {
				ClientHandler ch = new ClientHandler();
				
				System.out.println("Server listening for 1st");
				socket1 = ss.accept();
				
				System.out.println("Server listening for 2nd");
				socket2 = ss.accept();

				ch.runListeners(socket1, socket2);
				
				System.out.println("Created Client handler");
				
			} catch (SocketException e) {
			} catch (IOException e) {
			}
		}
	}

}
