package network;

import java.util.List;

public class NetworkManager {

    private int port = 8080;
    private String host = "localhost";
    private boolean isRunning = false;


    public void StartServer() {
        this.isRunning = true;
        System.out.println("Server started on " + host + ":" + port);
    }

    public void StopServer() {
        this.isRunning = false;
        System.out.println("Server stopped.");
    }


    public void broadcastFrequencyList(List<String> frequencies) {
    }

    public boolean handleFrequencyChangeRequest(String pilotId, String Frequency) {
        return true;
    }

    public void releaseFrequency(String pilotId) {
    }
}

