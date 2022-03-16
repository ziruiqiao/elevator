package elevator.elevator.states;

import elevator.elevator.Elevator;
import elevator.elevator.ElevatorState;
import elevator.communication.messages.*;

/**
 * @author Zirui and Callum
 *
 */
public class NearFloorState extends ElevatorState {
    public NearFloorState(Elevator e) {
        super(e);
    }

    public void entry() {
    	e.notifyScheduler(SchedulerEventTypes.OnFloorApproach,(byte)e.getCurrentFloor().getFloorNum());
    }
    
    public void continueMoving() {
    	e.changeState(e.getStates().getState(MovingState.class));
        e.continueMoving();
    }

    public void atDestinationFloor() {
        e.stopMotor();
        e.turnLampOff(e.getCurrentFloor().getFloorNum());
        remainingFloors--;
        e.changeState(e.getStates().getState(AtADestinationFloorState.class));
    }
}
