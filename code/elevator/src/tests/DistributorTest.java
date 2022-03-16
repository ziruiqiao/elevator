package tests;

import elevator.communication.Distributor;
import elevator.communication.messages.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Clark Bains
 *
 */

class DistributorTest {

    Distributor ds;
    SchedulerEvent se;
    SchedulerEvent se1;
    byte[] se1Data;
    ElevatorEvent ee;
    ElevatorEvent ee1;
    byte[] ee1Data;

    @BeforeEach
    void setUp() {
        ds = new Distributor();
        se = new SchedulerEvent(SystemComponent.ELEVATOR, 1, 1, SchedulerEventTypes.OnDoorClose);
        se1Data = new byte[]{1, 2, 3};
        se1 = new SchedulerEvent(SystemComponent.ELEVATOR, 1, 1, SchedulerEventTypes.OnElevatorStop, se1Data);

        ee = new ElevatorEvent(SystemComponent.SCHEDULER, 1, 1, ElevatorEventTypes.OpenDoorCommand);
        ee1Data = new byte[]{2,3,4};
        ee1 = new ElevatorEvent(SystemComponent.SCHEDULER, 1, 1, ElevatorEventTypes.AtDestinationFloor, ee1Data);

    }
    @AfterEach
    void tearDown() {
        ds.reapListeners();
    }


    @Test
    void testSchedulerEvent(){
        ds.sendToScheduler(se);
        ds.sendToScheduler(se1);
        assertEquals(se, ds.getSchedulerEvents());
        assertEquals(se1, ds.getSchedulerEvents());
    }

    @Test
    void testElevatorEvent(){
        ds.sendToElevator(ee);
        ds.sendToElevator(ee1);
        //assertEquals(ee, ds.getElevatorEvents());
        //assertEquals(ee1, ds.getElevatorEvents());
    }
}
