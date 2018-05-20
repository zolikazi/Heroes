package heroes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Kliens h�l�zati interf�sz.
 * Network oszt�lyb�l �r�kl�dik, emellett implement�lja az IClick interf�szt.
 * Egyszerre k�pes gamestate-ek h�l�zaton t�rt�n� k�ld�s�re �s fogad�s�ra.
 * 
 *  A klienst l�trehozza egy k�l�n sz�lban. A kliens v�gtelen ciklusban pr�b�l csatlakozni a szerverhez.
 *  Miut�n fel�p�tett�k a kapcsolatot, a bej�v� adatokb�l GameState objektumot gener�l, majd 
 *  tov�bb�tja a kliens GUI fel�. Csatlakoz�s alatt a GUI_Blockerrel blokkolja a kliens GUI-t.
 *  Ha az exit_flag �rt�ke <code>true</code>-ra v�ltozik, akkor le�ll.
 *
 * @author Misi
 */
public class Client extends Network implements IClick{
	
	private Socket socket = null;
	/**
	 * A kliens Server k�z�tti kapcsolat megszak�t�s�t jelz� flag, <code>true</code> �rt�kre �ll�t�s eset�n
	 * megszak�tja a kapcsolatot �s le�ll�tja a sz�lat.
	 */
	private boolean exit_flag = false;
	private ObjectOutputStream out = null;
	/**
	 * P�rhuzamos hozz�f�r�sek elleni v�delmet valos�tja meg.
	 */
	private ReentrantLock lock = null;
	
	private ObjectInputStream in = null;
	/**
	 * Szerver ip c�me
	 */
	private String ip = "localhost";//bear
	/**
	 * Kliens oldali GUI.
	 */
	private GUI gui;

	/**
	 * A klienst egy k�l�n sz�lba helyez� �s ott m�k�dtet� <code> Runnable </code> objektum.
	 */
	private ListenerWorker worker;
	/**
	 * A worker-t futtat� sz�l.
	 */
	private Thread thread;
	
	/**
	 * Konstruktor
	 * @param gui Kliens oldali GUI.
	 */
	Client(GUI gui)
	{
		//gsInterface = g;
		this.gui = gui;
		gui.setClick(this);
		worker = new ListenerWorker();
		lock = new ReentrantLock();
	}

	/**
	 * Socketek, streamek felszabad�t�sa
	 */
	private void cleanup()
	{
		lock.lock();
		try{
			if (out != null){
				out.close();
				out = null;
			}
		} catch (IOException ex){
			System.err.println("Error while closing out.");
		}
		try{
			if (in != null){
				in.close();
				in = null;
			}
		} catch (IOException ex){
			System.err.println("Error while closing in.");
		}
		try{
			if (socket != null){
				socket.close();
				socket = null;
			}
		} catch (IOException ex){
			System.err.println("Error while closing socket.");
		}
		lock.unlock();
	}
	/** 
	 *  Client ind�t�s. Megl�v� kapcsolatok bez�rr�sa, majd worker sz�l k�sz�t�se.
	 *  A klienst l�trehozza egy k�l�n sz�lban. V�gtelen ciklusban prob�l csatlakozni a szerverhez.
	 *  Miut�n fel�p�tett�k a kapcsolatot, a bej�v� adatokb�l GameState objektumot gener�l, majd 
	 *  tov�bb�tja a kliens GUI fel�. Csatlakoz�s alatt a GUI_Blockerrel blokkolja a kliens GUI-t.
	 *  Ha az exit_flag �rt�ke <code>true</code>-ra v�ltozik, akkor le�ll.
	 */
	public void start(String ip)
	{
		this.ip = ip;
		stop();
		exit_flag=false;
		thread = new Thread(worker);
		thread.start();
	}
	/**
	 * Client le�ll�t�s.
	 * <code> exit_flag=true</code> be�ll�t�s�val, illetve a szerver kliens socket bez�r�s�val
	 * egy kiv�tel dob�st gener�l, ami seg�ts�g�vel kil�p a v�rakoz� �llapotb�l.
	 * V�g�l megsemm�siti a worker sz�lat.
	 */
	public void stop()
	{
		lock.lock();
		try{
			exit_flag = true;
			if(socket != null)
				socket.close();
		} catch(IOException ex){
			System.out.println("Cannot close socket");
		}finally{
			lock.unlock();
		}
		try{
		if(thread!=null)
		{
			thread.join();
			thread=null;
		}
		} catch(InterruptedException ie){
			System.out.println("Join interrupted");
		}

	}
	/**
	 *  A klienst l�trehozza egy k�l�n sz�lban. V�gtelen ciklusban prob�l csatlakozni a szerverhez.
	 *  Miut�n fel�p�tett�k a kapcsolatot, a bej�v� adatokb�l GameState objektumot gener�l, majd 
	 *  tov�bb�tja a kliens GUI fel�. Csatlakoz�s alatt a GUI_Blockerrel blokkolja a kliens GUI-t.
	 *  Ha az exit_flag �rt�ke <code>true</code>-ra v�ltozik, akkor le�ll. 
	 * @author Misi
	 *
	 */
	private class ListenerWorker implements Runnable {
		public void run() {
			while(true){
				boolean f= true;
				Socket s = null;
				ObjectOutputStream o=null;
				// Connecting
				
				lock.lock();
				f = exit_flag;
				lock.unlock();
				if(f) {
					return;
				}
				
				GUI_Blocker blocker = null;
				// Block main window
				blocker = new GUI_Blocker(gui);
				while(s == null)
				{

					try{
						lock.lock();
						f = exit_flag;
						lock.unlock();
						if(f) {
							blocker.stop();
							return;
						}
						
						s = new Socket(ip,10007);
						try{
							o = new ObjectOutputStream(s.getOutputStream());
							o.flush();
							in = new ObjectInputStream(s.getInputStream());
						} catch(IOException ex){
							s.close();
							s = null;
						}
					} catch (UnknownHostException he){
						System.out.println("Cannot reach host");
					} catch (IOException ie) {
						System.out.println("Connection error");
					} 
				}
				if(blocker!=null) blocker.stop();
				// share objects
				lock.lock();
				try{
					f = exit_flag;
					if(f){
						o.close();
						in.close();
						s.close();
					} else {
						socket = s;
						out = o;
					}
				}catch (IOException ex){
					System.out.println("Close error");
				} finally{
				lock.unlock();
				}
				if(f) return;
	
				// CONNECTION ESTABLISHED
				System.out.println("Connected to server.");

				try {
					while (true) {
						GameState gs = (GameState) in.readObject();
						if(gs instanceof GameState)
							gui.onNewGameState(gs);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("Disconnected");
				} finally {
					cleanup();
				}
			}// while
		} // run
	} //
	
	/**
	 * Command objektumok k�ld�s�re szolg�l a szerver fel�.
	 */
	public void onNewClick(Click c)
	{
		lock.lock();
		try {
			if(out != null){
				out.writeObject(c);
				out.flush();
			}
		} catch (IOException ex) {
			System.out.println("Send error");
		} finally {
			lock.unlock();
		}

	}

}
