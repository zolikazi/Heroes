package heroes;
import java.io.Serializable;

public class Warrior extends Hero implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public Warrior(PlayerID player_id) {
		super(HeroType.WARRIOR, player_id);
	}

}
