package elevator;

import elevator.Floor.FloorLauncher;
import elevator.communication.Distributor;
import elevator.elevator.Elevator;
import elevator.scheduler.Scheduler;
import elevator.scheduler.SchedulerLauncher;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * Scheduler Test Application
 */
public class ElevatorAndFloorTestApplication {
    
    /**
     * Practical test of the Scheduler state machine, simulating calls from the floor and elevator subsystems
     * @param args The filename to read from
     */
    public static void main(String[] args) {
        String filename = "resources/floorInputs";
        if (args.length >= 2){
            filename = args[1];
            System.out.println("Reading Floor inputs from file: " + filename);
        }
        InetAddress server = null;

        try {
             server = InetAddress.getByName("");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println(server.getHostAddress());
        for (int i = 0; i < 5; i++) {
            Elevator e = new Elevator(server, 3333, i);
            Thread te = new Thread(e, "Elevator-Thread ("+i+")");
            te.start();
        }

        FloorLauncher f1 = new FloorLauncher(server, 4444, filename);
    }
}

