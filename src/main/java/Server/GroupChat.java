package Server;

import java.util.ArrayList;

public class GroupChat {
    ArrayList<String> groupMembers;
    private String groupName;
    public GroupChat(String groupName){
        groupMembers = new ArrayList<String>();
        this.groupName = groupName;
    }

    public String getGroupName(){
        return groupName;
    }
    public void addMember(String username){
        groupMembers.add(username);
    }
}
