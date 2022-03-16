/**
 * 
 */
package elevator.elevator.states;

import elevator.elevator.Elevator;
import elevator.elevator.ElevatorState;

/**
 * @author Callum
 *
 */
public class PrepareToLeaveState extends ElevatorState {
    public PrepareToLeaveState(Elevator e) {
        super(e);
    }
    
    public void doorCloseCommand() {
    	e.closeDoor();
    	e.changeState(e.getStates().getState(DoorClosedState.class));
	}
}
