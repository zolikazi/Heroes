package heroes.equipments.warrior;

import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;

public class ElvenDagger extends WarriorEquipment{
	private static AttackAbility attack=new AttackAbility(1, 0, 1,false);
	private static DefenseAbility defense=null;

	public ElvenDagger() {
		super(EqType.ELVEN_DAGGER, "ElvenDagger", 1,attack,defense);
		this.set_move_extr();
	}
}
