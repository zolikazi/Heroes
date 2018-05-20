package heroes.equipments.warrior;

import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;

public class ElvenBlade extends WarriorEquipment{
	private static AttackAbility attack=new AttackAbility(2, 0, 1,false);
	private static DefenseAbility defense=null;

	public ElvenBlade() {
		super(EqType.ELVEN_BLADE, "ElvenBlade", 1,attack,defense);
		this.set_move_extr();
	}
}
