package elevator.elevator;
import elevator.Floor.PassengerRequest;

import java.util.ArrayList;
/**
 * @author Callum and Zirui
 *
 */
public abstract class ElevatorState {

	protected Elevator e;
	protected int remainingFloors;

	public ElevatorState(Elevator e) {
		this.e = e;
		remainingFloors = 0;
	}

	public void entry() {
	}
	
	public void exit() {
	}

	private void printNotImplemented(){
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		System.err.println("Elevator State: " + stackTraceElements[2].getMethodName() + " not implemented while in state " + e.getState().getClass().getSimpleName());
	}

	public void doorCloseCommand() {
		printNotImplemented();
	}
	
	public void moveMotorCommand(Direction d) {
		printNotImplemented();
	}
	
	public void floorButtonPressed(ArrayList<PassengerRequest> event) {
		printNotImplemented();
	}
		
	public void arrivalSensorNearFloor() {
		printNotImplemented();
	}
	
	public void continueMoving() {
		printNotImplemented();
	}
	
	public void atDestinationFloor() {
		printNotImplemented();
	}
	
	public void openDoorCommand(boolean b) {
		printNotImplemented();
	}

}
