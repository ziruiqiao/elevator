package elevator.communication;

import elevator.communication.messages.SchedulerEvent;
import elevator.util.Serializer;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @author Zirui
 */
abstract class DistributorListener implements Runnable{
	
	private DatagramSocket socket_m;
	private final byte[] buffer_ma;
	protected Distributor distrib_m;
	private boolean running;

	/**
	 * @param d The distributor to send recieved events to
	 * @param port the port to listen on
	 */
	public DistributorListener(Distributor d, int port) {
		try {
            socket_m = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
		this.distrib_m = d;
		this.running = true;
		buffer_ma = new byte[2000];
	}

	/**
	 * Thread safe method to respond to a sent packet.
	 * @param src the packet to extract the sender address and port
	 * @param data Serializable data to send back to them
	 */
	public void respond (DatagramPacket src, Serializable data) {
		byte[] byteData = Serializer.toByteArray(data);
		DatagramPacket sendPac = new DatagramPacket(byteData, byteData.length, src.getAddress(), src.getPort());
		try {
            socket_m.send(sendPac);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
	}

	/**
	 * Stop listening, and exit the main run loop
	 */
	public void kill(){
		setRunning(false);
		socket_m.close();
	}

	synchronized private boolean isRunning(){ return running; }
	synchronized private void setRunning(boolean b){ running = b; }
	/**
	 * Thread Entrypoint. Listens for data, deserializes, and calls onSchedulerEvent
	 */
	@Override
	public void run() {
		while(isRunning()) {
	        DatagramPacket pac = new DatagramPacket(buffer_ma, buffer_ma.length);
	        try {
	            socket_m.receive(pac);
	        } catch (IOException e) {
				if (running) e.printStackTrace();
	            continue;
	        }
	        byte[] trimmed = new byte[pac.getLength()];
	        System.arraycopy(buffer_ma, 0, trimmed, 0, pac.getLength());
	        SchedulerEvent s = Serializer.fromByteArray(trimmed, SchedulerEvent.class);
	        onSchedulerEvent(s, pac);
		}	
	}

	/**
	 *
	 * @param s The deserialized event we recieved
	 * @param src The datagram packet that delivered this data
	 */
	abstract public void onSchedulerEvent(SchedulerEvent s, DatagramPacket src);
}
