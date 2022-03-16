/**
 * 
 */
package elevator.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import elevator.communication.Distributor;
import elevator.communication.messages.ElevatorEvent;
import elevator.communication.messages.ElevatorEventTypes;
import elevator.communication.messages.SchedulerEvent;
import elevator.communication.messages.SchedulerEventTypes;
import elevator.communication.messages.SystemComponent;

/**
 * @author Callum
 * Handle negotiating between different Scheduler objects.
 */
public class SuperScheduler implements Runnable {

	private Distributor distrib;
	HashMap<Integer, Scheduler> schedulers;
	private SchedulerCommunication communication;
	private int superschedulerId;
	private static int requestId = 1;
	
	public SuperScheduler(Distributor d, int id) {
		distrib = d;
		schedulers = new HashMap<Integer, Scheduler>();
		communication = new SchedulerCommunication();
		superschedulerId = id;
	}
	
	private static int getNewRequestId() {
		return SuperScheduler.requestId++;
	}
	
	/**
	 * Send requests to/from correct Scheduler
	 */
	public void run() {
		while(true) {
			//Get ALL pending requests from distributor
			LinkedList<SchedulerEvent> batchEvents = distrib.getSchedulerEventsBatch();
			
			//Handle each request separately 
			for(SchedulerEvent event : batchEvents) {
				switch(event.getCommand()) {
					case StartUp:
						//Ensure Key doesn't already exist
						if(schedulers.containsKey(event.getDestId())) {
							break;
						}
						
						//Make new Scheduler object + Thread
						Scheduler s = new Scheduler(event.getDestId(), distrib, communication);
						Thread SchedulerThread = new Thread(s,"Scheduler: "+event.getDestId());
						SchedulerThread.start();
						
						//Add it to list of schedulers and acknowledge creation
						schedulers.put(event.getDestId(), s);
						break;
					case OnFloorRequest:
						//Get new data required for handler thread
						int reqId = SuperScheduler.getNewRequestId();
						
						//Send 'DetermineDistance' request to all elevators
						for(int elevatorId : schedulers.keySet()) {
							communication.addIncomingSchedulerEvent(new SchedulerEvent(SystemComponent.SUPERSCHEDULER, reqId, elevatorId, SchedulerEventTypes.DetermineDistance, event.getData()));
						}
						
						ArrayList<Integer> schedulerIds = new ArrayList<Integer>(schedulers.keySet());
						SuperScheduler_DetermineDirection requestHandler = new SuperScheduler_DetermineDirection(event, 
																											schedulerIds,
																											communication,
																											reqId);
						//Start new thread to handle data
						Thread handleRequest = new Thread(requestHandler);
						handleRequest.start();
						break;
					default:
						//Everything else just send it to elevator
						communication.addIncomingSchedulerEvent(event);
						break;
				}
			}
		}
	}

}
