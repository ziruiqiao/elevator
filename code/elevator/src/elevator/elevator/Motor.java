package elevator.elevator;

import elevator.util.Sleep;

public class Motor {
    Elevator e;
    private Direction motorState;
    private long elevatorSpeed;

    public Motor(Elevator e) {
        this.e = e;
        this.elevatorSpeed = 0;
        this.motorState = Direction.STOPPED;
    }
    
    public void setDirection(Direction d) {
    	this.motorState = d;
    	this.elevatorSpeed = 0;
    }

    public Direction getDirection(){return motorState;}

    public void move() {
    	//Calculate acceleration:
    	if(elevatorSpeed < 30) {
    		elevatorSpeed += 15;
    	}
    	

        long timeToTravel = (60 / elevatorSpeed) * 1000;
    	System.out.println("(elevator) Moving... time between floors: " + timeToTravel);
        Sleep.EnvironmentSleep(timeToTravel);

    	
    	e.movedInDirection(motorState);
    	e.triggerArrivalSensor();
    }

    public boolean stop() {

    	long timeToStop = 2000;
    	System.out.println("(elevator) Stopping... time to stop: " + timeToStop);
        Sleep.EnvironmentSleep(timeToStop);

    	
        this.motorState = Direction.STOPPED;
        return true;
    }

    public boolean keepMoving() {
        // if the floor is 0 and state is going down
        // turn to go up
        // if the floor is highest and state is going up
        //turn to go down
        return true;
    }
}
