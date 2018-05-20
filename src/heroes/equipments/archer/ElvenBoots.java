package heroes.equipments.archer;

import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;

public class ElvenBoots extends ArcherEquipment{
	private static AttackAbility att=null;
	private static DefenseAbility def=null;
	
	public ElvenBoots() {
		super(EqType.ELVEN_BOOTS, "ElvenBoots", 3, att, def);
		this.set_roll_extr();
	}
}
