package elevator.communication;

import elevator.communication.messages.ElevatorEvent;
import elevator.communication.messages.SchedulerEvent;

import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * A central hub for all message passing.
 *
 * Handles events coming from both floor and elevator, each via a worker listener thread.
 * Handles events going out to elevator both async and sync, depending on if there are messages in the queue for the elevator.
 *
 */
public class Distributor {
    //Events waiting to go out
    private final LinkedList<SchedulerEvent> schedulerMsgs;
    private final HashMap<Integer, LinkedList<ElevatorEvent>> elevatorMsgs;

    //Datagram packets used to request data, for each elevator.
    private final HashMap<Integer, LinkedList<DatagramPacket>> elevatorRequestForDataQueue;
    private final FloorToScheduler fts;
    private final ElevatorToScheduler ets;
    private final Thread ftsThread;
    private final Thread etsThread;
    public final static int MAX_ELEVATORS = 10;

    public Distributor(){
        schedulerMsgs = new LinkedList<>();
        elevatorMsgs = new HashMap<>();
        elevatorRequestForDataQueue = new HashMap<>();

        fts = new FloorToScheduler(this, 4444);
        ets = new ElevatorToScheduler(this, 3333);
        (ftsThread = new Thread(fts)).start();
        (etsThread = new Thread(ets)).start();
    }

    private void initQueuesForElevator(int elevator){
        if (!elevatorMsgs.containsKey(elevator)) elevatorMsgs.put(elevator, new LinkedList<>());
        if (!elevatorRequestForDataQueue.containsKey(elevator)) elevatorRequestForDataQueue.put(elevator, new LinkedList<>());
    }

    /**
     * If there is an active request for data, respond with the event. Otherwise, add to a queue.
     * @param e The event to send
     */
    public synchronized void sendToElevator(ElevatorEvent e){
        initQueuesForElevator(e.getDestId());
        //If there is an active request for data, respond to that
        if (elevatorRequestForDataQueue.get(e.getDestId()).size() != 0){
            ets.respond(elevatorRequestForDataQueue.get(e.getDestId()).removeFirst(), e);
            notifyAll();
            return;
        }

        //Otherwise queue up the data, to be gotten on next sendElevatorEventNetwork call.
        elevatorMsgs.get(e.getDestId()).addLast(e);
        notifyAll();
        return;
    }

    /**
     * Queue up an event to be sent to the scheduler
     * @param se event to send
     */
    public synchronized void sendToScheduler(SchedulerEvent se){
        schedulerMsgs.addLast(se);
        notifyAll();
        return;
    }

    /**
     * Get events off the queue.
     * @return the oldest event
     */
    public synchronized SchedulerEvent getSchedulerEvents(){
        while (schedulerMsgs.size() == 0){
            try{
                wait();
            }catch (InterruptedException e){
                return null;
            }
        }
        return schedulerMsgs.removeFirst();
    }
    
    /**
     * Get all available SchedulerEvents
     * @return
     */
    public synchronized LinkedList<SchedulerEvent> getSchedulerEventsBatch(){
    	while (schedulerMsgs.size() == 0){
            try{
                wait();
            }catch (InterruptedException e){
                return null;
            }
        }
    	LinkedList<SchedulerEvent> retEvents = new LinkedList<SchedulerEvent>();
    	retEvents.addAll(schedulerMsgs);
    	schedulerMsgs.clear();
        return retEvents;
    }
    

    /**
     * Get events off the queue of messages destined for a elevator, and send them back to the requester over the network.
     * If there are messages waiting, send one, if not, store the request in a queue, so that it can be responded to when the next message is added.
     *
     * @param request The packet that invoked this request
     * @param elevator The elevator to communicate with.
     */
    public synchronized void sendElevatorEventNetwork(DatagramPacket request, int elevator){
        initQueuesForElevator(elevator);
        if (elevatorMsgs.get(elevator).size() != 0){
            ets.respond(request, elevatorMsgs.get(elevator).removeFirst());
        } else {
            elevatorRequestForDataQueue.get(elevator).addLast(request);
        }
        notifyAll();
    }

    public void reapListeners()  {
        ets.kill();
        fts.kill();
        try {
            etsThread.join();
            ftsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
