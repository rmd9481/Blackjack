To run this software, start the BlackJackStart.java main method. This starts two separate processes,
    - HeartbeatReceive.java (1st)
    - Game.java (BlackJack Simulation) (2nd)

Components:: HeartbeatReceiver, Players, Game, BlackJackStart

Summary::

The process is designed to simulate the game of Black Jack, typically referred to as the game of “21”.
The program is designed to receive a heart beat from the active game of Black Jack.
A socket is instantiated in order to receive a message from the active game.
The program is designed to create two unique processes, the heart beat receiver and the game.
When the process running the game terminates for any known/unknown reason,
the heart beat reports the game Engine as being terminated.


Assignment 2: Fault Recovery Implementation

The project implement a passive redundancy method. The method implements to important pieces:
    - A Game Log and creating checkpoints @ every 5 plays (Terminal will display: "Checkpoint saved.")
    - A crash counter, to track the number of crashes that have occurred
             (Terminal will display: Crash count incremented and saved.")

Our team introduced a method save and read the checkpoint to a File (checkpoint.txt) and a method to do the same
for the crashes (crashCount.txt).

The Heartbeat.java and Game.java are recursive. So the process are able to restart, once the crash occurs. At the
moment the crash occurs, the following will be displayed in the terminal
    - "There is no Game engine present"
    - "Crash count incremented and saved."

Key Points:
The log files
    -checkpoint.txt and crashCount.txt are created if they don't exist.

The player and game statistics will continue to increment when the crash occurs. Processes will be continued from the
checkpoints. See Sample Below:

There is no Game engine present
Crash count incremented and saved.
Number of crashes so far: 2
------------------------
blackjack.Game Statistics:
Player1: 16 wins (23.53%)
Player2: 16 wins (23.53%)
Player3: 17 wins (25.00%)
Dealer: 19 wins (27.94%)
Checkpoint saved.
Received: beep
------------------------