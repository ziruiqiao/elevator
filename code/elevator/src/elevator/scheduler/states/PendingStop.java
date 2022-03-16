package elevator.scheduler.states;

import elevator.communication.messages.ElevatorEventTypes;
import elevator.scheduler.Scheduler;
import elevator.scheduler.SchedulerState;

public class PendingStop extends SchedulerState  {
    public PendingStop(Scheduler es){
        super(es);
    }
    
    public void OnExit(SchedulerState s){
        System.out.println("(scheduler) Issue command to open doors on floor " + scheduler.getElevatorQueue().getCurrentFloor());
        scheduler.sendToElevator(ElevatorEventTypes.OpenDoorCommand,
                        (byte) (scheduler.getElevatorQueue().floorsInEitherDirection() ? 1 : 0));
    }

    @Override
    public void OnElevatorStop() {        
        //Check if more requests in current direction
        if(scheduler.getElevatorQueue().outOutRequests()) {
        	scheduler.getElevatorQueue().floorVisitedInDirection();
        	//No requests in other direction
        	if(scheduler.getElevatorQueue().shouldIdle()) { 
        		scheduler.changeState(scheduler.getAllStates().getState(Idle.class));
        	} 
        	//Flip active service direction
        	else {
        		scheduler.getElevatorQueue().determineNewService();
        		scheduler.changeState(scheduler.getAllStates().getState(PendingDoorClose.class));
        	}
        } 
        else {
        	scheduler.changeState(scheduler.getAllStates().getState(PendingDoorClose.class));
        }
    }
}
