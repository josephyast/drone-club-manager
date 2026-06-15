package network;

import network.dto.DroneDTO;
import java.util.concurrent.CountDownLatch;

public class RaceConditionTest {

    public static void main(String[] args) {
        System.out.println("STARTING REAL TIME RACE CONDITION TEST");

        CountDownLatch latch = new CountDownLatch(1);
        Thread clientA = new Thread(() -> sendUpdate("Client_A_Speedy", "Drone_Model_ALPHA", latch));
        Thread clientB = new Thread(() -> sendUpdate("Client_B_Flash", "Drone_Model_BETA", latch));

        clientA.start();
        clientB.start();

        try {
            System.out.println("[Race Master] Clients are warming up their connections");
            Thread.sleep(1500);

            System.out.println("[Race Master] Both clients firing UPDATE simultaneously!");
            latch.countDown();


            clientA.join();
            clientB.join();

        } catch (InterruptedException e) {
            System.err.println("Race Master thread interrupted: " + e.getMessage());
        }

        System.out.println("RACE CONDITION TEST COMPLETED ");
    }

    private static void sendUpdate(String clientName, String newmodelName, CountDownLatch latch) {
        DroneClient client = new DroneClient();
        client.connect();

        try {
            latch.await();

            DroneDTO droneDTO = new DroneDTO(
                    1, newmodelName, "TOOTHPICKS", 1.5, true, "2026-06-14", "2026-06-14", 3600, 2.4
            );

            ClientRequest request = new ClientRequest(Command.UPDATE_DRONE, droneDTO);

            System.out.println("[" + clientName + "]Sending UPDATE_DRONE to Model: " + newmodelName);
            ServerResponse response = client.sendRequest(request);

            if (response != null) {
                System.out.println("[" + clientName + "] RESPONSE -> Success: " + response.isSuccess() + " | " + response.getMessage());
            }

        } catch (Exception e) {
            System.err.println("[" + clientName + "] Error during race: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }
}