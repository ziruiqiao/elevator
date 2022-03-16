package elevator.communication.messages;

import java.util.Arrays;

public class SchedulerEvent extends Event {
    private final SchedulerEventTypes command;
    private final byte[] data;


    public SchedulerEvent(SystemComponent src, int srcId, int destId, SchedulerEventTypes command, byte... data) {
        super(src, srcId, destId);
        this.command = command;
        this.data = data;
    }

    public SchedulerEvent(SystemComponent src, int srcId, int destId, SchedulerEventTypes command) {
        super(src, srcId, destId);
        this.command = command;
        this.data = new byte[0];
    }

    public byte[] getData() {
        return data;
    }

    public SchedulerEventTypes getCommand() {
        return command;
    }
    
    @Override
    public String toString() {
        return super.toString() + ", SchedulerEvent{" +
                "command=" + command +
                ", data=" + Arrays.toString(data) +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchedulerEvent that = (SchedulerEvent) o;
        return command == that.command && Arrays.equals(data, that.data);
    }

}
