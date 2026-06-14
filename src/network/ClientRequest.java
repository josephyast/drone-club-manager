package network;

import java.io.Serializable;

public class ClientRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Command command;
    private final Object data;

    public ClientRequest(Command command, Object data) {
        this.command = command;
        this.data = data;
    }

    public ClientRequest(Command command) {
        this.command = command;
        this.data = null;
    }

    public Command getCommand() {
        return command;
    }

    public Object getData() {
        return data;
    }
}
