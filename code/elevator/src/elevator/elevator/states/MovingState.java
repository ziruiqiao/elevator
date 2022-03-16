package elevator.elevator.states;

import elevator.elevator.Elevator;
import elevator.elevator.ElevatorState;
/**
 * @author Zirui and Callum
 *
 */
public class MovingState extends ElevatorState {
    public MovingState(Elevator e) {
        super(e);
    }
    
    public void arrivalSensorNearFloor() {
    	e.changeState(e.getStates().getState(NearFloorState.class));
    }
}
