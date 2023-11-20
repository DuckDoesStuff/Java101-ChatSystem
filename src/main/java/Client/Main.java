package Client;

import SampleDataStructure.PackageDataStructure;

import java.io.IOException;
import java.nio.channels.ReadPendingException;

public class Main {
    public static void main(String[] args) {
        ClientModule client = new ClientModule("localhost", 4000);
        PackageDataStructure packageData =
                new PackageDataStructure(
                    "From client",
                    "To server",
                    "Some random thing in content",
                    1234
                );

        client.receivePackageData();
        client.sendPackageData(packageData);
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
