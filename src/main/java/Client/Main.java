package Client;

public class Main {
    public static void main(String[] args) {
        ClientModule client = new ClientModule("localhost", 4000);
        client.sendMessage("Hello, world!");
        client.receiveMessage();
    }
}
