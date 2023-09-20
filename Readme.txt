To run this software, start the HeartbeatReceiver.java main method, and then run the Game.java main method.

Components:: HeartbeatReceiver, Players, Game

Summary::

The process is designed to simulate the game of Black Jack, typically referred to as the game of “21”.
The program is designed to receive a heart beat from the active game of Black Jack.
A socket is instantiated in order to receive a message from the active game.
The program is designed to create two unique processes, the heart beat receiver and the game.
When the process running the game terminates for any known/unknown reason,
the heart beat reports the game Engine as being terminated.
