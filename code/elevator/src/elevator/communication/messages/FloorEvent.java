package elevator.communication.messages;

import java.util.Arrays;

public class FloorEvent extends Event {


    private final FloorEventTypes command;
    private final byte[] data;

    public FloorEvent(SystemComponent src, int srcId, int destId, FloorEventTypes command, byte... data) {
        super(src, srcId, destId);
        this.command = command;
        this.data = data;
    }

    public FloorEvent(SystemComponent src, int srcId, int destId, FloorEventTypes command) {
        super(src, srcId, destId);
        this.command = command;
        this.data = new byte[0];
    }

    public byte[] getData() {
        return data;
    }

    public FloorEventTypes getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return super.toString() + ": FloorEvent{" +
                "command=" + command +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
