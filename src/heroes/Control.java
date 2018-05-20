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
 * A  <tt>Control</tt> oszt�ly a vez�rl�s�rt felel�s
 * @author ABence
 */
class Control implements IClick{
	private GUI gui;
	private GameState gs;
	private ArrayList<Click> clicks_to_process;
	
	/**
	 * Szerver aminek k�ldi a control a j�t�k �llapotot (bear)
	 */
	private IGameState s; //bear
	/**
	 * network feladatait kezel� v�ltoz� (bear)
	 */
	private Network net = null;//bear
	
	/**
	 * A server-en a j�t�k vez�rl�s�re egy schedulert ind�tunk, ami figyeli a
	 * kattint�sokat, melyek hat�s�ra tov�bbl�pteti a j�t�kciklust. Az executor �s
	 * a future a scheduler vez�rl�s��rt felel�s v�ltoz�k.
	 */
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> future = null;

	/**
	 * A Control oszt�ly konstruktora. P�ld�nyos�tja a j�t�k �llapot�t ment� v�ltoz�t,
	 * illetve a feldolgozand� kattint�sok list�j�t.
	 */
	Control() {
		gs = new GameState();
		clicks_to_process = new ArrayList<Click>();			
	}
	
	/**
	 * Az �j j�t�k gener�l�s�t v�gz� f�ggv�ny. 
	 * J�t�kosonk�nt hozz�ad 2 Harcost �s 1 �j�szt a j�t�khoz, v�letlenszer�en felszerel�seket v�lasztva.
	 * Ezut�n legener�lja a t�bl�t �s felhelyezi a karaktereket.
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
	 * A t�bla gener�l�s�t v�gz� f�ggv�ny. 
	 */
	
	private void generateBoard(){
		gs.init_map();
		gs.set_starting_positions(5,5);
		gs.set_heroes_starting_positions();		
	}
	
	/**
	 * �sszekapcsolja a GUI p�ld�nyt ezen Control p�ld�nnyal.
	 * @param g A GUI p�ld�ny, melyet a Controlhoz k�v�nunk kapcsolni.
	 */

	void setGUI(GUI g) {
		gui = g;
	}
	
	/**
	 * A t�bla friss�t�s�t v�gz� f�ggv�ny. Elk�ldi az �j t�bla�llapotot a helyi GUI-nak,
	 * tov�bb�, a Networkon kereszt�l tov�bb�tja azt a m�sik oldalnak.
	 */
	void refresh_board(){
		gui.onNewGameState(gs);	
		if (net != null){
			s.onNewGameState(gs);			
		}
	}

	/**
	 * Szerver ind�t�sa, GUI-n kereszt�l.(bear)
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
	 * Adott IP c�men tal�lhat� szerverre val� csatlakoz�s (bear).
	 * @param s: IP c�m
	 */
	void startClient(String s) {//bearStart
		if (net != null)
			net.stop();
		net = new Client(gui);
		net.start(s);//bearEnd
		System.out.println("startclient megvolt!");
	}
	
	/**
	 * Az �temez� elind�t�s��rt felel�s f�ggv�ny. 
	 * Be�ll�tja a megfelel� kommunik�ci�s interf�szeket, p�ld�nyos�tja a k�l�n
	 * sz�lon fut� <code>Runnable</code> objektumot, majd 40 ms-k�nt lefuttatja azt.
	 * A f�ggv�ny v�g�n friss�ti a t�bl�t.
	 * 
	 * A k�l�n sz�lon val� futtat�s c�lja, hogy mind a GUI-n t�rt�n�, mind a Network
	 * fel�l �rkez� esem�nyeket k�nnyebb legyen kezelni, tov�bb� az id�ben magukt�l v�ltoz�
	 * esem�nyek (pl sebz�s eset�n �leter� cs�kken�s) is k�nyebben implement�lhat�ak.
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
	 * Ha a j�t�k m�g nem �rt a v�g�hez, feldolgozza a GUI-n, vagy h�l�zaton �rkez� 
	 * kattint�sokat, felt�ve, hogy azt a soron k�vetkez� j�t�kos k�ldte. A kattintott
	 * mez� alapj�n l�pteti a j�t�kot, majd friss�ti a t�bl�t.
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
	 * Az id�ben �nmagukt�l v�ltoz� esem�nyek ellen�rz�se.
	 * Ha van haldokl� h�s a t�bl�n, akkor a friss�ti a t�bl�t.
	 */
	public void check_for_periodic_change(){
		if(gs.check_and_refresh_if_dying()){
			refresh_board();
		}
	}
	
	/**
	 * A f� folyamat, mely megh�vja a periodikus v�ltoz�sokat, valamint ellen�rzi,
	 * hogy van-e feldolgozand� kattint�s. Ezt h�vja meg az �temezett feladat.
	 */
	
	public void mainProcess(){
		check_for_periodic_change();
		processClicks();
	}
	
	/**
	 * Hozz�adja az interf�szen be�rkez� kattint�sokat a feldolgozand� kattint�sokhoz.
	 * A GUI illetve a Network fel�l is ezen kereszt�l �rkeznek a kattint�sok. 
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
