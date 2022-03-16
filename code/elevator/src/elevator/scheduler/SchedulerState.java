package elevator.scheduler;

import elevator.Floor.PassengerRequest;

import java.util.ArrayList;

public class SchedulerState {

    protected Scheduler scheduler;

    public SchedulerState (Scheduler s){
        scheduler = s;
    }

    private void printNotImplemented(){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        System.err.println("Scheduler State: " + stackTraceElements[2].getMethodName() + " not implemented while in state " + scheduler.getCurrentState().getClass().getSimpleName());

    }

    /**
     *
     * @param ss The scheduler state being left
     */
    public void OnEntry(SchedulerState ss){}

    /**
     *
     * @param ss The scheduler state being left to
     */
    public void OnExit(SchedulerState ss){}

    public void OnRequestAdded(PassengerRequest e){
        scheduler.getElevatorQueue().addFloor(e);
    }

    public void OnDoorClose(){
        printNotImplemented();
    }

    public void OnFloorApproach(int f){
        printNotImplemented();
    }

    public void OnElevatorStop(){
        printNotImplemented();
    }

    public void OnDestinationAdded(ArrayList<PassengerRequest> events){
        printNotImplemented();
    }


}
