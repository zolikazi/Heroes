/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package heroes;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import heroes.Hero.PlayerID;
import heroes.equipments.Equipment;
import heroes.equipments.Equipment.EqType;
import heroes.equipments.WoodenShield;
import heroes.equipments.WoodenSword;
import heroes.equipments.archer.ElvenArmor;
import heroes.equipments.archer.ElvenBoots;
import heroes.equipments.archer.LongBow;
import heroes.equipments.archer.MythrillArmor;
import heroes.equipments.archer.ShortBow;
import heroes.equipments.warrior.BladeOfRes;
import heroes.equipments.warrior.ElvenBlade;
import heroes.equipments.warrior.ElvenDagger;
import heroes.equipments.warrior.IronShield;
import heroes.equipments.warrior.IronSword;
import heroes.equipments.warrior.IronSwordOfFury;
import heroes.equipments.warrior.SwordOfRes;
import heroes.equipments.warrior.WoodenSwordOfFury;

/**
 * A  <tt>Control</tt> osztály a vezérlésért felelõs
 * @author ABence
 */
class Control implements IClick{
	private GUI gui;
	private GameState gs;
	private ArrayList<Click> clicks_to_process;
	
	/**
	 * Szerver aminek küldi a control a játék állapotot (bear)
	 */
	private IGameState s; //bear
	/**
	 * network feladatait kezelõ változó (bear)
	 */
	private Network net = null;//bear
	
	/**
	 * A server-en a játék vezérlésére egy schedulert indítunk, ami figyeli a
	 * kattintásokat, melyek hatására továbblépteti a játékciklust. Az executor és
	 * a future a scheduler vezérléséért felelõs változók.
	 */
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> future = null;

	/**
	 * A Control osztály konstruktora. Példányosítja a játék állapotát mentõ változót,
	 * illetve a feldolgozandó kattintások listáját.
	 */
	Control() {
		gs = new GameState();
		clicks_to_process = new ArrayList<Click>();			
	}
	
	/**
	 * Az új játék generálását végzõ függvény. 
	 * Játékosonként hozzáad 2 Harcost és 1 Íjászt a játékhoz, véletlenszerûen felszereléseket választva.
	 * Ezután legenerálja a táblát és felhelyezi a karaktereket.
	 */
	void generate_new_game(){
		gs = new GameState();
		clicks_to_process = new ArrayList<Click>();	
		//Adding Warriors
				for (int i = 0; i < 4; i++) {
					Warrior w = null;
					if(i%2==0){
						w = new Warrior(PlayerID.CLIENT);				
					}
					else{
						w = new Warrior(PlayerID.SERVER);
					}

					for (int j = 0; j < 6; j++) {
						Random r = new Random();
						int n = r.nextInt(11);
						switch (n) {
						case 1:
							w.add_equip(new WoodenShield());
							break;
						case 2:
							w.add_equip(new WoodenSword());
							break;
						case 3:
							w.add_equip(new IronShield());
							break;
						case 4:
							w.add_equip(new IronSword());
							break;
						case 5:
							w.add_equip(new BladeOfRes());
							break;
						case 6:
							w.add_equip(new SwordOfRes());
							break;
						case 7:
							w.add_equip(new WoodenSwordOfFury());
							break;
						case 8:
							w.add_equip(new IronSwordOfFury());
							break;
						case 9:
							w.add_equip(new ElvenDagger());
							break;
						case 10:
							w.add_equip(new ElvenBlade());
							break;
						default:
							break;
						}				
					}
					gs.add_hero(w);
				}

				for (int i = 0; i < 2; i++) {
					Archer a = null;
					if(i%2==0){
						a = new Archer(PlayerID.SERVER);					
					}
					else{
						a = new Archer(PlayerID.CLIENT);	
					}

					for (int j = 0; j < 6; j++) {
						Random r = new Random();
						int n = r.nextInt(8);
						switch (n) {
						case 1:
							a.add_equip(new WoodenShield());
							break;
						case 2:
							a.add_equip(new WoodenSword());
							break;
						case 3:
							a.add_equip(new ElvenArmor());
							break;
						case 4:
							a.add_equip(new MythrillArmor());
							break;
						case 5:
							a.add_equip(new ElvenBoots());
							break;
						case 6:
							a.add_equip(new ShortBow());
							break;
						case 7:
							a.add_equip(new LongBow());
							break;
						default:
							break;
						}				
					}
					gs.add_hero(a);
				}
				
				generateBoard();
				refresh_board();
	}
	
	/**
	 * A tábla generálását végzõ függvény. 
	 */
	
	private void generateBoard(){
		gs.init_map();
		gs.set_starting_positions(5,5);
		gs.set_heroes_starting_positions();		
	}
	
	/**
	 * Összekapcsolja a GUI példányt ezen Control példánnyal.
	 * @param g A GUI példány, melyet a Controlhoz kívánunk kapcsolni.
	 */

	void setGUI(GUI g) {
		gui = g;
	}
	
	/**
	 * A tábla frissítését végzõ függvény. Elküldi az új táblaállapotot a helyi GUI-nak,
	 * továbbá, a Networkon keresztül továbbítja azt a másik oldalnak.
	 */
	void refresh_board(){
		gui.onNewGameState(gs);	
		if (net != null){
			s.onNewGameState(gs);			
		}
	}

	/**
	 * Szerver indítása, GUI-n keresztül.(bear)
	 */
	void startServer() {
		//generateBoard();
		gui.onNewGameState(gs);	
		if (net != null)//bearStart
			net.stop();
		net = new Server(this, gui);
		net.start("localhost");//bearEnd
		System.out.println("start szerver megvolt");
	}

	/**
	 * Adott IP címen található szerverre való csatlakozás (bear).
	 * @param s: IP cím
	 */
	void startClient(String s) {//bearStart
		if (net != null)
			net.stop();
		net = new Client(gui);
		net.start(s);//bearEnd
		System.out.println("startclient megvolt!");
	}
	
	/**
	 * Az ütemezõ elindításáért felelõs függvény. 
	 * Beállítja a megfelelõ kommunikációs interfészeket, példányosítja a külön
	 * szálon futó <code>Runnable</code> objektumot, majd 40 ms-ként lefuttatja azt.
	 * A függvény végén frissíti a táblát.
	 * 
	 * A külön szálon való futtatás célja, hogy mind a GUI-n történõ, mind a Network
	 * felõl érkezõ eseményeket könnyebb legyen kezelni, továbbá az idõben maguktól változó
	 * események (pl sebzés esetén életerõ csökkenés) is könyebben implementálhatóak.
	 */
	
	void startScheduler(){
		System.out.println("scheduler elinditva");
		this.gui.setClick(this); //bear
		s = (Server) net; //bear
		
		this.gui.enable_new_game_button();
		
		Runnable periodicTask = new Runnable() {
			public void run() {
				//System.out.println("Periodic task started");	
				try {
					mainProcess();					
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("Error in executing periodicTask");
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		};

		if (future == null || future.isCancelled())
			future = executor.scheduleAtFixedRate(periodicTask, 0, 40, TimeUnit.MILLISECONDS);
		
		refresh_board();		
	}
	
	/**
	 * Ha a játék még nem ért a végéhez, feldolgozza a GUI-n, vagy hálózaton érkezõ 
	 * kattintásokat, feltéve, hogy azt a soron következõ játékos küldte. A kattintott
	 * mezõ alapján lépteti a játékot, majd frissíti a táblát.
	 */
	
	public void processClicks(){
		
		
		if(clicks_to_process.isEmpty()){
			return;
		}
		
		if(gs.get_winner()!=null){
			return;
		}

		if(clicks_to_process.size()!=1)
			System.out.println("NR_OF_CLICKS AT PROCESS "+clicks_to_process.size());
		Click c = clicks_to_process.remove(0);
		if(gs.get_current_hero()==null){
			return;
		}
		if(c.playerID!=gs.get_current_hero().get_player_id()){
			return;
		}
		gs.interact(c.x, c.y);
		refresh_board();	
	}
	
	/**
	 * Az idõben önmaguktól változó események ellenõrzése.
	 * Ha van haldokló hõs a táblán, akkor a frissíti a táblát.
	 */
	public void check_for_periodic_change(){
		if(gs.check_and_refresh_if_dying()){
			refresh_board();
		}
	}
	
	/**
	 * A fõ folyamat, mely meghívja a periodikus változásokat, valamint ellenõrzi,
	 * hogy van-e feldolgozandó kattintás. Ezt hívja meg az ütemezett feladat.
	 */
	
	public void mainProcess(){
		check_for_periodic_change();
		processClicks();
	}
	
	/**
	 * Hozzáadja az interfészen beérkezõ kattintásokat a feldolgozandó kattintásokhoz.
	 * A GUI illetve a Network felõl is ezen keresztül érkeznek a kattintások. 
	 */

	@Override
	public void onNewClick(Click click) {
		if (future == null || future.isCancelled())
			System.out.println("SCHEDULER NOT RUNNING !!!!! ");	
			
		clicks_to_process.add(click);
		if(clicks_to_process.size()!=1)
			System.out.println("NR_OF_CLICKS "+clicks_to_process.size());					
	}
}
