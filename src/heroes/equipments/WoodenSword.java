package heroes.equipments;
import java.io.Serializable;

public class WoodenSword extends Equipment implements Serializable{
	

	private static AttackAbility attack=new AttackAbility(1, 0, 1,false);
	private static DefenseAbility defense=null;

	public WoodenSword() {
		super(EqType.WOODEN_SWORD, "WoodenSword", 1, null,attack,defense);
		// TODO Auto-generated constructor stub
	}

}
