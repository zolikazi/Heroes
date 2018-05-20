package heroes.equipments;
import java.io.Serializable;

public class WoodenShield extends Equipment implements Serializable{

	private static AttackAbility attack=null;
	private static DefenseAbility defense=new DefenseAbility(1);

	public WoodenShield() {
		super(EqType.WOODEN_SHIELD, "WoodenShield", 1, null,attack,defense);
		// TODO Auto-generated constructor stub
	}

}
