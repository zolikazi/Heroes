package heroes.equipments.warrior;

import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;

public class IronShield extends WarriorEquipment{
	private static AttackAbility attack=null;
	private static DefenseAbility defense=new DefenseAbility(2);

	public IronShield() {
		super(EqType.IRON_SHIELD, "IronShield", 2,attack,defense);
	}
}
