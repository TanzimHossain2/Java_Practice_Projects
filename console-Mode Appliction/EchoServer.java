import java.net.*;
import java.io.*;


public class EchoServer {
    final static int thePort = 1234;
    ServerSocket theServerSocket;
    public static void main(String[] args) {
        EchoServer theApp = new EchoServer();
        theApp.run();
    }

    public EchoServer(){
        try {
            theServerSocket = new ServerSocket(thePort);
           
        } catch (IOException io) {
            System.err.println("Socket error: terminating");
            System.exit(1);
        }
    }

    public void run(){
        try {
            System.out.println("Server ready.");
            Socket theSocket = theServerSocket.accept();
            System.out.println("Client connected");

            BufferedReader in = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(theSocket.getOutputStream()), true);

            while (true) {
                String line = in.readLine();
                if(line==null) break;
                System.out.println("Read:"+ line);
                line = line.toUpperCase() + "!";
                out.println(line);
                System.out.println("Wrote:"+line);
            }

            in.close();
            out.close();
            theSocket.close();
            theServerSocket.close();
            System.exit(0);

        } catch (IOException io) {
            System.out.println("Socket error: terminating");
            System.exit(1);
        } catch(Exception e){
            System.out.println("Something went wrong");
        }
    }
}
