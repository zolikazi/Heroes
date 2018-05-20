package heroes.equipments.warrior;

import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;

public class WoodenSwordOfFury extends WarriorEquipment{

	private static AttackAbility attack=new AttackAbility(1, 0, 1,true);
	private static DefenseAbility defense=null;

	public WoodenSwordOfFury() {
		super(EqType.WOODEN_SWORD_OF_FURY, "WoodenSwordOfFury", 2,attack,defense);
	}
}
