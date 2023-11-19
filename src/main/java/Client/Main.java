package Client;

import SampleDataStructure.PackageDataStructure;

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

        client.sendPackageData(packageData);
        PackageDataStructure pd = client.receivePackageData();
        System.out.println("Received from server: " + pd);
    }
}
