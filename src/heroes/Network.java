package heroes;

import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import heroes.GUI;
/**
 * Absztrakt osztály a hálózatot kezelõ osztályok számára.
 * @author Misi
 *
 */
abstract class Network {
	/**
	 * 
	 * A hálózati interfész  <code> start </code> függvény meghívásától <code> stop </code> 
	 * függvény meghívásáig folyamatosan probál csatlakozni a másik hálozati elemhez
	 * 
	 * @param ip A cél IP cím.
	 */
	abstract void start(String ip);
	/**
	 * A hálózati interfész  <code> start </code> függvény meghívásától <code> stop </code> 
	 * függvény meghívásáig folyamatosan probál csatlakozni a másik hálozati elemhez
	 */
	abstract void stop();	
	
	/**
	 * Az osztály a szerver és a kliens GUI-ját blokkolja a csatlakozás ideje alatt.
	 * A blokkolást a <code> stop </code> függvény segítségével oldja fel.
	 * 
	 * @author Misi
	 *
	 */
	protected class GUI_Blocker implements Runnable{
		private JDialog d;
		
		/**
		 * A létrejövõ dialogus ablak a paraméterben megadott GUI objektumot blokkolja,
		 *  ezenfelül tartalmaz egy gombot a program leállítására.
		 * 
		 * @param g  {@link GUI} objektum
		 */
		public GUI_Blocker(GUI g) {
			
			JPanel pan = new JPanel(new GridLayout(2, 1));

			JButton button = new JButton("Finish");
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					stop();
					System.out.println("Program is closed by the user.");
					System.exit(-1);
				}
			});
			
		//	pan.add(label);
			pan.add(button);

			d = new JDialog(g, "Waiting", Dialog.ModalityType.DOCUMENT_MODAL);
			d.add(pan);
			d.pack();
			d.setLocationRelativeTo(g);
			Thread blocker = new Thread(this);
			blocker.start();			
		}
		public void run() {
			d.setVisible(true);
			System.out.println("Ready");
		}
		
		/**
		 * Dialogus ablakok bezárására szolgáló függvény.
		 */
		public void stop() {
			d.dispose();
			 d.setVisible(false);
             d.dispatchEvent(new WindowEvent(d, WindowEvent.WINDOW_CLOSING));
		}
	}
	
}
