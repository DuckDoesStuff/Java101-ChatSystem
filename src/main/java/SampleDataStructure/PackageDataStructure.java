package SampleDataStructure;

import java.io.Serial;
import java.io.Serializable;

public class PackageDataStructure implements Serializable {
    //Required for Serializable
    //To make sure both the client and the server can deserialize
    @Serial
    private static final long serialVersionUID = 1L;

    String from;
    String to;
    String content;
    int password;
    public PackageDataStructure(String from, String to, String content, int password) {
        this.from = from;
        this.to = to;
        this.content = content;
        this.password = password;
    }
    //For easy debugging, override toString() method to use in
    //System.out.println()
    @Override
    public String toString() {
        return "From: " + from + "\nTo: " + to + "\nContent: " + content + "\nPassword: " + password;
    }
}
