package elevator.scheduler.states;

import elevator.communication.messages.ElevatorEventTypes;
import elevator.scheduler.Scheduler;
import elevator.scheduler.SchedulerState;

public class Moving extends SchedulerState {
    public Moving(Scheduler es){
        super(es);
    }

    @Override
    public void OnFloorApproach(int f) {
        scheduler.getElevatorQueue().moveToFloor(f);
        System.out.println("(scheduler) Notified that floor " + f + " is being approached");

        if (scheduler.getElevatorQueue().shouldStop()) {
        	scheduler.changeState(scheduler.getAllStates().getState(PendingStop.class));
        	scheduler.sendToElevator(ElevatorEventTypes.AtDestinationFloor);
        } else {
            scheduler.changeState(scheduler.getAllStates().getState(Moving.class));
            scheduler.sendToElevator(ElevatorEventTypes.ContinueMoving);
        }
    }
}
