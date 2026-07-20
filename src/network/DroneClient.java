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

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
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
}