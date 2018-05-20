package heroes.equipments.archer;

import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;

public class LongBow extends ArcherEquipment{
	private static AttackAbility att=new AttackAbility(2, 1, 3,false);
	private static DefenseAbility def=null;

	public LongBow() {
		super(EqType.LONG_BOW, "LongBow", 2, att, def);
		this.get_attack().set_blockedBy();
	}
}
