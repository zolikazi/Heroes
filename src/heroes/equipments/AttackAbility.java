package heroes.equipments;
import java.io.Serializable;

public class AttackAbility implements Serializable{
	private int strength;
	private int minRadius;
	private int maxRadius;
	private boolean attackAllNearby=false;
	private boolean blockedByNearby=false;
	
	public AttackAbility(int str, int minR, int maxR, boolean nearby) {
		// TODO Auto-generated constructor stub
		this.strength=str;
		this.minRadius=minR;
		this.maxRadius=maxR;
		this.attackAllNearby=nearby;
	}
	
	public int get_strength(){
		return strength;
	}
	
	public int get_minR(){
		return minRadius;
	}
	
	public int get_maxR(){
		return maxRadius;
	}
	
	public boolean get_allNearby(){
		return attackAllNearby;
	}
	
	public boolean get_blockedBy(){
		return blockedByNearby;
	}
	
	public void set_blockedBy(){
		blockedByNearby=true;
	}
}
