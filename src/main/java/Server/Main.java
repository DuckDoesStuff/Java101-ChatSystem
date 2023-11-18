package Server;

public class Main {
    public static void main(String[] args) {
        ServerModule server = new ServerModule(4000);
        server.startServer();
    }
}
