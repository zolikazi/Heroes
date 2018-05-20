package heroes;

import java.io.Serializable;

import heroes.Hero.PlayerID;

public class Click implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int x=-1;
	public int y=-1;
	public PlayerID playerID;
	
	public Click(int x, int y, PlayerID sent_by){
		this.x=x;
		this.y=y;
		this.playerID=sent_by;
	}
}
