package heroes.equipments.warrior;

import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;

public class SwordOfRes extends WarriorEquipment{
	private static AttackAbility attack=new AttackAbility(1, 0, 1,false);
	private static DefenseAbility defense=new DefenseAbility(2);

	public SwordOfRes() {
		super(EqType.SWORD_OF_RES, "SwordOfResistance", 3,attack,defense);
	}
}
