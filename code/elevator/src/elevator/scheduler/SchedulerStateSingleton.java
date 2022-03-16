package elevator.scheduler;

import elevator.scheduler.states.Idle;
import elevator.scheduler.states.Moving;
import elevator.scheduler.states.PendingDoorClose;
import elevator.scheduler.states.PendingStop;
import elevator.util.StateSingleton;

public class SchedulerStateSingleton extends StateSingleton<SchedulerState> {
    private final Scheduler scheduler;
    public SchedulerStateSingleton(Scheduler es){
        scheduler = es;
        createStates();
    }
    private void createStates(){
        addState(new Idle(scheduler));
        addState(new PendingDoorClose(scheduler));
        addState(new Moving(scheduler));
        addState(new PendingStop(scheduler));
    }
}