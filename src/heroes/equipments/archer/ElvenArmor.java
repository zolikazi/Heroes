package heroes.equipments.archer;

import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;

public class ElvenArmor extends ArcherEquipment{

	private static AttackAbility att=null;
	private static DefenseAbility def=new DefenseAbility(1);
	
	public ElvenArmor() {
		super(EqType.ELVEN_ARMOR, "ElvenArmor", 1, att, def);
		this.get_defense().set_nr_of_rolls(3);
	}

}
