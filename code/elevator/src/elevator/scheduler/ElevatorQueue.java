package elevator.scheduler;

import elevator.Floor.PassengerRequest;
import elevator.elevator.Direction;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 
 * @author Clark and Callum
 *
 */
public class ElevatorQueue {
    private final boolean[] upQueue;
    private final boolean[] downQueue;
    
    private final LinkedList<PassengerRequest>[] upQueueOutstanding; // Events we should read for more info once we get to the floor.
    private final LinkedList<PassengerRequest>[] downQueueOutstanding;
    
    private Direction direction; // Direction elevator is moving
    private Direction lastServed; // Last direction is serviced
    private Direction servicingDirection; //Direction it wants to service
    
    private boolean hasGuests;
    private int currentFloor;
    
    public static final int MAX_FLOORS = 20;

    @SuppressWarnings("unchecked")
	public ElevatorQueue(){
        upQueue = new boolean[MAX_FLOORS];
        downQueue = new boolean[MAX_FLOORS];
        
        upQueueOutstanding = new LinkedList[MAX_FLOORS];
        downQueueOutstanding = new LinkedList[MAX_FLOORS];
        
        currentFloor = 0;
        
        direction = Direction.STOPPED;
        lastServed = Direction.STOPPED;
        servicingDirection = Direction.STOPPED;

        hasGuests = false;
        
        for (int i = 0; i < MAX_FLOORS; i++) {
            upQueueOutstanding[i] = new LinkedList<>();
            downQueueOutstanding[i] = new LinkedList<>();
        }
    }

    /**
     * Getter for moving direction
     * @return Direction Enum representing physical direction Elevator is moving
     */
    public Direction getDirection() {
        return direction;
    }

	/**
	 * Setter for moving direction
	 */
	public void setDirection(Direction d)
	{
		this.direction=d;
	}

	/**
	 * Setter for servicingDirection
	 */
	public void setServicingDirection(Direction sd)
	{
		this.servicingDirection=sd;
	}

    /**
     * Getter for current floor elevator is on
     * @return int representing floor with 0 representing the first floor
     */
    public int getCurrentFloor() { return currentFloor;  }

	/**
	 * Setter for current floor elevator is on
	 */
	public void setCurrentFloor(int floor)
	{
		this.currentFloor=floor;
	}

    /**
     * Retrieves list of events from the current Floors outstanding queue in direction elevator is servicing
     * @return Arraylist of Events ordered in time added
     */
    public ArrayList<PassengerRequest> getDestinationRequestList() {
    	ArrayList<PassengerRequest> retVal = new ArrayList<>();
    	if(servicingDirection == Direction.UP) {
    		retVal = new ArrayList<>(upQueueOutstanding[currentFloor]);
    		upQueueOutstanding[currentFloor].clear();
    	} else if(servicingDirection == Direction.DOWN) {
    		retVal = new ArrayList<>(downQueueOutstanding[currentFloor]);
    		downQueueOutstanding[currentFloor].clear();
    	}
    	return retVal;
    }
    
    /**
     * Moves elevator to specified floor
     * @param f int representing floor which Elevator has moved to
     */
    public void moveToFloor(int f){
        if (f < 0 || f >= MAX_FLOORS) System.err.println("Tried to move to non existent floor");
        //else if (direction == Direction.UP && currentFloor >= f) System.err.println("Direction is up, yet tried to move to a lower floor");
        //else if (direction == Direction.DOWN && currentFloor <= f) System.err.println("Direction is down, yet tried to move to a higher floor");
        //else if (direction == Direction.STOPPED) System.err.println("Direction is stopped, yet tried to move");
        else currentFloor = f;
    }
    
    /**
     * Checks to see if there are any floors which elevator should visit in either direction
     * @return boolean that's true if there are floors to visit, false otherwise
     */
    public boolean floorsInEitherDirection(){
        for (int i = 0; i < MAX_FLOORS; i++){
            if ((upQueue[i] || downQueue[i]) && i != currentFloor) return true;
        }
        return false;
    }

    /**
     * Adds a requested floor to list of floors to visit
     * @param e Event from Floor that represents user requesting an elevator
     */
    public void addFloor(PassengerRequest e){
        if (e.getDirectionalButton().equals("up")){
            upQueue[e.getFloor()] = true;
            upQueueOutstanding[e.getFloor()].add(e);
        } else {
            downQueue[e.getFloor()] = true;
            downQueueOutstanding[e.getFloor()].add(e);
        }
    }

    /**
     * Check to see if there are more floors that the elevator should visit in it's current servicing direction
     * @return true if there are more floors to visit, false otherwise
     */
    public boolean moreFloorsInServicingDirection() {
    	//When servicing UP pick people up only at first floor requested
    	if(servicingDirection == Direction.UP) {
    		for(int i = 0; i < currentFloor; i++) {
    			//Find first UP request
    			if(upQueue[i]) {
                    return i > currentFloor;
    			}
    		}
			return false;
    	} else {
    		for(int i = MAX_FLOORS; i > currentFloor; i--) {
    			if(downQueue[i]) return true;
    		}
    		return false;
    	}
    }
    
    /**
     * Finds the first request that the elevator should serve, starting from the bottom and searching to the top
     */
    public void calculateServicingDirection() {
    	//We only care about determining new floor if currently stopped
    	if(servicingDirection == Direction.STOPPED) {
    		//TODO: Change this to closest 
    		for(int i = 0; i < MAX_FLOORS; i++) {
    			if(upQueue[i]) {
    				servicingDirection = Direction.UP;    				
    				return;
    			}
    			if(downQueue[i]) {
    				servicingDirection = Direction.DOWN;    				
    				return;
    			}
    		}
    	}
    }
     
    /**
     * Check to see if there are outstanding requests in direction elevator is currently servicing
     * @return true if there are outstanding requqests, false otherwise
     */
    public boolean requestsInServicingDirection() {
    	if(servicingDirection == Direction.UP) {
    		if(hasGuests || direction == servicingDirection) {
    			return (upQueueOutstanding[currentFloor].size() > 0);
    		} else if(firstFloorToService() == currentFloor) {
    			return (upQueueOutstanding[currentFloor].size() > 0);
			}
    	}
    	else if(servicingDirection == Direction.DOWN) {
    		if(hasGuests || direction == servicingDirection) {
    			return (downQueueOutstanding[currentFloor].size() > 0);
    		} else if(firstFloorToService() == currentFloor) {
    			return (downQueueOutstanding[currentFloor].size() > 0);
    		}
    	} 
    	return false;
    }
    
    /**
     * Check to see if Elevator should service a floor as it travels by
     * @return boolean, true if Elevator should stop and service. false otherwise
     */
    public boolean shouldServiceFloor() {
    	if (currentFloor < 0 || currentFloor >= MAX_FLOORS){
            System.err.println("Tried to test visiting an invalid floor");
            return false;
        }
    	
    	return (!moreFloorsInServicingDirection());
    }
    
    /**
     * Handle floor button being pressed on elevator, and ensure proper floor is visited
     * @param events Event received from user pressing a floor button
     */
    public void handleNewButtonPressed(ArrayList<PassengerRequest> events) {
    	for(int i = 0; i < events.size(); i++) {
    		int floorNum = events.get(i).getElevatorNumber();
    		if(events.get(i).getDirectionalButton().equals("up"))
    			upQueue[floorNum] = true;
    		else
    			downQueue[floorNum] = true;
    	}
    	hasGuests = true;
    }
    
    /**
     * Marks a floor as visited in direction Elevator is currently servicing
     */
    public void floorVisitedInDirection() {
		if(servicingDirection == Direction.UP) {
			upQueue[currentFloor] = false;
		} else {
			downQueue[currentFloor] = false;
		}
    }
    
    /**
     * Find first floor Elevator should service. If servicing Up requests it will return lowest up request, and if serving down requests it will serve highest down request
     * @return int representing the first floor the Elevator should service
     */
    public int firstFloorToService() {
    	if(servicingDirection == Direction.UP) {
    		for(int i = 0; i < MAX_FLOORS; i++) {
    			if(upQueue[i]) return i;
    		}
    		return -1;
    	} else {
    		for(int i = MAX_FLOORS - 1; i >= 0; i--) {
    			if(downQueue[i]) return i;
    		}
    		return -1;
    	}
    }
    
    /**
     * Find next floor that Elevator should service in it's servicingDirection
     * @return int representing floor that it should be visited
     */
    public int nextFloor() {
    	if(servicingDirection == Direction.UP) {
    		int start = hasGuests ? currentFloor + 1 : 0;
    		//If Elevator has folks on it will return next UP floor, otherwise it looks for first UP floor
    		for(int i = start; i < MAX_FLOORS; i++) {
    			if(upQueue[i]) {
    				return i;
    			}
    		}
    	} else {
    		int start = hasGuests ? currentFloor - 1 : MAX_FLOORS - 1;
    		for(int i = start; i >= 0; i--) {
    			if(downQueue[i]) {
    				return i;
    			}
    		}
    	}
    	
    	//Error case
    	return -1;
    }
    
    /**
     * Calculate which direction the Elevator should move in next
     * @return Direction enum representing direction elevator is going to move
     */
    public Direction calculateMoveDirection() {
    	if(nextFloor() > currentFloor) {
    		return direction = Direction.UP;
    	} else {
    		return direction = Direction.DOWN;
    	}
    }

    /**
     * Determines whether an elevator should stop at a floor
     * @return boolean, true if it should stop, false otherwise
     */
    public boolean shouldStop() {
    	//If it has guests only stop if guests want to go the same way
    	if(hasGuests) {
    		if(direction == servicingDirection) {
    			if(direction == Direction.UP && upQueue[currentFloor]) return true;
    			else return direction == Direction.DOWN && downQueue[currentFloor];
    		} else return false;
    	} 
    	//If elevator does not contain guest, keep going till further floor
    	else {
            return currentFloor == firstFloorToService();
    	}
    }

    /**
     * Determines if an Elevator should idle once done it's current job
     * @return boolean, true if it should idle, false otherwise
     */
    public boolean shouldIdle() {    	
    	if(servicingDirection == Direction.UP) {
    		//Done servicing all UP requests
    		if(nextFloor() == -1 && upQueueOutstanding[currentFloor].size() == 0) {
    			//See if any down requests need servicing
    			for(int i = MAX_FLOORS-1; i >= 0; i--) {
    				if(downQueue[i]) return false;
    			}
    			//Done UP requests and no DOWN requests
    			return true;
    		} else return false;
    	} 
    	else if (servicingDirection == Direction.DOWN) {
    		//Done servicing all DOWN requests
    		if(nextFloor() == -1 && downQueueOutstanding[currentFloor].size() == 0) {
    			//See if any UP requests need servicing
    			for(int i = 0; i < MAX_FLOORS; i++) {
    				if(upQueue[i]) return false;
    			}
    			//Done DOWN requests and no UP requests
    			return true;
    		} else return false;
    	}
    	return true;
    }
    
    /**
     * Check to see if the Elevator is out of requests
     * @return boolean, true if elevator is out of requests, false otherwise
     */
    public boolean outOutRequests() {
    	if(servicingDirection == Direction.UP) {
    		for(int i = currentFloor; i < MAX_FLOORS - 1; i ++) {
    			if(upQueue[i+1] || upQueueOutstanding[i].size() > 0) return false;
    		}
    		boolean h = false;
    		for(boolean i : downQueue) {
    			if (i) {
    				h = true;
    				break;
    			}
    		}
    		
    		hasGuests = h;
    		if(!h) {
    			for(int i = currentFloor - 1; i >= 0; i--) {
    				if(downQueue[i] || downQueueOutstanding[i].size() > 0) return false;
    			}
    		}
    		
    	} else if(servicingDirection == Direction.DOWN) {
    		for(int i = currentFloor; i > 0; i--) {
    			if(downQueue[i-1] || downQueueOutstanding[i].size() > 0) return false;
    		}
    		boolean h = false;
    		for(boolean i : upQueue) {
    			if (i) {
    				h = true;
    				break;
    			}
    		}
    		
    		hasGuests = h;
    		if(!h) {
    			for(int i = currentFloor + 1; i < MAX_FLOORS; i++) {
    				if(upQueue[i] || upQueueOutstanding[i].size() > 0) return false;
    			}
    		}
    	}
    	//None of the above conditions met means more requests to serve
    	return true;
    }
    
    /**
     * Finds a new servicing direction for an Elevator when a request is received. Set it to the current servicingDirection
     */
    public void determineNewService() {
    	//Save previous service direction
    	lastServed = servicingDirection;
    	hasGuests = false;
    	//If we were going UP check for new DOWN requests
    	if(lastServed == Direction.UP) {
    		for(int i = MAX_FLOORS - 1; i >= 0; i--) {
    			if(downQueue[i]) {
    				servicingDirection = Direction.DOWN;
    				return;
    			}
    		}
    	}
    	//If we were going DOWN check for new UP requests
    	if(lastServed == Direction.DOWN) {
    		for(int i = 0; i < MAX_FLOORS; i++) {
    			if(upQueue[i]) {
    				servicingDirection = Direction.UP;
    				return;
    			}
    		}
    	}
    	//Otherwise just check for new requests
    	for(int i = 0; i < MAX_FLOORS; i++) {
    		if(upQueue[i]) {
    			servicingDirection = Direction.UP;
				return;
    		}
    		if(downQueue[i]) {
    			servicingDirection = Direction.DOWN;
    			return;
    		}
    	}
    	//TODO: Add in case that a new up/down request from floor already visited is added
    	servicingDirection = direction = Direction.STOPPED;    	
    	
    }

    /**
     * Reset Elevator to it's initial state where all directions are stopped and it doesn't have guests
     */
    public void resetElevator() {
    	servicingDirection = Direction.STOPPED;
    	direction = Direction.STOPPED;
    	hasGuests = false;
    }

    public int determineDistance(int floorNum, String directionStr) {
    	Direction serviceDir;
    	if(directionStr.equals("up")) {
    		serviceDir = Direction.UP;
    	} else {
    		serviceDir = Direction.DOWN;
    	}
    	//Basic stopped case
    	if(servicingDirection == Direction.STOPPED) {
    		return (Math.abs(currentFloor - floorNum));
    	} 
    	//Case where elevator is already moving the right direction
    	else if(servicingDirection == this.direction) {
    		if(serviceDir == servicingDirection) {
    			//If we're already on the right floor
    			if(currentFloor == floorNum) return 0;	
    			
    			if(serviceDir == Direction.UP) {
    				//If it's above us, find how many floors away
    				if(floorNum > currentFloor) return (floorNum - currentFloor);
    				//Distance to the top, then the bottom, then back up to the floor
    				else return (2 * MAX_FLOORS - currentFloor + floorNum);
    			} 
    			else {
    				//If it's below us, find out how many floors away
    				if(floorNum < currentFloor) return (currentFloor - floorNum);
    				//Distance to the bottom, then back up to the top, then to the requested floor
    				else return currentFloor + 2 * MAX_FLOORS - floorNum;
    			}
    		}
    		else {
    			//Distance to the top, and then to the current floor
    			if(servicingDirection == Direction.UP) return (MAX_FLOORS - currentFloor) + (MAX_FLOORS - floorNum);
    			//Distance to the bottom and then to the requested floor
    			else return currentFloor + floorNum;
    		}
    	}
    	//Elevator is moving in the opposite direction of the servicing
    	else {
    		//If we're going to eventually service the request direction
    		if(servicingDirection == serviceDir) {
    			if(servicingDirection == Direction.UP) return currentFloor + floorNum;
    			else return (MAX_FLOORS - currentFloor) + (MAX_FLOORS - floorNum); 
    		} 
    		//Request is not in the same direction we're servicing
    		else {
    			if(servicingDirection == Direction.UP) return (currentFloor) + (MAX_FLOORS) + (MAX_FLOORS - floorNum);
    			else return (MAX_FLOORS - currentFloor) + (MAX_FLOORS) + (floorNum);
    		}
    	}
    
    }

}
