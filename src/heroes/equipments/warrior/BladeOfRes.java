package heroes.equipments.warrior;

import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;

public class BladeOfRes extends WarriorEquipment{
	private static AttackAbility attack=new AttackAbility(1, 0, 1, false);
	private static DefenseAbility defense=new DefenseAbility(1);

	public BladeOfRes() {
		super(EqType.BLADE_OF_RES, "BladeOfResistance", 2,attack,defense);
	}
}
