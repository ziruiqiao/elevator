package elevator.communication.messages;

import java.io.Serializable;
import java.util.Arrays;

public class ElevatorEvent extends Event {
    private final ElevatorEventTypes command;
    private final byte[] data;

    public ElevatorEvent(SystemComponent src, int srcId, int destId, ElevatorEventTypes command, byte... data) {
        super(src, srcId, destId);
        this.command = command;
        this.data = data;
    }

    public ElevatorEvent(SystemComponent src, int srcId, int destId, ElevatorEventTypes command) {
        super(src, srcId, destId);
        this.command = command;
        this.data = new byte[0];
    }


    public byte[] getData() {
        return data;
    }

    public ElevatorEventTypes getCommand() {
        return command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElevatorEvent that = (ElevatorEvent) o;

        if (command != that.command) return false;
        return Arrays.equals(data, that.data);
    }


}
