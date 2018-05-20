package heroes.equipments.warrior;

import heroes.Hero.HeroType;
import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;
import heroes.equipments.Equipment;

public abstract class WarriorEquipment extends Equipment{

	public WarriorEquipment(EqType eq_type, String eq_name, int value, AttackAbility att,
			DefenseAbility def) {
		super(eq_type, eq_name, value, HeroType.WARRIOR, att, def);
		// TODO Auto-generated constructor stub
	}

}
