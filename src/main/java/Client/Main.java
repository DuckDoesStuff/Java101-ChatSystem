package Client;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter your username: ");
//        String username = scanner.nextLine();
//        System.out.println("Enter your password: ");
//        String password = scanner.nextLine();

        //UserInfo user = new UserInfo(username);

        ClientModule client = new ClientModule("localhost", 1234, 5678);
        Thread clientThread = new Thread(client);
        clientThread.start();
    }
}
