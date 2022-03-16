package elevator.Floor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Badral
 */
public class FloorLauncher {


    private final String filename;
    private final InetAddress serverAddr;
    private final int port;
    /**
     * Constructor for Floor
     * @param filename Name of file to read
     */
    public FloorLauncher(InetAddress server, int port, String filename) {
        this.filename = filename;
        this.serverAddr = server;
        this.port = port;
        run();
    }
    

    /**
     * Read events from text floorInputs send them to scheduler as floor events
     * Check scheduler for new elevator event
     */
    public void run() {

        // Read from the text floorInputs and initialize events
        HashMap<Integer, ArrayList<PassengerRequest>> events = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                PassengerRequest e = PassengerRequest.parseEventString(line);
                if (!events.containsKey(e.getFloor())){
                    events.put(e.getFloor(), new ArrayList<>());
                }
                events.get(e.getFloor()).add(e);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Could not find file " + filename);
        } catch (IOException e) {
            System.err.println("Could not read file " + filename);
            e.printStackTrace();
        }

        for(Integer e: events.keySet()){
            FloorThread ft = new FloorThread(serverAddr, port, e, events.get(e));
            Thread executor = new Thread(ft, "Floor Thread (" + e + ")");
            executor.start();
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        String filename = "resources/floorInputs";
        if (args.length > 1) filename = args[1];
        InetAddress s = InetAddress.getLocalHost();
        FloorLauncher f = new FloorLauncher(s, 4444, filename);
    }    
}