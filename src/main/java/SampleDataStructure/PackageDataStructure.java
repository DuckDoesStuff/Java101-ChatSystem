package SampleDataStructure;

import java.io.Serial;
import java.io.Serializable;

public class PackageDataStructure implements Serializable {
    //Required for Serializable
    //To make sure both the client and the server can deserialize
    @Serial
    private static final long serialVersionUID = 1L;

    public String content;
    public int password;
    public PackageDataStructure(String content, int password) {
        this.content = content;
        this.password = password;
    }
    //For easy debugging, override toString() method to use in
    //System.out.println()
    @Override
    public String toString() {
        return "Content: " + content + "\nPassword: " + password;
    }
}
