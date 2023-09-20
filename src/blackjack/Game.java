package blackjack;

import java.io.IOException;
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
            if (roundWinner.getWinCount() >= 20) {
                throw new RuntimeException(roundWinner.getName() + " has won too many times ("+ roundWinner.getWinCount()+")!");
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

    public static void main(String[] args) throws InterruptedException, IOException {
        Game game = new Game();
        Socket socket = new Socket("localhost",6000);
        try{
            //Socket socket = new Socket("localhost",6000);
            for (int i = 0; i < 100; i++) {
                game.playRound();
                game.displayStatistics();
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
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();

              }
          }
        }
}
