package heroes.equipments.warrior;

import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;

public class IronSword extends WarriorEquipment{
	private static AttackAbility attack=new AttackAbility(2, 0, 1,false);
	private static DefenseAbility defense=null;

	public IronSword() {
		super(EqType.IRON_SWORD, "IronSword", 2,attack,defense);
	}
}
