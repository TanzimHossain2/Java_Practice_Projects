import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

// package client-server Application;

public class Client extends JFrame {
    private JTextField enter;
    private JTextArea display;
    ObjectOutputStream output;
    ObjectInputStream input;
    String message = "" ;

    public Client(){
        super("Client");

        Container c = getContentPane();
        enter = new JTextField();
        enter.setEnabled(false);

        enter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                sendData(e.getActionCommand());
            }
        });

        c.add(enter, BorderLayout.NORTH);

        display = new JTextArea();
        c.add(new JScrollPane(display), BorderLayout.CENTER);
        setSize(500, 400);

        setVisible(true);
        show();
    }

    public void runClient(){
        Socket client;

        try{
            //step 1: Create a Socket to make connection
            display.setText("Attempting connection \n");
            client = new Socket(InetAddress.getByName("127.0.0.1"), 5000);

            display.append("Connected to :"+ client.getInetAddress().getHostName());

            //step 2: Get input & output stream
            output = new ObjectOutputStream(client.getOutputStream());
            output.flush();

            input= new ObjectInputStream(client.getInputStream());
            display.append("\nGot I/O streams \n");

            //step 3: Process connection
            enter.setEnabled(true);

            do{
                try{
                    message = (String) input.readObject();
                    display.append("\n"+ message);
                    display.setCaretPosition(display.getText().length());
                } catch(ClassNotFoundException cnfex){
                    display.append("\nUnknown object type received.");
                }
            } while(!message.equals("SERVER>>> TERMINATE"));

            //step 4: process connection
            display.append("Closing connection. \n");
            input.close();
            output.close();
            client.close();
            
        }catch(EOFException eof){
            System.out.println("Server terminated connection");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void sendData (String s){
        try {
            message = s;
            output.writeObject("CLIENT>>>"+s);
            output.flush();
            display.append("\nSERVER>>>"+s);
        } catch (IOException e) {
           display.append(
            "\n Error writing object"
           );
        }
    }

    public static void main(String[] args) {
        Client app = new Client();
        app.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });

        app.runClient();
    }

}
