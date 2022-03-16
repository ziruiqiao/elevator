/**
 * 
 */
package elevator.scheduler;
import java.util.*;
import elevator.communication.messages.ElevatorEvent;
import elevator.communication.messages.SchedulerEvent;
import elevator.communication.messages.SchedulerEventTypes;
import elevator.communication.messages.SystemComponent;
import elevator.util.Serializer;
/**
 * @author Callum
 *
 */
public class SuperScheduler_DetermineDirection implements Runnable{
	private SchedulerEvent floorRequest;
	private ArrayList<Integer> elevatorIds;
	private SchedulerCommunication sc;
	private int requestId;
	
	public SuperScheduler_DetermineDirection(SchedulerEvent floorRequest, ArrayList<Integer> ids, SchedulerCommunication sc, int requestId) {
		this.floorRequest = floorRequest;
		this.elevatorIds = ids;
		this.sc = sc;
		this.requestId = requestId;
	}
	
	public void run() {
		//Get distances from all the schedulers 
		ArrayList<ElevatorEvent> responses = new ArrayList<ElevatorEvent>();
		for(int id: elevatorIds) {
			responses.add(sc.getElevatorDistance(id, requestId));
		}
		
		
		int elevatorId_min = 1;
		int minDistance    = Integer.MAX_VALUE;
		
		//Find minimum distance 
		for(ElevatorEvent response : responses) {
			int distance = Serializer.fromByteArray(response.getData(), Integer.class);
			if(distance < minDistance) {
				elevatorId_min = response.getSrcId();
				minDistance = distance;
			}
		}
		
		//Make new SchedulerEvent to send to Elevator
		SchedulerEvent toDestinationScheduler = new SchedulerEvent(SystemComponent.FLOOR, 
																floorRequest.getSrcId(), 
																elevatorId_min, 
																SchedulerEventTypes.OnFloorRequest, 
																floorRequest.getData());
		
		//Send original floor request to scheduler corresponding to elevator that should serve
		sc.addIncomingSchedulerEvent(toDestinationScheduler);
	}

}
