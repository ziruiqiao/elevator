package elevator.communication;

import java.net.DatagramPacket;

import elevator.communication.messages.*;

/**
 * @author Zirui
 */
class FloorToScheduler extends DistributorListener {
	
	public FloorToScheduler(Distributor d, int port) {
		super(d, port);
	}

	@Override
	public void onSchedulerEvent(SchedulerEvent s, DatagramPacket src) {
		if (s.getCommand() == SchedulerEventTypes.RequestData) {
			throw new IllegalArgumentException("Floor should not request data!!");
		} else {
			this.distrib_m.sendToScheduler(s);
			respond(src, new FloorEvent(SystemComponent.FLOORLISTENER, -1, s.getSrcId(), FloorEventTypes.ACK));
		}
	}
}
