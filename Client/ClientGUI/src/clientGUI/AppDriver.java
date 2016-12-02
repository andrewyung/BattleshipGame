package clientGUI;

import utilities.ClientGUIListener;

public class AppDriver {

	public static void main(String args[])
	{
		ClientGUIListener clGUI = new ClientGUIListener();
		MessengerGUI mf = new MessengerGUI();
		clGUI.addObserver(mf);
		mf.createMainFrame(clGUI);
	}
}	