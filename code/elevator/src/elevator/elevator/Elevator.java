package elevator.elevator;

import elevator.Floor.ElevatorFloor;
import elevator.communication.messages.*;
import elevator.elevator.states.IdleState;
import elevator.util.Serializer;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;


/**
 * Represents an Elevator that services a building. Has functional door, motor, arrival sensors, lamps
 *
 * @author David and Callum and Zirui
 */
public class Elevator implements Runnable {
    public final int NUM_FLOORS = 20;
    private final int elevatorNumber;
    private final Door door;
    private final Motor motor;
    private final ElevatorStateSingleton states;
    private final ArrayList<ArrivalSensor> arrivalSensors;
    private final ArrayList<ElevatorLamp> lamps;
    private final int serverPort;
    private final InetAddress serverAddr;
    private int currentFloor;
    private ElevatorState state;
    private DatagramSocket soc;


    public Elevator(InetAddress serverAddr, int serverPort, int number) {
        elevatorNumber = number;
        currentFloor = 0;
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;

        door = new Door();
        motor = new Motor(this);


        states = new ElevatorStateSingleton(this);
        changeState(states.getState(IdleState.class));

        arrivalSensors = new ArrayList<>();
        lamps = new ArrayList<>();

        try {
            soc = new DatagramSocket();
        } catch (SocketException e1) {
            e1.printStackTrace();
            System.exit(1);
        }

        //Populate Lamps and Arrival Sensors
        for (int i = 0; i < NUM_FLOORS; i++) {
            arrivalSensors.add(new ArrivalSensor(this, i));
            lamps.add(new ElevatorLamp(i));
        }

        changeState(states.getState(IdleState.class));
        notifyScheduler(SchedulerEventTypes.StartUp);
    }

    /**
     * Getter for Elevator number
     *
     * @return int representing elevator number
     */
    public int getElevatorNumber() {
        return elevatorNumber;
    }

    /**
     * Get current floor elevator is on
     *
     * @return
     */
    public ElevatorFloor getCurrentFloor() {
        return arrivalSensors.get(currentFloor).getFloor();
    }

    /**
     * Get Singleton containing all states in the state machine
     *
     * @return
     */
    public ElevatorStateSingleton getStates() {
        return states;
    }

    /**
     * Getter for the current state of the Elevator
     *
     * @return
     */
    public ElevatorState getState() {
        return state;
    }

    /**
     * Updates elevator position after it is moved
     *
     * @param d
     */
    public void movedInDirection(Direction d) {
        switch (d) {
            case DOWN:
                currentFloor--;
                break;
            case UP:
                currentFloor++;
                break;
            default:
                break;
        }
    }

    /**
     * Triggers arrival sensor on floor elevator is arriving to
     */
    public void triggerArrivalSensor() {
        arrivalSensors.get(currentFloor).arrivedAtFloor();
    }

    /**
     * Open elevator doors
     *
     * @return
     */
    public boolean openDoor() {
        return door.openDoor();
    }

    /**
     * Close elevator doors
     *
     * @return
     */
    public boolean closeDoor() {
        return door.closeDoor();
    }

    /**
     * Move Elevator in direction specified
     *
     * @param d
     */
    public void moveMotor(Direction d) {
        motor.setDirection(d);
        continueMoving();
    }

    /**
     * Continue moving in last moved direction
     */
    public void continueMoving() {
        motor.move();
    }

    /**
     * Stop the motor at the current floor
     */
    public void stopMotor() {
        motor.stop();
    }

    /**
     * Turn floor lamp on specified floor ON
     *
     * @param floorNum
     */
    public void turnLampOn(int floorNum) {
        lamps.get(floorNum).turnOn();
    }

    /**
     * Turn floor lamp on specified floor OFF
     *
     * @param floorNum
     */
    public void turnLampOff(int floorNum) {
        lamps.get(floorNum).turnOff();
    }

    /**
     * Event representing when an arrival sensor is near a floor
     */
    public void ArrivalSensorNearFloor() {
        state.arrivalSensorNearFloor();
    }

    /**
     * Notify scheduler of a new Event via networked Distributor
     *
     * @param cmd  The command to send
     * @param data optional data to send
     */
    public void notifyScheduler(SchedulerEventTypes cmd, byte... data) {
        SchedulerEvent se = new SchedulerEvent(SystemComponent.ELEVATOR, getElevatorNumber(), getElevatorNumber(), cmd, data);
        send(se);

        ElevatorEvent e = receive();
        if (e.getCommand() != ElevatorEventTypes.Ack) {
            System.err.println("(elevator) recieved non-ack to message");
        }
    }

    /**
     * Change the current Elevator state
     *
     * @param es
     */
    public void changeState(ElevatorState es) {
        System.out.println();
        ElevatorState old = null;
        if (state != null) {
            old = state;
            old.exit();
            //System.out.printf("(elevator)  FSM: %s -> %s\n", old.getClass().getSimpleName(), es.getClass().getSimpleName());

        } else {
            //System.out.printf("(elevator)  FSM:  -> %s\n", es.getClass().getSimpleName());
        }
        state = es;
        state.entry();
    }

    /**
     * Print out the current state of the Elevator and any information that should be output
     */
    public void printInformation() {
        String infoString = ("========================\n");
        infoString += ("Elevator Number: [" + elevatorNumber + "]\n");
        String lampStatus = "";
        for (int i = 0; i < lamps.size(); i+=3) {
            //To give the illusion of an elevator button template I do rows of three
            for (int j=0;j<3 && i+j < lamps.size();j++)
            {
                lampStatus += lamps.get(i+j).toString();
            }
            lampStatus += "\n";
        }
        infoString += lampStatus + "\n";
        infoString += ("Door: [" + door.doorStatus() + "]\n");
        infoString += ("Floor: [" + getCurrentFloor() + "]\n");
        if(motor.getDirection().toString() != "STOPPED" )
        {
            infoString += ("Status: [Moving " + motor.getDirection() + "]\n");
        }
        else
        {
            infoString += ("Status: [" + motor.getDirection() + "]\n");
        }
        infoString += ("STATE: [" + state.getClass().getSimpleName() + "]\n");
        infoString += ("========================\n");
        System.out.println(infoString);
    }

    public ElevatorEvent receive() {
        DatagramPacket pac;
        byte[] receive = new byte[2000];
        pac = new DatagramPacket(receive, receive.length);
        try {
            soc.receive(pac);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        byte[] trimmed = new byte[pac.getLength()];
        System.arraycopy(receive, 0, trimmed, 0, pac.getLength());
        ElevatorEvent e;
        return Serializer.fromByteArray(trimmed, ElevatorEvent.class);
    }

    public void send(Event ee) {
        DatagramPacket pac = null;
        byte[] data = Serializer.toByteArray(ee);

        pac = new DatagramPacket(data, data.length, serverAddr, serverPort);
        try {
            soc.send(pac);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {

        while (true) {
            printInformation();
            send(new SchedulerEvent(SystemComponent.ELEVATOR, getElevatorNumber(), -1, SchedulerEventTypes.RequestData));
            ElevatorEvent e = receive();
            switch (e.getCommand()) {
                case OpenDoorCommand:
                    state.openDoorCommand(e.getData()[0] == 1);
                    break;
                case DoorCloseCommand:
                    state.doorCloseCommand();
                    break;
                case MoveMotorCommand:
                    state.moveMotorCommand(Serializer.fromByteArray(e.getData(), Direction.class));
                    break;
                case AtDestinationFloor:
                    state.atDestinationFloor();
                    break;
                case ContinueMoving:
                    state.continueMoving();
                    break;
                case FloorButtonPressed:
                    state.floorButtonPressed(Serializer.fromByteArray(e.getData(), ArrayList.class));
                    break;
            }

        }
    }
}
