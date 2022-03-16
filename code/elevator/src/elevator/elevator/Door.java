package elevator.elevator;

/**
 * @author Callum + David
 *
 */
public class Door {
	enum DOOR_STATE {
		OPEN,
		CLOSED,
		MOVING
	}

	private DOOR_STATE doorState;
	
	public Door() {
		this.doorState = DOOR_STATE.OPEN;
	}

	public boolean openDoor() {
		this.doorState = DOOR_STATE.OPEN;
		return true;
	}
	
	public boolean closeDoor() {
		this.doorState = DOOR_STATE.CLOSED;
		return true;
	}

	public DOOR_STATE doorStatus()
	{
		return doorState;
	}
	
}
