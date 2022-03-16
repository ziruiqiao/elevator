package elevator.communication.messages;

public enum ElevatorEventTypes {
	OpenDoorCommand,
    DoorCloseCommand,
    MoveMotorCommand,
    AtDestinationFloor,
    ContinueMoving,
    FloorButtonPressed,
    DistanceDetermined,
    Ack
}
