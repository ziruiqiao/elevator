package elevator.Floor;

import elevator.communication.messages.SchedulerEvent;
import elevator.communication.messages.SchedulerEventTypes;
import elevator.communication.messages.SystemComponent;
import elevator.util.Serializer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Uses a threadpool to send delayed events.
 * @author Clark
 */
class FloorThread implements Runnable {

    private final int number;
    private final int port;
    private final ArrayList<PassengerRequest> eventArrayList;
    private final ScheduledExecutorService scheduler;
    private DatagramSocket socket_m;
    private final InetAddress addr;
    /**
     * Constructor for FloorThread
     * @param addr address to send to
     * @param port port to send to
     * @param n Floor number
     * @param events List of events to send
     */
    public FloorThread(InetAddress addr, int port, int n, ArrayList<PassengerRequest> events) {
        this.number = n;
        this.port = port;
        this.addr = addr;
        eventArrayList = events;
        scheduler = Executors.newScheduledThreadPool(1);
        try {
            socket_m = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        //System.out.printf("Floor Thread Started (%d)\n", number);
        for (PassengerRequest e: eventArrayList){
            Runnable eventLauncher = new Runnable() {
                @Override
                public void run() {
                    SchedulerEvent s = new SchedulerEvent(SystemComponent.FLOOR, number, -1,
                            SchedulerEventTypes.OnFloorRequest, Serializer.toByteArray(e));

                    byte[] data = Serializer.toByteArray(s);
                    DatagramPacket dp;
                    try {
                         dp = new DatagramPacket(data, data.length, addr, port);
                        socket_m.send(dp);
                    } catch (IOException ex) {
                        System.err.println("Could not find host. Cannot send floor request.");
                        return;
                    }
                }
            };
            scheduler.schedule(eventLauncher, e.getTime(), TimeUnit.MILLISECONDS);
        }
    }
}