package elevator.elevator.states;
/**
 * 
 */

import elevator.elevator.Elevator;
import elevator.elevator.ElevatorState;
import elevator.communication.messages.*;

/**
 * @author Zirui and Callum
 *
 */
public class AtADestinationFloorState extends ElevatorState {

	public AtADestinationFloorState(Elevator e) {
		super(e);
	}
	
	public void entry()
	{
		e.notifyScheduler(SchedulerEventTypes.OnElevatorStop);

	}
	
	public void openDoorCommand(boolean b) {
		e.openDoor();
		e.changeState(e.getStates().getState(IdleState.class));		
	}
}
