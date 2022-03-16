/**
 * 
 */
package elevator.Floor;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Callum
 *
 */

public class PassengerRequest implements Serializable, Comparable<PassengerRequest> {
	private final int floor;
	private final int elevatorNumber;
	private final int time;
	private final String directionalButton;
	
	/**
	 * Construct event using numbers input directly
	 * @param floor
	 * @param elevatorNumber
	 * @param time
	 * @param directionalButton
	 */
	public PassengerRequest(int floor, int elevatorNumber, int time, String directionalButton) {
		this.floor = floor;
		this.elevatorNumber = elevatorNumber;
		this.time = time;
		this.directionalButton = directionalButton;
	}
	
	/**
	 * Create new Event object based on Event string specified in Project doc
	 * @param in Event string in form "HH:MM:SS.MMM N up/down C"
	 * @return New event object based on parsed string
	 */
	public static PassengerRequest parseEventString(String in) {
		//Split event string to usable parts
		String[] components = in.split(" ", 4);
		
		//Turn WW:XX:YY.ZZZ into [WW,XX,YY.ZZZ]
		String[] timeComponents = components[0].split(":",3);
		int hour   = Integer.parseInt(timeComponents[0]);
		int minute = Integer.parseInt(timeComponents[1]);
		
		//Turn XX.yyy into [XX,yyy]		
		String[] secondAndMilli = timeComponents[2].split("\\.",2);
		int seconds =      Integer.parseInt(secondAndMilli[0]);
		int milliSeconds = Integer.parseInt(secondAndMilli[1]);
		
		//Add milliseconds to create time since midnight in milliseconds
		int time = 0;
		time += hour*3600000;
		time += minute*60000;
		time += seconds*1000;
		time += milliSeconds;
		
		int floor = Integer.parseInt(components[1]);
		String directionalButton = components[2];
		int elevatorNumber = Integer.parseInt(components[3]);
		
		return new PassengerRequest(floor, elevatorNumber, time, directionalButton);
	}
	
	/**
	 * Get floor number
	 * @return
	 */
	public int getFloor() {
		return floor;
	}
	
	/**
	 * Get Elevator number
	 * @return
	 */
	public int getElevatorNumber() {
		return elevatorNumber;
	}
	
	/**
	 * Get time - represented as milliseconds since midnight
	 * @return
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * Get directional button, either up or down
	 * @return
	 */
	public String getDirectionalButton() {
		return directionalButton;
	}
	
	@Override
	public String toString() {
		int tmpTime = time;
		int milliSeconds = tmpTime % 1000;
		tmpTime -= milliSeconds;
		int seconds = (tmpTime % 60000)/1000;
		tmpTime -= seconds*1000;
		int minutes = (tmpTime % 3600000)/60000;
		tmpTime -= minutes*60000;
		int hour = tmpTime/3600000;
		String timeString = String.format("%02d:%02d:%02d.%03d",hour,minutes,seconds,milliSeconds);
		return (timeString + " " + floor + " " + directionalButton + " " + elevatorNumber);
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PassengerRequest event = (PassengerRequest) o;
		return floor == event.floor && elevatorNumber == event.elevatorNumber && time == event.time && Objects.equals(directionalButton, event.directionalButton);
	}

	@Override
	public int compareTo(PassengerRequest e){
		return this.time - e.time;
	}

}
