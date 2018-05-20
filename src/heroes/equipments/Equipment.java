package heroes.equipments;

import heroes.Hero.HeroType;
import java.io.Serializable;

public abstract class Equipment implements Serializable {
	public enum EqType {
		NONE					(0),
		WOODEN_SWORD			(1),
		WOODEN_SHIELD			(2),
		IRON_SWORD				(3),
		IRON_SHIELD				(4),
		BLADE_OF_RES			(5),
		SWORD_OF_RES			(6),
		WOODEN_SWORD_OF_FURY	(7),
		IRON_SWORD_OF_FURY		(8),
		ELVEN_DAGGER			(9),
		ELVEN_BLADE				(10),
		ELVEN_ARMOR				(11),
		MYTHRILL_ARMOR			(12),
		ELVEN_BOOTS				(13),
		SHORT_BOW				(14),
		LONG_BOW				(15),
		;
		
		public final int id;
		private EqType(int id) {
			this.id=id;
		}
	}
	public static final int num_of_eq = EqType.values().length;
	private EqType eq_type;
	private String eq_name;
	private int value;
	private DefenseAbility defense=null;
	private HeroType available_for;
	private AttackAbility attack=null;
	private boolean canMoveExtra=false;
	private boolean canRollExtra=false;
	
	public Equipment(EqType eq_type,String eq_name,int value,HeroType available_for, AttackAbility att, DefenseAbility def){
		this.eq_type = eq_type;
		this.eq_name = eq_name;
		this.value = value;
		this.available_for = available_for;
		this.attack = att;
		this.defense = def;
	}
	
	public boolean get_move_extra(){
		return canMoveExtra;
	}
	
	public boolean get_roll_extra(){
		return canRollExtra;
	}
	
	protected void set_move_extr(){
		this.canMoveExtra=true;
	}
	
	protected void set_roll_extr(){
		this.canRollExtra=true;
	}
	
	public EqType get_type() {
		return eq_type;
	}
	
	public int get_type_in_int(){
		return eq_type.id;
	}
	
	public String get_name(){
		return eq_name;
	}
	
	public int get_value(){
		return value;
	}
	public AttackAbility get_attack(){
		return attack;
	}
	public DefenseAbility get_defense(){
		return defense;
	}
	
	public HeroType get_available_for(){
		return available_for;
	}
}
