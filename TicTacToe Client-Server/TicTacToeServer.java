import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TicTacToeServer extends JFrame {
    private byte[] board;
    private boolean xMove;
    private JTextArea output;
    private Player[] players;
    private ServerSocket server;
    private int currentPlayer;

    public TicTacToeServer() {
        super("TicTacToe Server");
        board = new byte[9];
        xMove = true;
        players = new Player[2];
        currentPlayer = 0;

        // set up ServerSocket
        try {
            server = new ServerSocket(5000, 2);
        } catch (IOException io) {
            io.printStackTrace();
            System.exit(1);
        }

        output = new JTextArea();
        getContentPane().add(new JScrollPane(output), BorderLayout.CENTER);
        output.setText("Server awaiting connections\n");

        setSize(500, 500);
    }

    // wait for two connections so the game can be played
    public void runServer() {
        for (int i = 0; i < players.length; i++) {
            try {
                players[i] = new Player(server.accept(), this, i);
                players[i].start();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        // Player X is suspended until Player 0 connects
        synchronized (players[0]) {
            players[0].threadSuspended = false;
            players[0].notify();
        }
    }

    public void display(String s) {
        output.append(s + "\n");
    }

    // Determine if a move is valid. This method is synchronized because only one move can be made at a time.
    public synchronized boolean validMove(int loc, int player) {
        boolean moveDone = false;

        while (player != currentPlayer) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!isOccupied(loc)) {
            board[loc] = (byte) (currentPlayer == 0 ? 'X' : 'O');
            currentPlayer = (currentPlayer + 1) % 2;
            players[currentPlayer].otherPlayerMoved(loc);
            notify(); // tell waiting player to continue

            return true;
        } else {
            return false;
        }
    }

    public boolean isOccupied(int loc) {
        return board[loc] == 'X' || board[loc] == 'O';
    }

    public boolean gameOver() {
        // place code here to test for a winner of the game
        return false;
    }

    public static void main(String[] args) {
        TicTacToeServer game = new TicTacToeServer();
        game.setVisible(true);
        game.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        game.runServer();
    }

    class Player extends Thread {
        private Socket connection;
        private DataInputStream input;
        private DataOutputStream output;
        private TicTacToeServer control;

        private int number;
        private char mark;
        protected boolean threadSuspended = true;

        public Player(Socket s, TicTacToeServer t, int num) {
            mark = (num == 0 ? 'X' : 'O');
            connection = s;

            try {
                input = new DataInputStream(connection.getInputStream());
                output = new DataOutputStream(connection.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            control = t;
            number = num;
        }

        public void otherPlayerMoved(int loc) {
            try {
                output.writeUTF("Opponent moved");
                output.writeInt(loc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            boolean done = false;
            try {
                control.display("Player " + (number == 0 ? 'X' : 'O') + " Connected");
                output.writeChar(mark);
                output.writeUTF("Player " + (number == 0 ? "X connected\n" : "O connected, please wait\n"));

                // wait for another player to arrive
                if (mark == 'X') {
                    output.writeUTF("Waiting for another player");

                    try {
                        synchronized (this) {
                            while (threadSuspended) {
                                wait();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    output.writeUTF("Other player connected. Your move");
                }

                // play game
                while (!done) {
                    int location = input.readInt();

                    if (control.validMove(location, number)) {
                        control.display("loc:" + location);
                        output.writeUTF("Valid Move");
                    } else {
                        output.writeUTF("Invalid move, try again");
                        if (control.gameOver()) {
                            done = true;
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
