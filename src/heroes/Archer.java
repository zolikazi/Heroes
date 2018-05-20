package heroes;
import java.io.Serializable;

public class Archer extends Hero implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public Archer(PlayerID player_id) {
		super(HeroType.ARCHER, player_id);
	}

}
