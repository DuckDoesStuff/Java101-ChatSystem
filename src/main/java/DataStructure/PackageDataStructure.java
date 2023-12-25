package DataStructure;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class PackageDataStructure implements Serializable {
    //Required for Serializable
    //To make sure both the client and the server can deserialize
    @Serial
    private static final long serialVersionUID = 1L;

    public ArrayList<String> content;
    //public int password;
    public PackageDataStructure(String content) {
        this.content = new ArrayList<>();
        if (!content.equals(""))
            this.content.add(content);
        //this.password = password;
    }

    public PackageDataStructure(String[] content){
        this.content = new ArrayList<>();
        for (String s : content) {
            this.content.add(s);
        }
    }

    public PackageDataStructure(ArrayList<String> content) {
        this.content = new ArrayList<>();
        this.content.addAll(content);
        //this.password = password;
    }
    //For easy debugging, override toString() method to use in
    //System.out.println()
    @Override
    public String toString() {
        return "Content: " + content ;
    }
}
