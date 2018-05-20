package heroes.equipments.archer;

import heroes.Hero.HeroType;
import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;
import heroes.equipments.Equipment;

public abstract class ArcherEquipment extends Equipment{

	public ArcherEquipment(EqType eq_type, String eq_name, int value, AttackAbility att,
			DefenseAbility def) {
		super(eq_type, eq_name, value, HeroType.ARCHER, att, def);
	}

}
