package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try{
            this.out = new ObjectOutputStream(clientSocket.getOutputStream());
            this.in = new ObjectInputStream(clientSocket.getInputStream());

            String threadName = Thread.currentThread().getName();
            System.out.println("Handshake completed. Client assigned to " + threadName);

            while(true){
                Object input = in.readObject();

                if(input instanceof ClientRequest){
                    ClientRequest request = (ClientRequest) input;
                    System.out.println("[" + threadName + "] Received command: " + request.getCommand());

                    ServerResponse response = processRequest(request);

                    out.writeObject(response);
                    out.flush();
                }
            }
        } catch (IOException | ClassNotFoundException e){
            System.out.println("Client disconnected: " + Thread.currentThread().getName());
        } finally {
            closeResources();
        }
    }

    private ServerResponse processRequest(ClientRequest request){
        Command command = request.getCommand();

        switch (command){
            case LOGIN ->{
                return new ServerResponse(true, "Login successful");
            }
            case LOGOUT ->{
                return new ServerResponse(true, "Logout successful");
            }
            case REGISTER ->{
                return new ServerResponse(true, "Register successful");
            }
            default ->{
                return new ServerResponse(false, "Command '" + command + "' is recognized but not implemented yet.");            }
        }
    }

    private void closeResources(){
        try{
            if(in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            System.out.println("Resources successfully cleaned up for disconnected client.");
        } catch(IOException e){
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
}
