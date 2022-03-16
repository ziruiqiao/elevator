# SYSC 3303 Elevator Project Group 11 (Project Iteration 3)

## Team Members

| Name                | Student Number |
| ------------------- | -------------- |
| Clark Bains         | 101149052      |
| Callum MacLeod      | 101109519      |
| Badral Khurelbaatar | 101166852      |
| David Katz          | 101157096      |
| Zirui Qiao          | 101100225      |

## File Names:

### Elevator/src/elevator

- **/Floor**
  - ElevatorFloor.java
  - PassengerRequest.java
  - FloorThread.java
  - FloorLauncher.java
- **/communication**

  - Distributor.java
  - DistributorListener.java
  - ElevatorToScheduler.java
  - FloorToScheduler.java
  - **/messages**
    - Event.java
    - SystemComponent.java
    - ElevatorEvent.java
    - ElevatorEventTypes.java
    - SchedulerEvent.java
    - SchedulerEventTypes.java
    - FloorEvent.java
    - FloorEventTypes.java

- **/elevator**

  - ArrivalSensor.java
  - Direction.java
  - Door.java
  - Elevator.java
  - ElevatorLamp.java
  - ElevatorState.java
  - ElevatorStateSingleton.java
  - Motor.java
  - **/states**
    - AtADestinationFloorState.java
    - DoorClosedState.java
    - IdleState.java
    - MovingState.java
    - NearFloorState.java
    - PrepareToLeaveState.java

- **/scheduler**

  - Scheduler.java
  - ElevatorQueue.java
  - SchedulerState.java
  - SchedulerStateSingleton.java
  - SchedulerCommunication.java
  - SchedulerLauncher.java
  - SuperScheduler_DetermineDirection.java
  - SuperScheduler.java

  - **/states**
    - Idle.java
    - Moving.java
    - PendingDoorClose.java
    - PendingStop.java

- **/util**

  - StateSingleton.java
  - Sleep.java
  - Serializer.java

- ElevatorAndFloorTestApplication.java

### Elevator/src/tests

- EventTest.java
  - Runs a test for the Events.java class
- ElevatorQueue.java
  - Runs a test for the ElevatorQueue.java class
- DistributorTest.java
  - Tests the distribution object

### Elevator/docs/UML

- UMLClass_Iter3.pdf
- Sequence_RequestDestination_Iter3.pdf
- Sequence_FloorRequest_Iter3.pdf

### Elevator/docs/FSMs

- SchedulerFSM.pdf
- ElevatorFSM.pdf

### Elevator/docs/javadoc

- index.html

## Network Requirements (For testing over the network)

NAT Traversal works as expected, so no additional configuration is required on the network running ElevatorAndFloorTestApplication.

- ALLOW 0.0.0.0/0 UDP to host running SchedulerLauncher on port 4444
- ALLOW 0.0.0.0/0 UDP to host running SchedulerLauncher on port 3333

## Iteration Marking Instructions:

### Set up Instructions:

1. Unzip Elevator into eclipse workspace
2. Go to the File >> import menu in eclipse
3. Expand the General folder in the import wizard, and select "Projects from Folder or Archive"
4. Select Directory as the import Source
5. Select the code/elevator subfolder
6. Install the "ANSI Escape in Console" plugin to eclipse [found here](https://marketplace.eclipse.org/content/ansi-escape-console)
7. Click on Finish

### Running Instructions:

1. Right click and Run as java application for elevator.scheduler.SchedulerLauncher.java (This runs the scheduler component)
2. Right click and Run as java application for elevator.ElevatorAndFloorTestApplication.java (This runs the elevator and floor component)
3. Have great day :D

### Testing Instructions:

1. Open the Run Configurations Dialogue
2. Run the "Test" configuration, under JUnit
3. Ensure that all the tests have ran successfully
4. Have great day :D

## Breakdown of Responsibilies:

- **Clark:**
  - Researched then architected the network communication as a drop-in replacement for existing distribution system.
  - Helped Architect/implement the Floor scaling system
  - Helped test system with data traversal over the open internet.
- **Callum:**
  - Worked on Scheduler Scaling (SuperScheduler)
  - Devised Algorithm to determine optimal Elevator to dispatch to pickup passengers
  - Helped test system with data traversal over the open internet
  - Created the sequence diagrams
- **Badral:**
  - Drew the UML Diagram
  - Worked on Elevator and Floor Scaling
- **David:**
  - Reformatted elevator print statements
  - Created Google Slides for lab presentation (will be shown during lab meeting)
- **Zirui:**
  - Helped with the architecture of the network communication system
  - Implemented most of the network communication system
