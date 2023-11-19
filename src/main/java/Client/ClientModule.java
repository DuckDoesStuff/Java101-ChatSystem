package Client;

import SampleDataStructure.PackageDataStructure;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ClientModule {
    AsynchronousSocketChannel clientSocket;
    String host;
    int port;
    ClientModule(String host, int port) {
        try {
            clientSocket = AsynchronousSocketChannel.open();
            this.host = host;
            this.port = port;
            InetSocketAddress serverAddress = new InetSocketAddress(host, port);
            if(clientSocket.connect(serverAddress) == null) {
                System.out.println("Connected to server");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //Define other client methods here
    public void sendPackageData(PackageDataStructure packageData) {
        byte[] data = packageData.data;
        ByteBuffer buffer = ByteBuffer.wrap(data);
        clientSocket.write(buffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                System.out.println("Sending package data to server");
                System.out.println("Package data: " + packageData);
                System.out.println("Package data sent to server");
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("Error sending package data");
                throw new RuntimeException(exc);
            }
        });
    }
    public PackageDataStructure receivePackageData() {
        final PackageDataStructure[] packageData = new PackageDataStructure[1];
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        clientSocket.read(buffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer result, Object attachment) {
                System.out.println("Package data received from server");
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
        return packageData[0];
    }
}
