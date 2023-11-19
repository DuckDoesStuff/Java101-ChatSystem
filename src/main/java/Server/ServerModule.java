package Server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;

import SampleDataStructure.PackageDataStructure;
class ClientHandler {
    AsynchronousSocketChannel clientSocket;
    SocketAddress serverAddress;
    ClientHandler(AsynchronousSocketChannel clientSocket) {
        this.clientSocket = clientSocket;
        try {
            serverAddress = clientSocket.getRemoteAddress();
        } catch (IOException e) {
            System.out.println("Error creating input stream");
            throw new RuntimeException(e);
        }
    }
    public void run() throws IOException {
        System.out.println("Client connected: " + clientSocket.getRemoteAddress());
        receivePackageData();
        PackageDataStructure packageData =
                new PackageDataStructure(
                        "From server",
                        "To client",
                        "Some random thing in content",
                        1234
                );
        sendPackageData(packageData);
    }

    public void receivePackageData() {
        final PackageDataStructure[] packageData = new PackageDataStructure[1];
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        clientSocket.read(buffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                System.out.println("Package data received from client");
                buffer.flip();
                packageData[0] = (PackageDataStructure) attachment;
                System.out.println("Package data: " + packageData[0]);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("Error receiving package data");
                throw new RuntimeException(exc);
            }
        });
    }

    public void sendPackageData(PackageDataStructure packageData) {
        byte[] data = packageData.data;
        ByteBuffer buffer = ByteBuffer.wrap(data);
        clientSocket.write(buffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                System.out.println("Sending package data to client");
                System.out.println("Package data: " + packageData);
                System.out.println("Package data sent to client");
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("Error sending package data");
                throw new RuntimeException(exc);
            }
        });
    }
}
public class ServerModule {
    AsynchronousServerSocketChannel serverSocket;
    ArrayList<Thread> clientThreads = new ArrayList<>();
    ServerModule(int port) {
        try {
            serverSocket = AsynchronousServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress("localhost", port));
        } catch (Exception e) {
            System.out.println("Error creating server socket: " + e.getMessage());
            throw new RuntimeException(e);
        }
        System.out.println("Server is now on port " + port);
    }
    public void startServer() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stopServer();
            System.out.println("Server closed");
        }));
        try {
            while(true) {
                serverSocket.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                    @Override
                    public void completed(AsynchronousSocketChannel completionResult, Object attachment) {
                        if(serverSocket.isOpen())
                            serverSocket.accept(null, this);

                        System.out.println("Client connected");
                        ClientHandler clientHandler = new ClientHandler(completionResult);
                        Thread clientThread = new Thread(() -> {
                            try {
                                clientHandler.run();
                            } catch (IOException e) {
                                System.out.println("Error handling client");
                                throw new RuntimeException(e);
                            }
                        });
                        clientThread.start();
                        clientThreads.add(clientThread);
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        System.out.println("Error accepting client connection");
                        throw new RuntimeException(exc);
                    }
                });
                System.in.read();
            }
        } catch (IOException e) {
            System.out.println("Error accepting client connection");
            throw new RuntimeException(e);
        }
    }

    public void stopServer() {
        for(Thread clientThread : clientThreads) {
            clientThread.interrupt();
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //Define other client methods here

}
