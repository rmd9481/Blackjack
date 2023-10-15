import blackjack.Game;
import heartbeat.HeartbeatReceiver;

import java.io.IOException;
/*
* Run the following Class to run the Black Jack Simulation
*
*/
public class BlackJackStart {

    public static void main(String[] args) throws InterruptedException {
        // Thread that Run the Heartbeat Receiver
        Thread HeartThread = new Thread() {
            public void run() {

                try {
                    HeartbeatReceiver.main(null);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        };


        // Thread that Runs the Game Engine
        Thread GameThread = new Thread() {
            public void run() {
                try {
                    Game.main(null);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };


        // Start the downloads.
        HeartThread.start();
        GameThread.start();

        // Wait for them both to finish
        HeartThread.join();
        GameThread.join();

    }

}
