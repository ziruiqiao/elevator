package elevator.elevator.states;

import elevator.elevator.Elevator;
import elevator.elevator.ElevatorState;
import elevator.elevator.Direction;
import elevator.communication.messages.*;

/**
 * @author Callum
 *
 */
public class DoorClosedState extends ElevatorState{
	 public DoorClosedState(Elevator e) {
		 super(e);
	 }
	 
	 public void entry() {
		 e.notifyScheduler(SchedulerEventTypes.OnDoorClose);
	 }
	 public void moveMotorCommand(Direction d) {
		e.changeState(e.getStates().getState(MovingState.class));
		e.moveMotor(d);
	 }
}
