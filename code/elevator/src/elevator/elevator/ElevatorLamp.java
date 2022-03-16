package elevator.elevator;

/**
 * @author Callum
 *
 */
public class ElevatorLamp {
	private boolean state;
	private final int  floorNum;
	
	public ElevatorLamp(int f) {
		this.state = false;
		this.floorNum = f;
	}
	
	public void turnOn() {
		this.state = true;
	}
	
	public void turnOff() {
		this.state = false;
	}

	public String toString() {
		return "["+floorNum+" - "+(state ? "\u001b[33m * \u001b[0m" : "\u001b[37m o \u001b[0m")+"]";
	}
}

