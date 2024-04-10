import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TicTacToeClient extends JPanel implements Runnable {
    private JTextArea display;
    private JPanel boardPanel1, pane12;
    private Square board[][];
    private Square currentSquare;
    private Socket connection;
    private DataInputStream input;
    private DataOutputStream output;
    private Thread outputThread;
    private char myMark;
    private boolean myTurn;

    // set up user-interface and board
    public void init() {
        setLayout(new BorderLayout());

        display = new JTextArea(4, 30);
        display.setEditable(false);
        add(new JScrollPane(display), BorderLayout.SOUTH);

        boardPanel1 = new JPanel(new GridLayout(3, 3));
        boardPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        board = new Square[3][3];

        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {
                board[row][column] = new Square(' ', row * 3 + column);
                boardPanel1.add(board[row][column]);
            }
        }

        add(boardPanel1, BorderLayout.CENTER);
    }

    // make connection to server and get associated streams
    public void start() {
        try {
            connection = new Socket(InetAddress.getByName("127.0.0.1"), 5000);
            input = new DataInputStream(connection.getInputStream());
            output = new DataOutputStream(connection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputThread = new Thread(this);
        outputThread.start();
    }

    // control thread that allows continuous update of display
    public void run() {
        try {
            myMark = input.readChar();
            display.append("You are player \"" + myMark + "\"\n");
            myTurn = (myMark == 'X');
        } catch (IOException e) {
            e.printStackTrace();
        }

        // receive messages sent to client and output them
        while (true) {
            try {
                String s = input.readUTF();
                processMessage(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // process messages sent to client
    public void processMessage(String s) {
        if (s.equals("Valid move.")) {
            myTurn = false;
        } else if (s.equals("Invalid move, try again")) {
            display.append(s + '\n');
            myTurn = true;
        } else if (s.equals("Opponent moved")) {
            try {
                int loc = input.readInt();
                board[loc / 3][loc % 3].setMark(myMark == 'X' ? 'O' : 'X');
                board[loc / 3][loc % 3].repaint();
                display.append("Opponent moved. Your turn.\n");
                myTurn = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (s.startsWith("BoardState")) {
            String[] parts = s.split(":");
            String[] boardState = parts[1].split(",");
            for (int i = 0; i < boardState.length; i++) {
                char mark = boardState[i].charAt(0);
                board[i / 3][i % 3].setMark(mark);
                board[i / 3][i % 3].repaint();
            }
        } else {
            display.append(s + '\n');
            display.setCaretPosition(display.getText().length());
        }
    }

    public void sendClickedSquare(int loc) {
        if (myTurn) {
            try {
                output.writeInt(loc);
                myTurn = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Maintains one square on the board
    class Square extends JPanel {
        private char mark;
        private int location;

        public Square(char m, int loc) {
            mark = m;
            location = loc;
            setPreferredSize(new Dimension(100, 100));
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setOpaque(true);
            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (myTurn && mark == ' ') {
                        setMark(myMark);
                        sendClickedSquare(location);
                    }
                }
            });
        }

        public void setMark(char c) {
            mark = c;
            repaint();
        }

        public int getSquareLocation() {
            return location;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (mark == 'X') {
                g.setColor(Color.RED);
                g.drawLine(20, 20, getWidth() - 20, getHeight() - 20);
                g.drawLine(getWidth() - 20, 20, 20, getHeight() - 20);
            } else if (mark == 'O') {
                g.setColor(Color.BLUE);
                g.drawOval(20, 20, getWidth() - 40, getHeight() - 40);
            }
        }
    }

    // Add a main method
    public static void main(String[] args) {
        // Create an instance of TicTacToeClient
        TicTacToeClient tttClient = new TicTacToeClient();
        tttClient.init(); // Initialize the applet
        tttClient.start(); // Start the applet

        // Create a JFrame to contain the TicTacToeClient instance
        JFrame frame = new JFrame("Tic Tac Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(tttClient); // Add the TicTacToeClient instance to the frame
        frame.pack(); // Size the frame based on its content
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true); // Make the frame visible
    }
}
