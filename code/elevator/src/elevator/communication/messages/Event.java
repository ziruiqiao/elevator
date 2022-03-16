package elevator.communication.messages;

import elevator.communication.Distributor;

import java.io.Serializable;

public class Event implements Serializable {
    private final SystemComponent src;
    private final int srcId;
    private final int destId;

    public Event(SystemComponent src, int srcId, int destId) {
        this.src = src;
        this.srcId = srcId;
        this.destId = destId;
    }

    public SystemComponent getSrc() {
        return src;
    }

    public int getSrcId() {
        return srcId;
    }

    public int getDestId() {
        return destId;
    }

    @Override
    public String toString() {
        return "Event{" +
                "src=" + src +
                ", srcId=" + srcId +
                ", destId=" + destId +
                '}';
    }
}
