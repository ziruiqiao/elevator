package elevator.elevator;

import elevator.elevator.states.*;
import elevator.util.StateSingleton;

public class ElevatorStateSingleton extends StateSingleton<ElevatorState> {
    private final Elevator elevator;
    public ElevatorStateSingleton(Elevator ev){
        elevator = ev;
        createStates();
    }
    private void createStates(){
        addState(new AtADestinationFloorState(elevator));
        addState(new MovingState(elevator));
        addState(new NearFloorState(elevator));
        addState(new IdleState(elevator));
        addState(new DoorClosedState(elevator));
        addState(new PrepareToLeaveState(elevator));
    }

}
