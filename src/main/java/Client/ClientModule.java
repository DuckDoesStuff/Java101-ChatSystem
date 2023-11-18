package Client;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class ClientModule {
    Socket socket;
    String host;
    int port;
    BufferedReader in;
    PrintWriter out;
    ClientModule(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.host = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        System.out.println("Connected to server: " + host + ":" + port);
    }
    //Define other client methods here
    public void receiveMessage() {
        String serverMessage;
        try {
            System.out.println("Waiting for message from server...");
            while(!in.ready()) {}
            System.out.println("Message received from server");

            serverMessage = in.readLine();
            System.out.println("Server " + host + ":" + port + " says: " + serverMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void sendMessage(String message) {
        System.out.println("Sending message: " + message);
        out.println(message);
    }
}
