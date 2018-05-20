/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package heroes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import heroes.Hero.HeroType;
import heroes.Hero.PlayerID;
import heroes.equipments.Equipment;

/**
 * A <tt>GUI</tt> oszt�ly a megjelen�t�s�rt felel�s
 * 
 * @author ABence
 */
public class GUI extends JFrame implements IGameState, ComponentListener{

	private static final long serialVersionUID = 1L;
	private Control ctrl;
	public int FIELD_SIZE = 60;
	private int TABLE_SIZE_X = 8, TABLE_SIZE_Y = 8;
	
	private static int MENUBAR_OFFSET = 54;
	private static int WINDOW_BORDER_OFFSET = 8;
	
	public int BOARD_OFFSET = FIELD_SIZE/2;
	private int BOARD_WIDTH = TABLE_SIZE_X*FIELD_SIZE, BOARD_HEIGHT = TABLE_SIZE_Y*FIELD_SIZE;
	
	private int WINDOW_WIDTH = 2*BOARD_OFFSET+BOARD_WIDTH+WINDOW_BORDER_OFFSET*2;
	private int WINDOW_HEIGHT = 2*BOARD_OFFSET+BOARD_HEIGHT+MENUBAR_OFFSET+WINDOW_BORDER_OFFSET;
	
	private GameState gui_gs;
	

	private int WARRIOR_DRAW_RADIUS = 30;
	private int ARCHER_DRAW_MARGIN = 15;

	private static Color col_bg = new Color(0xF5F2DC);
	private static Color col_field_bg = new Color(0x7A797A);
	private static Color col_win_text = new Color(0xE82C0C);
	private static Color col_current_hero_mark= new Color(0xF0CA4D);
	private static Color col_hero_client = new Color(0xFF5729);	
	private static Color col_hero_server = new Color(0x009494);
	private static Color col_start_client = new Color(0xFF5729);	
	private static Color col_start_server = new Color(0x009494);
	private static Color col_extra_step = new Color(0x324D5C);
	private static Color col_wanna_step = new Color(0x46B29D);
	private static Color col_dying_hero = new Color(0xDE5B49);
	
	public static int hero_death_decr = 11;
	
	private GamePanel gamePanel;
	
	private IClick click = null; //bear
	
	private JMenuBar menuBar;
	private JMenuItem menuItem_new_game;
	private JFrame frame;
	
	
	void setClick(IClick c) { //bear
		click = c;
	}
	
	/**
	 * A GUI oszt�ly konstruktora. L�trehozza
	 * 
	 * @param c Control objektum
	 */
	GUI(Control c) {
		super("Heroes");
		
		
		
		ctrl = c;
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(null);
		gui_gs = new GameState();

		menuBar = new JMenuBar();

		JMenu menu = new JMenu("Start");

		JMenuItem menuItem = new JMenuItem("Client");
		menuItem.addActionListener(new MenuListener());
		menu.add(menuItem);

		menuItem = new JMenuItem("Server");
		menuItem.addActionListener(new MenuListener());
		menu.add(menuItem);

		menuBar.add(menu);

		menuItem_new_game = new JMenuItem("New Game");
		menuItem_new_game.addActionListener(new MenuListener());
		menuItem_new_game.setEnabled(false);
		menuBar.add(menuItem_new_game);

		menuItem = new JMenuItem("Exit");
		menuItem.addActionListener(new MenuListener());
		menuBar.add(menuItem);

		setJMenuBar(menuBar);
		setVisible(true);

		gamePanel = new GamePanel();
		update_game_panel_bounds();
		//gamePanel.setBorder(BorderFactory.createTitledBorder("Game"));
		add(gamePanel);
		this.addComponentListener(this);
	}
	
	/**
	 * Be�ll�tja a j�t�kt�r hat�rait.
	 */
	private void update_game_panel_bounds(){
		gamePanel.setBounds(BOARD_OFFSET, BOARD_OFFSET, BOARD_WIDTH, BOARD_HEIGHT);		
	}
	
	private Color get_col_with_alpha(Color col, int alpha){
		return new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha);
	}
	
	/**
	 * Seg�df�ggv�ny a Gamepanel-en t�r�tn� rajzol�shoz
	 * <p>
	 * A <code>FIELD_SIZE</code> egy mez� sz�less�g�t adja meg
	 * @param x hanyadik mez�be szeretn�nk rajzolni x szerint
	 * @return pixelben megadja, hogy az x-edik milyen t�vol van a j�t�kt�r sz�l�t�l
	 */
	private int get_x_offset(int x){
		return x*FIELD_SIZE;
	}
	
	/**
	 * Seg�df�ggv�ny a Gamepanel-en t�r�tn� rajzol�shoz
	 * <p>
	 * A <code>FIELD_SIZE</code> egy mez� magass�g�t adja meg
	 * @param y hanyadik mez�be szeretn�nk rajzolni y szerint
	 * @return pixelben megadja, hogy az y-edik milyen t�vol van a j�t�kt�r sz�l�t�l
	 */
	private int get_y_offset(int y){
		return y*FIELD_SIZE;
	}
	
	/**
	 * Enged�lyezi az �j j�t�k gomb megnyom�s�t
	 */
	public void enable_new_game_button(){
		menuItem_new_game.setEnabled(true);
	}
	
	/**
	 * A GUI men�j�ben t�rt�n� kattint�sokat figyeli
	 * 
	 * @author javor
	 *
	 */
	private class MenuListener implements ActionListener {
		
		/**
		 * A men�elemekre t�r�n� kattint�sok eset�n t�rt�n� beavatkoz�sokat v�gzi.
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("New Game")) {
				gui_gs = new GameState();
				ctrl.generate_new_game();
								
			}
			if (e.getActionCommand().equals("Exit")) {
				System.exit(0);
			}
			if (e.getActionCommand().equals("Server")) {
				
				ctrl.startServer();
				
				
			}
			if (e.getActionCommand().equals("Client")) {
				String s = (String) JOptionPane.showInputDialog(frame, "Server IP:", "Connecting...",
						JOptionPane.PLAIN_MESSAGE, null, null, "localhost");
				ctrl.startClient(s);//bear
				
			}
		}
	}
	
	/**
	 * A j�t�kt�r. Tagf�ggv�nyei felel�sek a minden, a j�t�kt�ren t�rt�n� dolog 
	 * (harcos, l�p�si lehet�s�g, j�t�k v�ge sz�veg) kirajzol�s��rt
	 * 
	 * @author javor
	 *
	 */	
	private class GamePanel extends JPanel implements MouseListener{

		private static final long serialVersionUID = 1L;
		
		/**
		 * A Gamepanel oszt�ly konstruktora. Be�ll�tja a kattint�sok figyel�s�t.
		 */
		public GamePanel(){
			this.addMouseListener(this);
		}
		
		/**
		 * Kirajzolja azokat a mez�ket, ahov� l�phet h�s.
		 * @param g {@link Graphics} objektum
		 */
		private void draw_valid_fields(Graphics g) {
			for (int i = 0; i < TABLE_SIZE_X; i++) {
				for (int j = 0; j < TABLE_SIZE_Y; j++) {
					if(gui_gs.valid_field[i][j]){
						int off_x = get_x_offset(i);
						int off_y = get_y_offset(j);
						g.setColor(col_field_bg);
						g.fillRect(off_x, off_y, FIELD_SIZE, FIELD_SIZE);	
					}					
				}
			}			
		}
		
		/**
		 * A f�ggv�ny v�gzi a j�t�kosok kezd�poz�ci�inak rajzol�s�t
		 * 
		 * @param g {@link Graphics} objektum
		 */
		private void draw_starting_positions(Graphics g) {
			List<Click> start_pos=gui_gs.start_pos;
			if(start_pos!=null){
				for(Click c:start_pos){
					int off_x = get_x_offset(c.x);
					int off_y = get_y_offset(c.y);
					switch (c.playerID) {
					case CLIENT:
						g.setColor(col_start_client);
						break;
					case SERVER:
						g.setColor(col_start_server);
						break;
					default:
						g.setColor(Color.black);
						break;
					}
					g.fillRect(off_x, off_y, FIELD_SIZE, FIELD_SIZE);
				}
			}
		}
		
		/**
		 * Amennyiben egy karakter a felszerel�s�nek megfelel�en
		 * extra kl�p�st tehet, a f�ggv�ny kirajzolja az �sszes
		 * lehets�ges l�p�st
		 * @param g {@link Graphics} objektum
		 */
		private void draw_all_steppable(Graphics g) {
			for(Click c:gui_gs.extra_steps){
				int off_x = get_x_offset(c.x);
				int off_y = get_y_offset(c.y);
				g.setColor(col_extra_step);
				g.fillRect(off_x, off_y, FIELD_SIZE, FIELD_SIZE);
				
			}	
		}

		/**
		 * Felrajzolja a l�p�si lehet�s�get, ha a j�t�kos olyan mez�re kattint, 
		 * ahov� az adott karakter l�phet
		 * @param g {@link Graphics} objektum
		 */
		private void draw_steppable(Graphics g) {
			Click st = gui_gs.wanna_step;
			if(st!=null){
				int off_x = get_x_offset(st.x);
				int off_y = get_y_offset(st.y);
				g.setColor(col_wanna_step);
				g.fillRect(off_x, off_y, FIELD_SIZE, FIELD_SIZE);					
			}
		}
		
		/**
		 * A f�ggv�ny a bemen� stringet ki�rja a j�t�kt�r k�zep�re, 
		 * figyelembe v�ve a \n sort�r�s karaktereket.
		 * @param g {@link Graphics} objektum
		 * @param text a ki�rnd� sz�veg
		 * @param width a j�t�kt�r sz�less�ge
		 * @param height a j�t�kt�r magass�ga
		 */
		private void drawStringCenter(Graphics g, String text, int width, int height){
	    	FontMetrics metrics = g.getFontMetrics();
			String[] txt_arr = text.split("\n");
			int y = height/2-txt_arr.length*metrics.getHeight();
			
	        for (String line : txt_arr)       	
	            g.drawString(line, (width - metrics.stringWidth(line)) / 2, y += metrics.getHeight());
	    }
		
		/**
		 * A f�ggv�ny megvizsg�lja, hogy v�get �rt-e a j�t�k,
		 * ha igen, ki�rja a j�t�kosnak, hogy nyert vagy vesztett.
		 * @param g {@link Graphics}
		 * @return logikai �rt�k, hogy a j�t�k befejez�d�tt-e
		 */
		private boolean draw_end_game_text(Graphics g){
			boolean did_i_win=false;
			PlayerID winner = gui_gs.get_winner();
			if(winner==null){
				return false;
			}
			switch (winner) {
			case SERVER:
				if(am_i_the_server()){
					did_i_win=true;
				}
				break;
			case CLIENT:
				if(am_i_the_server()==false){
					did_i_win=true;
				}
				break;
			default:
				return false;
			}
			g.setColor(col_win_text);
			g.setFont(new Font("Times New Roman", Font.BOLD, BOARD_WIDTH/20));
			if(did_i_win){
				drawStringCenter(g, "Congratulations!\nYou won the Game!", BOARD_WIDTH, BOARD_HEIGHT);
			}
			else{
				drawStringCenter(g, "You lost the Game!\nBetter luck next time!", BOARD_WIDTH, BOARD_HEIGHT);				
			}
			return true;
		}
		
		/**
		 * A f�ggv�ny h�romsz�get rajzol a mez� sz�l�re
		 * @param g {@link Graphics}
		 * @param x a mez� x param�tere pixelben
		 * @param y a mez� y param�tere pixelben
		 * @param size a mez� m�ret�nek negyede
		 * @param rot_clckwise_90 Megadja, hogy melyik sarokba ker�lj�n a h�romsz�g
		 */
		private void draw_small_triangle(Graphics g, int x, int y, int size, int rot_clckwise_90){
			Polygon p = new Polygon();
			switch (rot_clckwise_90) {
			case 0:
				p.addPoint(x+size, y);
				p.addPoint(x, y+size);
				p.addPoint(x, y);    
			    g.fillPolygon(p);
				break;
			case 1:
				p.addPoint(x-size, y);
				p.addPoint(x, y+size);
				p.addPoint(x, y);    
			    g.fillPolygon(p);
				break;
			case 2:
				p.addPoint(x-size, y);
				p.addPoint(x, y-size);
				p.addPoint(x, y);    
			    g.fillPolygon(p);
				break;
			case 3:
				p.addPoint(x+size, y);
				p.addPoint(x, y-size);
				p.addPoint(x, y);    
			    g.fillPolygon(p);
				break;

			default:
				break;
			}
			
		}
		
		/**
		 * A v�dekez�si �rt�knek megfelel� sz�m� h�romsz�g rajzol�sa a h�s mez�j�nek sarkaiba
		 * @param g {@link Graphics}
		 * @param h h�s
		 */
		protected void draw_defense(Graphics g, Hero h){
			int off_x = get_x_offset(h.get_x());
			int off_y = get_y_offset(h.get_y());			
			int size = FIELD_SIZE/4;
			switch (h.get_current_defense()) {
			case 2:
				draw_small_triangle(g, off_x+FIELD_SIZE, off_y, size, 1);
				draw_small_triangle(g, off_x, off_y+FIELD_SIZE, size, 3);
				/*FALLTHRU*/
			case 1:	
				draw_small_triangle(g, off_x, off_y, size, 0);
				draw_small_triangle(g, off_x+FIELD_SIZE, off_y+FIELD_SIZE, size, 2);
				break;
			default:
				break;
			}
		}
		
		/**
		 * A mez� k�zep�re igaz�tott k�r rajzol�sa
		 * @param g {@link Graphics}
		 * @param x a mez� k�z�ppontja pixelben <i>x</i> koordin�ta szerint
		 * @param y a mez� k�z�ppontja pixelben <i>y</i> koordin�ta szerint
		 * @param r a k�r �tm�r�je
		 */
		private void drawCenteredCircle(Graphics g, int x, int y, int r) {
			  x = x-(r/2);
			  y = y-(r/2);
			  g.fillOval(x,y,r,r);
			}
		
		/**
		 * H�s megrajzol�sa
		 * @param g {@link Graphics}
		 * @param h a h�s t�usa
		 * @param off_x mez� <i>x</i> koordin�t�ja pixelben
		 * @param off_y a mez� <i>y</i> koordin�t�ja pixelben
		 */
		private void draw_hero(Graphics g, HeroType h, int off_x, int off_y){
			switch (h) {
			case WARRIOR:
				drawCenteredCircle(g,off_x+FIELD_SIZE/2, off_y+FIELD_SIZE/2, 
						WARRIOR_DRAW_RADIUS);
				break;
			case ARCHER:
				Polygon p = new Polygon();		
				p.addPoint(off_x+FIELD_SIZE/2, off_y+ARCHER_DRAW_MARGIN);
				p.addPoint(off_x+ARCHER_DRAW_MARGIN, off_y+FIELD_SIZE-ARCHER_DRAW_MARGIN);
				p.addPoint(off_x+FIELD_SIZE-ARCHER_DRAW_MARGIN, off_y+FIELD_SIZE-ARCHER_DRAW_MARGIN);	    
			    g.fillPolygon(p);
				break;

			default:
				break;
			}
		}
		
		protected void draw_eq(Graphics g, Equipment e, int off_x, int off_y){
			int num = 0;
			if(e!=null){
				num = e.get_type_in_int();
			}
			g.setColor(Color.black);
			g.drawString(Integer.toString(num), off_x+FIELD_SIZE/2-5, off_y+FIELD_SIZE/2+7);
		}
		
		/**
		 * Az �sszes h�s rajzol�sa
		 * @param g {@link Graphics}
		 */
		private void draw_heroes(Graphics g){
			for(Hero h : gui_gs.heroes){
				if(h.get_health() % 6 >2){
					int off_x = get_x_offset(h.get_x());
					int off_y = get_y_offset(h.get_y());
					g.setColor(col_field_bg.darker());	
					if(h.get_attackable()){
						g.setColor(Color.red);	
					}
					if(!gui_gs.extra_steps.isEmpty() && h==gui_gs.get_current_hero()){
						g.setColor(col_extra_step);						
					}
					if(h.get_dying()){
						g.setColor(col_dying_hero);
					}
					if(gui_gs.wanna_step!=null){
						if(h.get_x()==gui_gs.wanna_step.x){
							if(h.get_y()==gui_gs.wanna_step.y){
								g.setColor(col_wanna_step);								
							}
						}
					}
					g.fillRect(off_x, off_y, FIELD_SIZE, FIELD_SIZE);
					switch (h.get_player_id()) {
					case CLIENT:
						g.setColor(col_hero_client);					
						break;
					case SERVER:	
						g.setColor(col_hero_server);				
						break;
	
					default:
						g.setColor(Color.black);
						break;
					}
					draw_defense(g,h);
					//h.draw(g, off_x,  off_y);
					draw_hero(g,h.get_type(),off_x,off_y);
					draw_eq(g,h.get_last_rolled_equip(),off_x,off_y);
				}
			}
		}
		
		/**
		 * A k�vetkez� l�p�sre jogosult h�s k�r� s�rga keret rajzol�sa
		 * @param g {@link Graphics}
		 */
		private void draw_current_hero_mark(Graphics g){
			Hero h = gui_gs.get_current_hero();
			int off_x = get_x_offset(h.get_x());
			int off_y = get_y_offset(h.get_y());
			g.setColor(col_current_hero_mark);
			g.drawRect(off_x, off_y, FIELD_SIZE-1, FIELD_SIZE-1);
			g.drawRect(off_x+1, off_y+1, FIELD_SIZE-3, FIELD_SIZE-3);
		}
		
		/**
		 * A f�ggv�ny megrajzolja a j�t�kteret a h�s�kkl egy�tt
		 * @param g {@link Graphics}
		 */
		@Override
		public void paintComponent(Graphics g) {
			g.setColor(col_bg);
			g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
			g.setColor(Color.black);
			g.setFont(new Font("Times New Roman", Font.BOLD, 24));		
			if(draw_end_game_text(g)){
				return;
			}
			draw_valid_fields(g);
			draw_starting_positions(g);
			draw_all_steppable(g);
			draw_steppable(g);
			draw_heroes(g);
			
			if(gui_gs.heroes.isEmpty()){
				return;
			}
			
			draw_current_hero_mark(g);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		/**
		 * A f�ggv�ny megn�zi, hogy a j�t�kos hov� kattintott. Ezt ki�rja a konzolra, illetve, 
		 * megn�zi, hogy ez val�s mez� �s nincs ott m�sik h�s
		 * @param e Kattint�s az eg�rrel
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

			int x = e.getX() / FIELD_SIZE;
			if(x <0 || x>= TABLE_SIZE_X){
				return;
			}
			int y = e.getY() / FIELD_SIZE ;	
			if(y <0 || y>= TABLE_SIZE_Y){
				return;
			}
			System.out.println("X:" + x + " Y:" + y+ "    e-X:" + e.getX() + " Y:" + e.getY());
			
			if(gui_gs.heroes.isEmpty()){
				ctrl.onNewClick(new Click(x,y,PlayerID.CLIENT));
				return;
			}
			
			if(gui_gs.get_winner()!=null){
				return;
			}
			
			if(click!=null){				
				if(am_i_the_server()){
					//System.out.println(" CLICKED ON SERVER ");
					click.onNewClick(new Click(x, y, PlayerID.SERVER));	
				}
				else{
					//System.out.println(" CLICKED ON CLIENT ");	
					click.onNewClick(new Click(x, y, PlayerID.CLIENT));				
				}			
			}
		}
	}
	
	/**
	 * A f�ggv�ny megvizsg�lja, hogy az adott j�tkos a szerver-e
	 * @return logikai �rt�k, hogy az adott j�t�kos-e a szerver
	 */
	private boolean am_i_the_server(){
		return click==ctrl;
	}
	
	/**
	 * �jram�tretez�s eset�n kisz�m�tja az ablak m�ret�t
	 */
	private void calculate_window_sizes(){
		int field_x =  (getWidth()-2*WINDOW_BORDER_OFFSET)/(TABLE_SIZE_X+1);
		int field_y =  (getHeight()-WINDOW_BORDER_OFFSET-MENUBAR_OFFSET)/(TABLE_SIZE_Y+1);
		//System.out.println("CALC FIELD - X:" + field_x + " Y:" + field_y);
		if(field_x<field_y){
			FIELD_SIZE=field_x;
		}
		else{
			FIELD_SIZE=field_y;			
		}
		BOARD_OFFSET=FIELD_SIZE/2;
		BOARD_HEIGHT=FIELD_SIZE*TABLE_SIZE_Y;
		BOARD_WIDTH=FIELD_SIZE*TABLE_SIZE_X;
		WINDOW_HEIGHT=getHeight();
		WINDOW_WIDTH=getWidth();
	}
	
	/**
	 * A f�ggv�ny �jrarajzolja a j�t�kteret a jelenlei �llapotnak megfelel�en
	 * @param gs a j�t�k jelenlegi �llapot�t t�rolja
	 */
	@Override
	public void onNewGameState(GameState gs) {
		// TODO Auto-generated method stub
		gui_gs.copy(gs);
		gamePanel.repaint();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("RESIZED WINDOW - X:" + getWidth() + " Y:" + getHeight());
		calculate_window_sizes();
		update_game_panel_bounds();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}
