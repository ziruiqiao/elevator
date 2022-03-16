/**
 * 
 */
package elevator.elevator.states;

import elevator.elevator.Elevator;
import elevator.elevator.ElevatorState;
import elevator.Floor.PassengerRequest;
import elevator.communication.messages.*;
import elevator.util.Serializer;

import java.util.ArrayList;

/**
 * @author Callum
 *
 */
public class IdleState extends ElevatorState {
	public IdleState(Elevator e) {
		super(e);
	}
	
	public void floorButtonPressed(ArrayList<PassengerRequest> events) {
		for (PassengerRequest ent: events ) {
			e.turnLampOn(ent.getElevatorNumber());
		}
		remainingFloors++;
		e.changeState(e.getStates().getState(PrepareToLeaveState.class));

		e.notifyScheduler(SchedulerEventTypes.OnDestinationSelected, 
				 Serializer.toByteArray(events));
	}

	public void doorCloseCommand() {
		e.closeDoor();
		e.changeState(e.getStates().getState(DoorClosedState.class));
	}
	
}
