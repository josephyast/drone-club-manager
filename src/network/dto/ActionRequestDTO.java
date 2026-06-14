package network.dto;

import java.io.Serializable;

public class ActionRequestDTO {
    private static final long serialVersionUID = 1L;

    private int targetId;
    private int associatedId;
    private double value;

    public ActionRequestDTO(int targetId, int associatedId) {
        this.targetId = targetId;
        this.associatedId = associatedId;
    }

    public ActionRequestDTO(int targetId, int associatedId, double value) {
        this.targetId = targetId;
        this.associatedId = 0;
        this.value = value;
    }

    public int getTargetId() {
        return targetId;
    }

    public int getAssociatedId() {
        return associatedId;
    }

    public double getValue() {
        return value;
    }
}
