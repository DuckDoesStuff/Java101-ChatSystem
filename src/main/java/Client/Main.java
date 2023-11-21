package Client;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        ClientModule client = new ClientModule("localhost", 4000, username);
        Thread clientThread = new Thread(client);
        clientThread.start();
    }
}
