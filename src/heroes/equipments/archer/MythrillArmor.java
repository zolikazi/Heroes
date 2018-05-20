package heroes.equipments.archer;

import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;

public class MythrillArmor extends ArcherEquipment{
	private static AttackAbility att=null;
	private static DefenseAbility def=new DefenseAbility(1);
	
	public MythrillArmor() {
		super(EqType.ELVEN_ARMOR, "MythrillArmor", 1, att, def);
		this.get_defense().set_nr_of_rolls(5);
	}
}
