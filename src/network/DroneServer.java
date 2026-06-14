package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DroneServer {
    private static final int PORT = 8080;
    private boolean isRunning = true;

    public void start(){
        System.out.println("Starting Server on port " + PORT);

        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Server started. Waiting for clients...");
            while(isRunning){
                Socket clienctSocket = serverSocket.accept();
                System.out.println("Client connected: " + clienctSocket.getRemoteSocketAddress());

                ClientHandler handler = new ClientHandler(clienctSocket);
                Thread clientThread = new Thread(handler);
                clientThread.start();
            }
        } catch (IOException e){
            System.err.println("Server socket error: " + e.getMessage());
        }
    }

    public static void main(String[] args){
        DroneServer server = new DroneServer();
        server.start();
    }
}
