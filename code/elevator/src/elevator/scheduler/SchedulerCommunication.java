/**
 * 
 */
package elevator.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import elevator.communication.messages.ElevatorEvent;
import elevator.communication.messages.SchedulerEvent;

/**
 * @author Callum
 *
 */
public class SchedulerCommunication {
	private HashMap<Integer,LinkedList<SchedulerEvent>> incomingMessages;
	private HashMap<Integer,LinkedList<ElevatorEvent>>  distanceMessages;
	
	public SchedulerCommunication() {
		incomingMessages = new HashMap<Integer, LinkedList<SchedulerEvent>>();
		distanceMessages = new HashMap<Integer, LinkedList<ElevatorEvent>>();
	}
		
	public synchronized void addIncomingSchedulerEvent(SchedulerEvent s) {
		incomingMessages.get(s.getDestId()).addLast(s);
		notifyAll();
		return;
	}
	
	public synchronized SchedulerEvent getIncoming(int ID) {
		while(incomingMessages.get(ID).size() == 0) {
			try {
				wait();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return incomingMessages.get(ID).pop();
	}

	public synchronized void addNewQueue(int id) {
		incomingMessages.put(id, new LinkedList<SchedulerEvent>());
		distanceMessages.put(id, new LinkedList<ElevatorEvent>());
	}

	/**
	 * Check and see if requested message is present in queue
	 * @param elevatorId
	 * @param requestId
	 * @return
	 */
	private boolean containsDestId(int elevatorId, int requestId) {
		for(int i = 0; i < distanceMessages.get(elevatorId).size(); i++) {
			if(distanceMessages.get(elevatorId).get(i).getDestId() == requestId) {
				return true;
			}
		}
		return false;
	}
		
	public synchronized ElevatorEvent getElevatorDistance(int elevatorId, int requestId) {
		while((distanceMessages.get(elevatorId).size() == 0) && !containsDestId(elevatorId, requestId)) {
				try {
					wait();
				} catch(Exception e) {
					e.printStackTrace();
				}
		}
		
		for(int i = 0; i < distanceMessages.get(elevatorId).size(); i++) {
			ElevatorEvent event = distanceMessages.get(elevatorId).get(i);
			if(event.getDestId() == requestId) {
				return distanceMessages.get(elevatorId).remove(i);
			}
		}
		
		//Shouldn't get here
		return null;
	}

	public synchronized void addDistanceMessage(ElevatorEvent e) {
		distanceMessages.get(e.getSrcId()).add(e);
		notifyAll();
		return;
	}

}
