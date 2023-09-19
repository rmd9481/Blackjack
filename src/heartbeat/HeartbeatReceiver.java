package heartbeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class HeartbeatReceiver extends Thread {

    public void receive() {
        try(ServerSocket serverSocket = new ServerSocket(6000)) {
            Socket gameSocket = serverSocket.accept();
            BufferedReader input = new BufferedReader(new InputStreamReader(gameSocket.getInputStream()));
            String inputString;
            while ((inputString = input.readLine()) != null) {
                System.out.println("Received: " + inputString);
            }
            System.out.println("Game Died.");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        HeartbeatReceiver heartbeatReceiver = new HeartbeatReceiver();
        heartbeatReceiver.receive();
    }
}
