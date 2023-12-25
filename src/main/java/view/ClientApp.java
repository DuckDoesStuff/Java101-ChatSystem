package view;
import Client.ClientModule;
import javax.swing.*;

class ClientUI {
    JFrame mainFrame;
    ClientModule clientModule;

    ClientUI(ClientModule clientModule) {
        this.clientModule = clientModule;
        if(clientModule.isConnected()) {
            mainFrame = new SignInUI();
        }
    }
}


public class ClientApp {
    public static void main(String[] args) {
        ClientModule clientModule = ClientModule.getInstance("localhost", 1234, 5678);
        Thread clientThread = new Thread(clientModule);
        clientThread.start();

        ClientUI clientUI = new ClientUI(clientModule);
    }
}
