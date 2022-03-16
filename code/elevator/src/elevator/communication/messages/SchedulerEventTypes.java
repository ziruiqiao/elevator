package elevator.communication.messages;

public enum SchedulerEventTypes {
	OnDoorClose,
    OnDestinationSelected,
    OnFloorApproach,
    OnElevatorStop,
    OnFloorRequest,
    RequestData,
    StartUp,
    DetermineDistance,
    Ack
}
