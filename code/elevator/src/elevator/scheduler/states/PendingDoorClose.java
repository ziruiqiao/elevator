package elevator.scheduler.states;

import elevator.Floor.PassengerRequest;
import elevator.communication.messages.ElevatorEventTypes;
import elevator.scheduler.Scheduler;
import elevator.scheduler.SchedulerState;
import elevator.util.Serializer;
import elevator.util.Sleep;

import java.util.ArrayList;

public class PendingDoorClose extends SchedulerState  {
    public PendingDoorClose(Scheduler es){
        super(es);
    }
    SchedulerState previous;
    @Override
    public void OnEntry(SchedulerState ss) {
        previous = ss;
        try {
        	//Only get requests at a floor if we're moving in right direction
        	if(scheduler.getElevatorQueue().requestsInServicingDirection()) {
        		System.out.println("(scheduler) Waiting for passengers to board");
                Sleep.EnvironmentSleep(2500);
                ArrayList<PassengerRequest> events = scheduler.getElevatorQueue().getDestinationRequestList();
            	scheduler.sendToElevator(ElevatorEventTypes.FloorButtonPressed,
                                Serializer.toByteArray(events));
                Sleep.EnvironmentSleep(2500);
        	} else {
        		System.out.println("(scheduler) Issued command to close door");
                scheduler.sendToElevator(ElevatorEventTypes.DoorCloseCommand);
        	}
    	} catch(Exception e) {}
        
        
    }

    public void OnDestinationAdded(ArrayList<PassengerRequest> events){
    	scheduler.getElevatorQueue().handleNewButtonPressed(events);
    	System.out.println("(scheduler) Issued command to close door");
    	scheduler.sendToElevator(ElevatorEventTypes.DoorCloseCommand);
    }

    @Override
    public void OnDoorClose() {
        scheduler.getElevatorQueue().floorVisitedInDirection();
        scheduler.changeState(scheduler.getAllStates().getState(Moving.class));
        scheduler.sendToElevator(ElevatorEventTypes.MoveMotorCommand,
                        Serializer.toByteArray(scheduler.getElevatorQueue().calculateMoveDirection()));
    }
}
