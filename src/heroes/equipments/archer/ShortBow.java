package heroes.equipments.archer;

import heroes.equipments.AttackAbility;
import heroes.equipments.DefenseAbility;

public class ShortBow extends ArcherEquipment{
	
	private static AttackAbility att=new AttackAbility(1, 1, 3,false);
	private static DefenseAbility def=null;

	public ShortBow() {
		super(EqType.SHORT_BOW, "ShortBow", 2, att, def);
		this.get_attack().set_blockedBy();
	}

}
