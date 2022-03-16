package elevator.scheduler;

import elevator.Floor.PassengerRequest;
import elevator.communication.Distributor;
import elevator.communication.messages.ElevatorEvent;
import elevator.communication.messages.ElevatorEventTypes;
import elevator.communication.messages.SchedulerEvent;
import elevator.communication.messages.SystemComponent;
import elevator.scheduler.states.Idle;
import elevator.util.Serializer;

import java.util.ArrayList;

public class Scheduler implements Runnable {
    public static final int MAX_FLOORS = 60;
    private SchedulerState s;
    private SchedulerCommunication schedulerCommunication;
    private final SchedulerStateSingleton states;
    private final Distributor distributor;
    private final ElevatorQueue eq;
    private final int elevatorId;

    public Scheduler(int id, Distributor d, SchedulerCommunication sc){
        distributor = d;
        elevatorId = id;
        schedulerCommunication = sc;
        schedulerCommunication.addNewQueue(elevatorId);
        
        eq = new ElevatorQueue();
        states = new SchedulerStateSingleton(this);
        changeState(states.getState(Idle.class));
    }

    /**
     * Send an event to the distributor to send over the network
     */
    public void sendToElevator(ElevatorEventTypes cmd, byte... data) {
        ElevatorEvent e = new ElevatorEvent(SystemComponent.SCHEDULER, elevatorId, elevatorId, cmd, data);
        distributor.sendToElevator(e);
    }
        
    public void sendDistanceMessage(int dist, int requestId) {
    	ElevatorEvent e = new ElevatorEvent(SystemComponent.SCHEDULER, 
    										elevatorId, 
    										requestId, 
    										ElevatorEventTypes.DistanceDetermined, 
    										Serializer.toByteArray(dist));
    	schedulerCommunication.addDistanceMessage(e);
    }
    
    /**
     * Getter for Elevator queue that determines how Elevator services floors
     * @return ElevatorQueue object
     */
    public ElevatorQueue getElevatorQueue(){ return eq; }
    
    /**
     * Getter for state singleton containing all state
     * @return SchedulerStateSingleton object
     */
    public SchedulerStateSingleton getAllStates(){
        return states;
    }
    
    /**
     * Getter for current state of Scheduler
     * @return State object
     */
    SchedulerState getCurrentState(){
        return s;
    }
    
    /**
     * Change state from old to new
     * @param ss new State scheduler should be in
     */
    public void changeState(SchedulerState ss){
        System.out.println();
        SchedulerState old = null;
        if (s != null){
            old = s;
            old.OnExit(ss);
            //System.out.printf("(scheduler) FSM: %s -> %s\n", old.getClass().getSimpleName(), ss.getClass().getSimpleName());
        } else {
            //System.out.printf("(scheduler) FSM:  -> %s\n", ss.getClass().getSimpleName());
        }
        s = ss;
        s.OnEntry(old);
    }
    
    @Override
    public void run() {
    	SchedulerEvent e;
        while(true) {
        	e = schedulerCommunication.getIncoming(elevatorId);
        	switch(e.getCommand()) {
				case OnDestinationSelected:
                    s.OnDestinationAdded(Serializer.fromByteArray(e.getData(), ArrayList.class));
					break;
				case OnDoorClose:
					s.OnDoorClose();
					break;
				case OnElevatorStop:
					s.OnElevatorStop();
					break;
				case OnFloorApproach:
                    if (e.getData().length > 0)	s.OnFloorApproach(e.getData()[0]);
                    else System.err.println("OnFloorApproach msg sent with no floor in data position 1");
					break;
				case OnFloorRequest:
                    s.OnRequestAdded(Serializer.fromByteArray(e.getData(), PassengerRequest.class));
					break;
				case DetermineDistance:
					PassengerRequest floorOp = Serializer.fromByteArray(e.getData(), PassengerRequest.class);
					int distance = eq.determineDistance(floorOp.getFloor(), floorOp.getDirectionalButton());
					sendDistanceMessage(distance, e.getSrcId());
					break;
				default:
                    System.err.println("Unhandled Scheduler event " + e.getCommand());
					break;
	        	
        	}
        }
    }
}
