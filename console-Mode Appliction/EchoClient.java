import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class EchoClient extends JFrame {
    final static int thePort = 1234;
    Socket theSocket;
    BufferedReader in;
    PrintWriter out;
    JTextField theField = new JTextField();
    JTextArea theArea = new JTextArea();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EchoClient();
            }
        });
    }

    public EchoClient() {
        setTitle("Echo Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        add(theField, BorderLayout.NORTH);
        add(new JScrollPane(theArea), BorderLayout.CENTER); // Use JScrollPane for the text area
        theArea.setEditable(false);
        setSize(400, 300);
        setVisible(true);

        String host = "localhost"; // Change this to your server's host name if needed
        try {
            theSocket = new Socket(host, thePort);
            in = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(theSocket.getOutputStream()), true);

            theField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    out.println(theField.getText());
                    theField.setText("");
                }
            });

            startListening();
        } catch (IOException io) {
            System.out.println("IOException :\n" + io);
        }
    }

    public void startListening() {
        Thread listenerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        String s = in.readLine();
                        if (s != null) {
                            appendToArea(s + '\n');
                        }
                    }
                } catch (IOException io) {
                    System.err.println("IOException: \n" + io);
                }
            }
        });
        listenerThread.start();
    }

    // Append text to the JTextArea from any thread (thread-safe)
    public void appendToArea(String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                theArea.append(text);
            }
        });
    }
}
