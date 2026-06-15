package network;

import network.dto.PilotDTO;
import java.util.concurrent.CountDownLatch;

public class MultiClientTest {
    public static void main(String[] args) {
        System.out.println("STARTING ADVANCED MULTI-CLIENT CONCURRENCY TEST");

        CountDownLatch latch = new CountDownLatch(1);

        Thread t1 = new Thread(() -> runMockClient("Client_1_Reader", Command.GET_ALL_DRONES, latch));
        Thread t2 = new Thread(() -> runMockClient("Client_2_Reader", Command.GET_ALL_PILOTS, latch));
        Thread t3 = new Thread(() -> runMockClient("Client_3_Writer", Command.ADD_PILOT, latch));
        Thread t4 = new Thread(() -> runMockClient("Client_4_Writer", Command.ADD_PILOT, latch));
        Thread t5 = new Thread(() -> runMockClient("Client_5_Reader", Command.GET_ALL_FLIGHTS, latch));

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        try {
            System.out.println("[Test Master] Threads are initializing and moving to starting line");
            Thread.sleep(2000);

            System.out.println("[Test Master] START");
            latch.countDown();
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            t5.join();

        } catch (InterruptedException e) {
            System.err.println("Main test thread interrupted: " + e.getMessage());
        }

        System.out.println("ADVANCED MULTI-CLIENT CONCURRENCY TEST COMPLETED");
    }

    private static void runMockClient(String clientName, Command command, CountDownLatch latch) {
        DroneClient client = new DroneClient();
        client.connect();

        try {
            latch.await();

            System.out.println("[" + clientName + "] Requesting: " + command);
            ClientRequest request;

            if (command == Command.ADD_PILOT) {
                String uniqueUser = "concurrent_user_" + System.nanoTime();
                PilotDTO newPilot = new PilotDTO(0, clientName, uniqueUser, "Pass123!", "BEGINNER", 0, 100.0, true);
                request = new ClientRequest(command, newPilot);
            } else {
                request = new ClientRequest(command, null);
            }

            ServerResponse response = client.sendRequest(request);

            if (response != null) {
                System.out.println("[" + clientName + "] RESULT -> Success: " + response.isSuccess() + " | " + response.getMessage());
            } else {
                System.out.println("[" + clientName + "] RESULT -> Error: Received null response from server.");
            }

        } catch (Exception e) {
            System.err.println("[" + clientName + "] Exception during execution: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }
}