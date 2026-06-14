package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class DroneClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public void connect(){
        try{
            System.out.println("Connecting to server at " + HOST + ":" + PORT);
            this.socket = new Socket(HOST, PORT);

            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Connected to server.");

        }catch(IOException e){
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    public ServerResponse sendRequest(ClientRequest request){
        try{
            if (socket == null || socket.isClosed()) {
                System.err.println("Request failed: Not connected to server.");
                return null;
            }
            out.writeObject(request);
            out.flush();

            Object responseInput = in.readObject();
            if(responseInput instanceof ServerResponse){
                return (ServerResponse)responseInput;
            }
        } catch(IOException | ClassNotFoundException e){
            System.err.println("Communication error: " + e.getMessage());
        }
        return null;
    }

    public void disconnect(){
        try{
            if(in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            System.out.println("Disconnected from server.");
        } catch(IOException e){
            System.err.println("Disconnection error: " + e.getMessage());
        }
    }

    public static void  main(String[] args){
        DroneClient client = new DroneClient();
        client.connect();

        System.out.println("Sending LOGIN request...");
        ClientRequest loginRequest = new ClientRequest(Command.LOGIN);
        ServerResponse loginResponse = client.sendRequest(loginRequest);
        if(loginResponse != null){
            System.out.println("Server Success Status: " + loginResponse.isSuccess());
            System.out.println("Server Message: "+loginResponse.getMessage());

        }

        System.out.println("Sending GET_ALL_DRONES request...");
        ClientRequest droneRequest = new ClientRequest(Command.GET_ALL_DRONES);
        ServerResponse droneResponse = client.sendRequest(droneRequest);
        if(droneResponse != null){
            System.out.println("Server Success Status: " + droneResponse.isSuccess());
            System.out.println("Server Message: "+droneResponse.getMessage());
        }

        System.out.println("Sending Unimplemented Command");
        ClientRequest pilotRequest = new ClientRequest(Command.ADD_PILOT);
        ServerResponse pilotResponse = client.sendRequest(pilotRequest);
        if (pilotResponse != null) {
            System.out.println("Server Success Status: " + pilotResponse.isSuccess());
            System.out.println("Server Message: " + pilotResponse.getMessage());
        }

        System.out.println();
        client.disconnect();
    }
}
