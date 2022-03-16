package elevator.communication;

import elevator.communication.messages.*;

import java.net.DatagramPacket;

/**
 * Works with Distributor to provide a non-blocking way of responding to data requests, inspired by nginx worker thread architecture.
 * <br>
 * Forwards other requests to the distributor scheduler queue.
 * @author cbains
 */
class ElevatorToScheduler extends DistributorListener {

	public ElevatorToScheduler(Distributor d, int port) {
		super(d, port);
	}

	@Override
	public void onSchedulerEvent(SchedulerEvent s, DatagramPacket src) {
		int sendingElevatorId = s.getSrcId();
		if (s.getCommand() == SchedulerEventTypes.RequestData) {
			this.distrib_m.sendElevatorEventNetwork(src, sendingElevatorId);
		} else {
			this.distrib_m.sendToScheduler(s);
			respond(src, new ElevatorEvent(SystemComponent.ELEVATORLISTENER, -1, sendingElevatorId, ElevatorEventTypes.Ack));
		}
	}
}
