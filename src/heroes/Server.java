package heroes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;

/**
 * Szerver h�l�zati interf�sz.
 * Network oszt�lyb�l �r�kl�dik, emellett implement�lja az IGameState interf�szt.
 * Egyszerre k�pes gamestate-ek h�l�zaton t�rt�n� k�ld�s�re �s fogad�s�ra.
 * 
 * A szervert l�trehozza egy k�l�n sz�lban. Server socketet nyit a megadott porton,
 *  majd v�rakozik a kliens csatlakoz�s�ra. Abban az esetben ha megszakadna a kapcsolat
 *  �jra v�rakoz� �llapotba ker�l �s ek�zben a server oldali GUI-t blokkolja a <code> Network GUI_Blocker</code>
 *  oszt�ly seg�ts�g�vel. A kapcsolat fel�p�t�se ut�n fogadja a be�rkez� 
 *  adatokat, amit Click objektumokk� alak�t,�s tov�bbk�ldi az IClick interf�szen kereszt�l a Control fel�.
 *  A j�t�k�llapot (<code> GameState </code>) objektumokat az IGameState interf�szen kereszt�l
 *  k�ldi tov�bb a kliensnek.
 *  
 * 
 * @author Misi
 * 
 */
public class Server extends Network implements IGameState{
	
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	/**
	 * A kliens Server k�z�tti kapcsolat megszak�t�s�t jelz� flag, igaz �rt�kre �ll�t�s eset�n
	 *  megszak�tja a kapcsolatott �s le�ll�tja a sz�lat.
	 */
	private boolean exit_flag;
	private ObjectOutputStream out = null;
	/**
	 * P�rhuzamos hozz�f�r�sek elleni v�delmet valos�tja meg.
	 */
	private ReentrantLock lock = null;
	
	private ObjectInputStream in = null;
	
	private Control clickInterface;
	private GUI gui;
	/**
	 * A szervert egy k�l�n sz�lba helyez� �s ott m�k�dtet� <code> Runnable </code> objektum.
	 */
	private ListenerWorker worker;
	/**
	 * A worker-t futtat� sz�l.
	 */
	private Thread thread;
	
	/**
	 * Konstruktor
	 * @param ci A szerver oldali Control.
	 * @param g A szerver oldali GUI. (A csatlakoz�s k�zbeni blokkol�s miatt sz�ks�ges.)
	 */
	Server(Control ci, GUI g)
	{
		worker = new ListenerWorker();
		clickInterface = ci;
		this.gui = g;
		lock = new ReentrantLock();
	}
	/**
	 * Stream-ek bez�r�sa
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
				if (clientSocket != null){
					clientSocket.close();
					clientSocket = null;
				}
			} catch (IOException ex){
				System.err.println("Error while closing socket.");
			}
			lock.unlock();
	}
	
	 /**
	  * Szerver ind�t�s. Megl�v� kapcsolatok bez�r�sa, majd worker sz�l k�sz�t�se.
	  *	A szervert l�trehozza egy k�l�n sz�lban. V�gtelen ciklusban v�rakozik a kliensre.
	  *  Miut�n fel�p�tett�k a kapcsolatot, a bej�v� adatokat fogadja, az esetleges sz�tkapcsol�sok ut�n �jra
	  *  visszat�r a v�gtelen ciklusba �s v�rakozik egy esetleges csatlakoz�sra.
	  */
	 @Override
	void start(String ip) {
		stop();
		exit_flag=false;
		thread = new Thread(worker);
		thread.start();
	}
	
	/**
	 * Szerver le�ll�t�s.
	 * <code> exit_flag=true</code> be�ll�t�s�val, illetve a szerver kliens socket bez�r�s�val
	 * egy kiv�tel dob�st gener�l, ami seg�ts�g�vel kil�p a v�rakoz� �llapotb�l.
	 * V�g�l megsemm�siti a worker sz�lat.
	 */
	@Override
	void stop() {
		lock.lock();
		exit_flag=true;
		try{
			if(serverSocket != null){
				serverSocket.close();
				serverSocket = null;
			}
		} catch (Exception ex){
			System.out.println("Cannot close server socket");
		}
		try{
			if(clientSocket != null){
				clientSocket.close();
				clientSocket = null;
			}
		} catch (Exception ex) {
			System.out.println("Cannot close client socket");
		}
		lock.unlock();
		
		
		try {
			if(thread!=null)
				thread.join();
		} catch (InterruptedException e) {
			System.out.println("Cannot stop the worker thread.");
		}
	}
	
	/**
	 *	A szervert l�trehozza egy k�l�n sz�lban. V�gtelen ciklusban v�rakozik a kliensre.
	  *  Miut�n fel�p�tett�k a kapcsolatot, a bej�v� adatokat fogadja, az esetleges sz�tkapcsol�sok ut�n �jra
	  *  visszat�r a v�gtelen ciklusba �s v�rakozik egy esetleges csatlakoz�sra.
	 *
	 *@author Misi
	 */
        private class ListenerWorker implements Runnable {
		public void run() {
			// Create server socket
			ServerSocket ss = null;
			while(ss==null)
				try{
					ss =  new ServerSocket(10007);
				}catch(IOException ex){
					System.out.println("Failed to create server socket");
					JOptionPane.showMessageDialog(gui, "Cannot create server socket on port 10007.","Fatal error",JOptionPane.ERROR_MESSAGE);
					System.exit(-1);
				}
			lock.lock();
			serverSocket = ss;
			lock.unlock();

			// Start listening
			while(true)
			{
				boolean f = true;
				Socket cs=null;
				ObjectOutputStream os=null;
				// Waiting for clients
				while(cs==null)
				{

					lock.lock();
					f = exit_flag;
					lock.unlock();
					if(f==true) return;
					
					GUI_Blocker blocker = null;
					try{
						// Block main window
						blocker = new GUI_Blocker(gui);
						cs = serverSocket.accept();
						// Release blocking
						blocker.stop();
						try{
							os = new ObjectOutputStream(cs.getOutputStream());
							in = new ObjectInputStream(cs.getInputStream());
							os.flush();
						} catch(IOException ex){
							cs.close();
							cs = null;
						}
					} catch (Exception ex) {
						if(blocker != null) blocker.stop();
					}
				}

				// share objects
				lock.lock();
				try{
					f = exit_flag;
					if(f){
						os.close();
						in.close();
						cs.close();
					} else {
						clientSocket = cs;
						out = os;
					}
				}catch (IOException ex){
					System.out.println("Close error");
				} finally{
				lock.unlock();
				}
				if(f) return;
				
				// CONNECTION ESTABLISHED
				System.out.println("Client connected.");
				clickInterface.startScheduler();
				// COMMUNICATING	
				try {
					while (true) {
						Click c = (Click) in.readObject();
						if(c instanceof Click)
							clickInterface.onNewClick(c);
					}
				} catch (Exception ex) {
					System.out.println("Disconnected");
				} finally {
					cleanup();
				}
			} // while
		}//run
	}// worker
	
        /**
         * A server �ltal k�ld�tt j�t�k�llapotok k�ld�s�t val�s�tja meg a kliens fel�.
         */
	public void onNewGameState(GameState gs)
	{
		lock.lock();
		try {
			if(out != null)
			{
				out.reset();
				out.writeObject(gs);
				out.flush();
				
			}
		} catch (IOException ex) {
			System.err.println("Send error.");
		} finally {
			lock.unlock();
			
		}
	}
}
