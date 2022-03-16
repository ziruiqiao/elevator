package elevator.elevator;

import elevator.Floor.ElevatorFloor;

public class ArrivalSensor {
    private final Elevator e;
    private final ElevatorFloor ef;
    private final int floorNum;
    static int fnum = 1;
    
    public ArrivalSensor(Elevator e,int floor) {
        this.e = e;
        this.ef = new ElevatorFloor(floor);
        this.floorNum = floor;
    }

    public ElevatorFloor getFloor() {
        return ef;
    }
    
    public void arrivedAtFloor() {
    	e.ArrivalSensorNearFloor();
    }
    
}
