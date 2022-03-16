/**
 * 
 */
package elevator.scheduler;

import elevator.communication.Distributor;

/**
 * @author Callum
 * Launch point for scheduler application
 */
public class SchedulerLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Distributor d = new Distributor();
		SuperScheduler ss = new SuperScheduler(d, 1);
		Thread superschedulerThread = new Thread(ss);
		superschedulerThread.start();
	}

}
