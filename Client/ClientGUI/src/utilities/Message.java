package utilities;

import java.io.Serializable;

public class Message implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5879685342656098783L;
	
	String username;
	String message;
	private String notifyCode;
	
	String hitVal;
	boolean hit;
	
	/**
	 * Constructor that initializes hitVal and hit
	 * @param hitVal - value of position being hit
	 * @param hit - true if hit
	 */
	public Message(String hitVal, boolean hit)
	{
		this.hitVal = hitVal;
		this.hit = hit;
	}
	
	/**
	 * Constructor that initializes notifyCode
	 * @param notifyCode - state of client
	 */
	public Message(String notifyCode)
	{
		this.notifyCode = notifyCode;
	}
	
	/**
	 * Constructor that initializes username and message
	 * @param username - username of the client
	 * @param message - message being sent from the client
	 */
	public Message(String username, String message)
	{
		this.username = username;
		this.message = message;
	}
	
	/**
	 * Set hitVal
	 * @param hitVal - Value of where the position is being hit
	 */
	public void setHit(String hitVal)
	{
		this.hitVal = hitVal;
	}
	
	/**
	 * Get username
	 * @return Returns username
	 */
	public String getUsername()
	{
		return username;
	}
	
	/**
	 * Get notifycode
	 * @return Returns notifycode
	 */
	public String getCode()
	{
		return notifyCode;
	}
	
	/**
	 * Get hit value
	 * @return Returns hit value
	 */
	public String getHitVal()
	{
		return hitVal;
	}
	
	/**
	 * Get boolean of hit
	 * @return Returns hit
	 */
	public boolean getHit()
	{
		return hit;
	}
	
	/**
	 * toString for message output
	 */
	public String toString()
	{
		return "\n" + username + ": " + message;
	}

}
