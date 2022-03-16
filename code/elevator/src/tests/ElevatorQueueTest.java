package tests;

import elevator.Floor.PassengerRequest;
import elevator.elevator.Direction;
import elevator.scheduler.ElevatorQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Clark Bains
 *
 */

class ElevatorQueueTest {

    ElevatorQueue eq = new ElevatorQueue();

    static PassengerRequest generateEvent(int from, int to){
        String dir = "up";
        if (from > to) dir = "down";
        return new PassengerRequest(from, to, 1, dir);
    }

    @BeforeEach
    void setUp() {
        eq = new ElevatorQueue();
    }


    @Test
    void initialElevatorState() {
        assertEquals(0, eq.getCurrentFloor());
        assertEquals(Direction.STOPPED, eq.getDirection());
    }

    @Test
    void movesUp(){
        addFloor(generateEvent(1, 2));
        verifyFloorStopped(Direction.UP, 1, 1);
        verifyFloorStopped(Direction.UP, 2, 0);

    }

    @Test
    void movesDown(){

        addFloor(generateEvent(0, 5));
        handleInitial(1);
        verifyFloorPassed(Direction.UP, 1);
        verifyFloorPassed(Direction.UP, 2);
        verifyFloorPassed(Direction.UP, 3);
        verifyFloorPassed(Direction.UP, 4);
        eq.addFloor(generateEvent(4, 2));
        verifyFloorStopped(Direction.UP, 5, 0);

        verifyFloorStopped(Direction.DOWN, 4, 1);
        verifyFloorPassed(Direction.DOWN, 3);
        verifyFloorStopped(Direction.DOWN, 2, 0);

    }

    @Test
    void stopsUp()  {
        addFloor(generateEvent(1, 2));
        verifyFloorStopped(Direction.UP, 1, 1);
        verifyFloorStopped(Direction.UP, 2,0);
    }

    @Test
    void stopsUpMultiple(){

        addFloor(generateEvent(1, 2));
        addFloor(generateEvent(2, 4));
        addFloor(generateEvent(5, 6));

        verifyFloorState(1, Direction.UP, true, 1);
        verifyFloorState(2, Direction.UP, true, 1);
        verifyFloorPassed(Direction.UP, 3);
        verifyFloorStopped(Direction.UP, 4, 0);
        verifyFloorStopped(Direction.UP, 5, 1);
        verifyFloorStopped(Direction.UP, 6, 0);

    }

    @Test
    void switchesDirection(){

        addFloor(generateEvent(2, 3));
        addFloor(generateEvent(2, 4));
        addFloor(generateEvent(5, 3));


        verifyFloorPassed(Direction.UP, 1);
        verifyFloorStopped(Direction.UP, 2, 2);
        verifyFloorStopped(Direction.UP, 3, 0);
        verifyFloorStopped(Direction.UP, 4, 0);
        verifyFloorStopped(Direction.UP, 5, 1);
        addFloor(generateEvent(3, 6));
        verifyFloorPassed(Direction.DOWN, 4);

        verifyFloorStopped(Direction.DOWN, 3, 1);
        verifyFloorPassed(Direction.UP, 4);
        verifyFloorPassed(Direction.UP, 5);
        verifyFloorStopped(Direction.UP, 6, 0);
    }

    @Test
    void switchesDirectionOnFloor(){

        addFloor(generateEvent(0, 1));
        addFloor(generateEvent(1, 0));
        handleInitial(1);
        verifyFloorStopped(Direction.UP, 1, 1);
        verifyFloorStopped(Direction.DOWN, 0, 0);
    }

    @Test
    void switchDirectionDownToPickupPassenger() throws NoSuchFieldException, IllegalAccessException {
    	addFloor(generateEvent(0,5));
        handleInitial(1);
        verifyFloorPassed(Direction.UP, 1);
        verifyFloorPassed(Direction.UP, 2);
        verifyFloorPassed(Direction.UP, 3);
        verifyFloorPassed(Direction.UP, 4);
        verifyFloorStopped(Direction.UP, 5, 0);
        addFloor(generateEvent(3,5));
        verifyFloorPassed(Direction.DOWN, 4);
        verifyFloorStopped(Direction.DOWN, 3, 1);
        verifyFloorPassed(Direction.UP, 4);
        verifyFloorStopped(Direction.UP, 5, 0);

    }

    @Test
    void switchDirectionUpToPickupPassenger()  {

        addFloor(generateEvent(0,4));
        addFloor(generateEvent(5,3));

        handleInitial(1);
        verifyFloorPassed(Direction.UP, 1);
        verifyFloorPassed(Direction.UP, 2);
        verifyFloorPassed(Direction.UP, 3);
        verifyFloorStopped(Direction.UP, 4, 0);

        verifyFloorState(5, Direction.UP, true, 1);
        verifyFloorPassed(Direction.DOWN, 4);
        addFloor(generateEvent(4,6));
        verifyFloorStopped(Direction.DOWN, 3, 0);
        verifyFloorStopped(Direction.UP, 4, 1);
        addFloor(generateEvent(4,5));

        verifyFloorPassed(Direction.UP, 5);
        verifyFloorStopped(Direction.UP, 6, 0);
        verifyFloorPassed(Direction.DOWN, 5);
        verifyFloorStopped(Direction.DOWN, 4, 1);
        verifyFloorStopped(Direction.UP, 5, 0);

    }

    void addFloor(PassengerRequest e){
        eq.addFloor(e);
        eq.calculateServicingDirection();

    }
    //Simulate transition out of idle
    void handleInitial(int newEvents){
       assertEquals(true, eq.shouldStop());
        ArrayList<PassengerRequest> events = new ArrayList<>();
        if (eq.requestsInServicingDirection()){
            events = eq.getDestinationRequestList();
        }
        assertEquals(newEvents, events.size());
        eq.handleNewButtonPressed(events);
        eq.floorVisitedInDirection();
    }
    void verifyFloorPassed(Direction d, int newFloor){
        verifyFloorState(newFloor, d, false, 0);

    }

    void verifyFloorStopped(Direction direction, int newFloor, int added){
        verifyFloorState(newFloor, direction, true, added);
    }


    void verifyFloorState(int newFloor, Direction newDirection, boolean doorState, int floorsAdded){
        assertEquals(newDirection, eq.calculateMoveDirection(), "Direction");
        eq.moveToFloor(newFloor);
        assertEquals(doorState, eq.shouldStop(), "Should stop");

        if (eq.outOutRequests()){
            eq.floorVisitedInDirection();
            if (eq.shouldIdle()){
                return;
            } else {
                eq.determineNewService();
            }
        }

        ArrayList<PassengerRequest> events = new ArrayList<>();
        if (eq.requestsInServicingDirection()){
            events = eq.getDestinationRequestList();
        }
        assertEquals(floorsAdded, events.size());
        if (events.size() > 0)
            eq.handleNewButtonPressed(events);
        eq.floorVisitedInDirection();

    }

}
