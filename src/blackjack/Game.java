package blackjack;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
    private List<Player> players;
    private Player dealer;
    private List<String> winners;
    private int totalGames;
    private int crashCount;

    public Game() {
        players = new ArrayList<>();
        players.add(new Player("Player1"));
        players.add(new Player("Player2"));
        players.add(new Player("Player3"));
        dealer = new Player("Dealer");
        winners = new ArrayList<>();
        totalGames = 0;
    }

    public void playRound() throws InterruptedException {
        Random rand = new Random();

        // Reset scores
        for (Player player : players) {
            player.resetScore();
        }
        dealer.resetScore();

        // Deal first card to players and dealer
        for (Player player : players) {
            player.addScore(rand.nextInt(10) + 1);
        }
        dealer.addScore(rand.nextInt(10) + 1);

        // Players take turns to get more numbers until they have at least 15
        for (Player player : players) {
            while (player.getScore() < 15) {
                player.addScore(rand.nextInt(10) + 1);
            }
        }

        // Dealer gets numbers until they have 17 or more
        while (dealer.getScore() < 17) {
            dealer.addScore(rand.nextInt(10) + 1);
        }

        // Determine the winner
        int maxScore = 0;
        Player roundWinner = null;
        for (Player player : players) {
            if (player.getScore() > maxScore && player.getScore() <= 21) {
                maxScore = player.getScore();
                roundWinner = player;
            }
        }

        if (dealer.getScore() > maxScore && dealer.getScore() <= 21) {
            roundWinner = dealer;
        }

        if (roundWinner != null) {
            winners.add(roundWinner.getName());
            roundWinner.incrementWinCount();
            if (roundWinner.getWinCount() >= 15 + crashCount * 5) {
                RuntimeException runtimeException = new RuntimeException(roundWinner.getName() + " has won too many times ("+ roundWinner.getWinCount()+")!");
                runtimeException.printStackTrace();
                throw runtimeException;
            }
        }
        totalGames++;
    }

    public void displayStatistics() {
        System.out.println("------------------------");
        System.out.println("blackjack.Game Statistics:");
        for (Player player : players) {
            System.out.printf("%s: %d wins (%.2f%%)\n", player.getName(), player.getWinCount(), player.getWinPercentage(totalGames));
        }
        System.out.printf("%s: %d wins (%.2f%%)\n", dealer.getName(), dealer.getWinCount(), dealer.getWinPercentage(totalGames));
    }

    public void saveCrashCount(int count){

        try (PrintWriter writer = new PrintWriter("crashCount.txt", StandardCharsets.UTF_8)) {
            writer.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int loadCrashCount(){
        File file = new File("crashCount.txt");
        if (!file.exists()) return 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            return Integer.parseInt(reader.readLine());

        }catch (IOException e){
            e.printStackTrace();
        }

        return 0;
    }

    public void saveCheckpoint() {
        try (PrintWriter writer = new PrintWriter("checkpoint.txt", StandardCharsets.UTF_8)) {
            for (Player player : players) {
                writer.println(player.getName() + "," + player.getWinCount());
            }
            writer.println("Dealer," + dealer.getWinCount());
            writer.println(totalGames);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restoreFromCheckpoint() {
        File file = new File("checkpoint.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            for (Player player : players) {
                String[] parts = reader.readLine().split(",");
                player.setName(parts[0]);
                player.setWinCount(Integer.parseInt(parts[1]));
            }
            String[] dealerParts = reader.readLine().split(",");
            dealer.setName(dealerParts[0]);
            dealer.setWinCount(Integer.parseInt(dealerParts[1]));
            totalGames = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Socket connectToServer() {
        for(int i = 0; i < 5; i++) {
            try {
                Socket socket = new Socket("localhost",6000);
                return socket;
            } catch (IOException e) {
                System.out.println("Failed to connect, trying again in 5 seconds.");
                try {
                    Thread.sleep(5000);
                } catch(InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
        throw new RuntimeException("Could not connect to server after 5 attempts.");
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Game game = new Game();

        // Restore from the last checkpoint if it exists
        game.restoreFromCheckpoint();
        game.crashCount = game.loadCrashCount();

        System.out.println("Number of crashes so far: " + game.crashCount);

        Socket socket = null;
        try {
            socket = connectToServer();
            for (int i = 0; i < 100; i++) {
                game.playRound();
                game.displayStatistics();

                // Save the game's state after each round
                if(i % 5 == 0) {
                    game.saveCheckpoint();
                    System.out.println("Checkpoint saved.");
                }

                socket.getOutputStream().write("beep\n".getBytes(StandardCharsets.UTF_8));
                Thread.sleep(500);
            }
            game.displayStatistics();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                    game.crashCount++;
                    game.saveCrashCount(game.crashCount);
                    System.out.println("Crash count incremented and saved.");
                    Thread.sleep(5000);
                    main(null);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

}
