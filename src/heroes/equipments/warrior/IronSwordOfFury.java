package heroes.equipments.warrior;

import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;

public class IronSwordOfFury extends WarriorEquipment {

	private static AttackAbility attack=new AttackAbility(2, 0, 1,true);
	private static DefenseAbility defense=null;

	public IronSwordOfFury() {
		super(EqType.IRON_SWORD_OF_FURY, "IronSwordOfFury", 2,attack,defense);
	}
}
