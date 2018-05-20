/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package heroes;

/**
 *
 * @author ABence
 */
public class Heroes_Main {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		Control c = new Control();
		GUI g = new GUI(c);
		c.setGUI(g);
	}

}
