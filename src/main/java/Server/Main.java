package Server;

public class Main {
    public static void main(String[] args) {
        ServerModule server = new ServerModule(1234, 5678);
        server.startServer();
    }
}
