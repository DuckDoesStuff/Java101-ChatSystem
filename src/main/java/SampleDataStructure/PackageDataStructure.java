package SampleDataStructure;

import java.io.*;

public class PackageDataStructure {
    public static class Package implements Serializable {
        //Required for Serializable
        //To make sure both the client and the server can deserialize
        @Serial
        private static final long serialVersionUID = 1L;
        String from;
        String to;
        String content;
        int password;
        Package(String from, String to, String content, int password) {
            this.from = from;
            this.to = to;
            this.content = content;
            this.password = password;
        }
    }

    public byte[] data;
    public Package p;
    public PackageDataStructure(String from, String to, String content, int password) {
        p = new Package(from, to, content, password);
        data = serialize(p);
    }

    public PackageDataStructure(byte[] data) {
        this.data = data;
        p = deserialize(data);
    }

    private byte[] serialize(Package p) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos)){
            oos.writeObject(p);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Package deserialize(byte[] data) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis)){
            return (Package) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //For easy debugging, override toString() method to use in
    //System.out.println()
    @Override
    public String toString() {
        if (p != null)
            return "From: " + p.from + "\nTo: " + p.to + "\nContent: " + p.content + "\nPassword: " + p.password;
        else
            p = deserialize(data);
        return "From: " + p.from + "\nTo: " + p.to + "\nContent: " + p.content + "\nPassword: " + p.password;
    }


    //For testing only
    public static void main(String[] args) {
        PackageDataStructure packageData =
                new PackageDataStructure(
                        "From server",
                        "To client",
                        "Some random thing in content",
                        1234
                );
        System.out.println(packageData);
        PackageDataStructure packageData2 = new PackageDataStructure(packageData.data);
        System.out.println(packageData2);
    }
}
