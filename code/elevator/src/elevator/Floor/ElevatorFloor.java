package elevator.Floor;

/**
 * @author Badral
 */
public class ElevatorFloor {
    public ElevatorFloor(int floorNum) {
        this.floorNum = floorNum;
    }

    public int getFloorNum() {
        return floorNum;
    }

    private final int floorNum;
    
    public String toString() {
    	return Integer.toString(floorNum);
    }
}