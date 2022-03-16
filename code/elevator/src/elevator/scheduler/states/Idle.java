package elevator.scheduler.states;

import elevator.Floor.PassengerRequest;
import elevator.scheduler.Scheduler;
import elevator.scheduler.SchedulerState;

public class Idle extends SchedulerState {
    public Idle(Scheduler es){
        super(es);
    }

    public void OnEntry(SchedulerState ss){
    	scheduler.getElevatorQueue().resetElevator();
    }
    
    @Override
    public void OnRequestAdded(PassengerRequest e) {
        super.OnRequestAdded(e);
        scheduler.getElevatorQueue().calculateServicingDirection();
        scheduler.changeState(scheduler.getAllStates().getState(PendingDoorClose.class));
    }

}
