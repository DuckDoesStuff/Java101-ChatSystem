package view;

import Client.ClientModule;
import DataStructure.PackageDataStructure;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

class User {
    int id;
    String name;
    String status;

    public User(int id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }
}

public class Chat extends JFrame {
    ClientModule clientModule;

    private final JPanel chatN_field;
    private final JLabel chatN_jlb;
    private final JLabel subChatN_jlb;
    private final JPanel buttons_field;
    private final JPanel bottom_field;
    private JButton addFriend_btn;
    private JButton unfriend_btn;
    private JButton block_btn;
    private JButton spam_btn;
    private JButton deleteHst_btn;
    private JButton newGroup_btn;
    private JTextField newName_jtf;
    private JButton changeN_btn;
    private JButton addPerson_btn;
    private JButton addAdmin_btn;
    private JButton deletePerson_btn;
    private JTextField searchMsg_jtf;
    private JButton search_btn;
    private JButton searchAll_btn;
    private JButton leaveG_btn;
    private JTextField inputChat_jtf;
    private JEditorPane mainChat;
    private JPanel buttonPanel;

    private String mainChatName;

    public ArrayList<User> getUsers() {
        System.out.println("Getting friends list get from server");
        ArrayList<String> friends = clientModule.getFriendList();
        System.out.println("Friends list get from server");
        ArrayList<String> friendsStatus = clientModule.getFriendStatus(friends);
        System.out.println("Friends status get from server");
        System.out.println("All friends in4 get from server");
        ArrayList<User> userlist = new ArrayList<>();
        //int id = 0;
        for (int i = 0; i < friends.size(); i++)
        {
            System.out.println(friends.get(i) + " " + friendsStatus.get(i));
            userlist.add(new User(0, friends.get(i), friendsStatus.get(i).equals("true") ? "ON" : "OFF" ));
        }

        return userlist;
    }


    public Chat() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        clientModule = ClientModule.getInstance("localhost", 4000, 3000);
        new Thread(new listenForMessage()).start();
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        setSize(1000,740);
        setTitle("Chat");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                clientModule.closeConnection();
            }
        });

        //LEFT AREA
        JPanel leftArea = new JPanel(null);
        leftArea.setBounds(0, 0, 250, 700);
        leftArea.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));

        //topPart(in left area - title and searching)
        //frame
        JPanel topPart = new JPanel(null);
        topPart.setBounds(0, 0, 249, 126);
        topPart.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        topPart.setBackground(new Color(227, 235, 240));
        leftArea.add(topPart);
        //title
        JLabel title = new JLabel("Chats");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setBounds(10, 0, 200, 60);
        topPart.add(title);
        //find user
        JTextField inputUser_jtf = new JTextField(3);
        inputUser_jtf.setFont(new Font("Arial", Font.PLAIN, 14));
        inputUser_jtf.setBounds(10, 65, 150, 30);
        topPart.add(inputUser_jtf);

        JButton findUser_btn = new JButton("Find");
        //JButton findUser_btn = new RoundedButton("Find", 70, 30, 20, true, Color.BLACK);
        findUser_btn.setBackground(Color.WHITE);
        findUser_btn.setBounds(170, 65, 70, 30);
        topPart.add(findUser_btn);
        findUser_btn.addActionListener(e -> {
            //call function searching user
        });

        //midPart(in left area - user list)
        //frame
        JPanel midPart = new JPanel(null);
        midPart.setBounds(0, 126, 249, 500);
        midPart.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        midPart.setBackground(new Color(227, 235, 240));
        leftArea.add(midPart);
        //Choose filter
        String[] statusL = {"All", "Online list", "Offline list"};
        JComboBox status = new JComboBox(statusL);
        status.setBounds(10, 10, 170, 20);
        status.setFont(new Font("Arial", Font.PLAIN, 14));
        midPart.add(status);

        JButton filter_btn = new JButton("OK");
        filter_btn.setBackground(Color.WHITE);
        filter_btn.setBounds(185, 10, 50, 20);
        filter_btn.setFont(new Font("Arial", Font.BOLD, 11));
        midPart.add(filter_btn);

        //user list
//        JPanel usersField = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        usersField.setBounds(0, 50, 249, 380);
//        usersField.setBackground(new Color(217, 217, 217));

        ArrayList<User> users = getUsers();
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        for (User user: users) {
            JButton avatar = new JButton(user.name);
            Dimension buttonSize = new Dimension(228, 60);
            avatar.setPreferredSize(buttonSize);
            avatar.setMinimumSize(buttonSize);
            avatar.setMaximumSize(buttonSize);
            avatar.setBackground(new Color(156, 156, 156));
            buttonPanel.add(avatar);

            // Change right side
            avatar.addActionListener(e -> {
                System.out.println(avatar.getText());
                mainChatName = avatar.getText();
                for (User selectedOne: users) {
                    if (Objects.equals(selectedOne.name, avatar.getText())) {
                        System.out.println(selectedOne.id);
                        displayRightSide(selectedOne.id);
                        displayChatName(selectedOne.name);
                    }
                }
                mainChat.setText("");
                ArrayList<String> history = clientModule.getChatHistory(avatar.getText());
                HTMLDocument htmlDocument = (HTMLDocument) mainChat.getDocument();
                HTMLEditorKit editorKit = (HTMLEditorKit) mainChat.getEditorKit();
                for (String msg: history){
                    String[] split = msg.split(",");
                    if (Objects.equals(split[0], clientModule.getUsername())){
                        String message = "<div style='text-align: right;'>" + split[1] + "\n" + "</div>";
                        if (!msg.isEmpty()) {
                            try {
                                editorKit.insertHTML(htmlDocument, htmlDocument.getLength(), message, 0, 0, null);
                            } catch (BadLocationException ex) {
                                throw new RuntimeException(ex);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    } else {
                        String message = "<div style='text-align: left;'>" + split[1] + "\n" + "</div>";
                        if (!msg.isEmpty()) {
                            try {
                                editorKit.insertHTML(htmlDocument, htmlDocument.getLength(), message, 0, 0, null);
                            } catch (BadLocationException ex) {
                                throw new RuntimeException(ex);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                }
            });
        }

        JScrollPane listuser = new JScrollPane(buttonPanel);
        listuser.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        listuser.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listuser.setBounds(0, 50, 249, 450);

        midPart.add(listuser);

        //bottomPart(in left area - logout)
        //frame
        JPanel bottomPart = new JPanel(null);
        bottomPart.setBounds(0, 627, 249, 75);
        bottomPart.setBackground(new Color(227, 235, 240));
        leftArea.add(bottomPart);
        //logout_btn
        JButton logout_btn = new RoundedButton("Logout", 100, 40, 5, true, Color.BLACK);
        logout_btn.setBounds(70, 20, 100, 40);
        logout_btn.setFont(new Font("Arial", Font.BOLD, 15));
        logout_btn.setBackground(new Color(226, 142, 142));
        bottomPart.add(logout_btn);



        //CENTER AREA
        //frame
        JPanel centerArea = new JPanel(null);
        centerArea.setBounds(251, 0, 500, 700);

        //chatN (in center area - name of chat)
        chatN_field = new JPanel(null);
        chatN_field.setBounds(0, 0, 500, 75);
        chatN_field.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        chatN_field.setBackground(new Color(227, 235, 240));

        chatN_jlb = new JLabel();
        chatN_jlb.setFont(new Font("Arial", Font.BOLD, 20));
        chatN_jlb.setBounds(10, 0, 249, 60);
        chatN_field.add(chatN_jlb);
        centerArea.add(chatN_field);


        //chatField (in center area - field of messages)
        //frame
        mainChat = new JEditorPane();
        mainChat.setEditable(false);
        mainChat.setBackground(new Color(227, 235, 240));
        mainChat.setContentType("text/html");

        JScrollPane talkPane = new JScrollPane(mainChat,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        talkPane.setBounds(0, 76, 500, 550);
        talkPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        centerArea.add(talkPane);

        //inputMsg (in center area - input and send message)
        //frame
        JPanel input_field = new JPanel(null);
        input_field.setBounds(0, 627, 500, 75);
        input_field.setBackground(new Color(227, 235, 240));
        centerArea.add(input_field);
        //input
        inputChat_jtf = new JTextField(3);
        inputChat_jtf.setFont(new Font("Arial", Font.PLAIN, 14));
        inputChat_jtf.setBounds(10, 10, 400, 30);
        input_field.add(inputChat_jtf);

        JButton send_btn = new JButton("Send");
//        JButton send_btn = new RoundedButton("Send", 70, 30, 20, true, Color.BLACK);
        send_btn.setBackground(Color.WHITE);
        send_btn.setBounds(420, 10, 70, 30);
        send_btn.addActionListener(e -> {
            try {
                //listenForMessage();
                sendMessage();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            }
            inputChat_jtf.setText("");
        });
        input_field.add(send_btn);

        //RIGHT AREA
        JPanel rightArea = new JPanel(null);
        rightArea.setBounds(752, 0, 250, 700);
        rightArea.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK));
        rightArea.setBackground(new Color(227, 235, 240));

        //nameChat (in right area)
        subChatN_jlb = new JLabel();
        subChatN_jlb.setFont(new Font("Arial", Font.PLAIN, 20));
        subChatN_jlb.setBounds(10, 0, 249, 60);
        rightArea.add(subChatN_jlb);

        //buttons field (in right area)
        buttons_field = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons_field.setBounds(1, 100, 240, 400);
        buttons_field.setBackground(new Color(227, 235, 240));
        rightArea.add(buttons_field);

        //bottom field ((in right area)
        bottom_field = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom_field.setBounds(1, 550, 240, 150);
        bottom_field.setBackground(new Color(227, 235, 240));
        rightArea.add(bottom_field);


        add(leftArea);
        add(centerArea);
        add(rightArea);

        setVisible(true);

        //Handling the filtering of user lists
        filter_btn.addActionListener(e -> {
            final ArrayList<User> usersUpdated = getUsers();
            displayChatName("TVA - 123");
            //call function display user
            String selectedStatus = (String) status.getSelectedItem();

            if (Objects.equals(selectedStatus, "All")) {
                System.out.println("Selected status: " + selectedStatus);
                buttonPanel.removeAll();
                buttonPanel.revalidate();
                buttonPanel.repaint();

                for (int i = 0; i < usersUpdated.size(); i++) {
                    JButton avatar = new JButton(usersUpdated.get(i).name);
                    Dimension buttonSize = new Dimension(228, 60);
                    avatar.setPreferredSize(buttonSize);
                    avatar.setMinimumSize(buttonSize);
                    avatar.setMaximumSize(buttonSize);
                    avatar.setBackground(new Color(156, 156, 156));
                    buttonPanel.add(avatar);
                }

            } else if (Objects.equals(selectedStatus, "Online list")) {
                System.out.println("Selected status: " + selectedStatus);
                buttonPanel.removeAll();
                buttonPanel.revalidate();
                buttonPanel.repaint();

                for (int i = 0; i < usersUpdated.size(); i++) {
                    if (Objects.equals(usersUpdated.get(i).status, "ON")) {
                        JButton avatar = new JButton(usersUpdated.get(i).name);
                        Dimension buttonSize = new Dimension(228, 60);
                        avatar.setPreferredSize(buttonSize);
                        avatar.setMinimumSize(buttonSize);
                        avatar.setMaximumSize(buttonSize);
                        avatar.setBackground(new Color(156, 156, 156));
                        buttonPanel.add(avatar);
                    }
                }
            } else {
                System.out.println("Selected status: " + selectedStatus);
                buttonPanel.removeAll();
                buttonPanel.revalidate();
                buttonPanel.repaint();

                for (int i = 0; i < usersUpdated.size(); i++) {
                    if (Objects.equals(usersUpdated.get(i).status, "OFF")) {
                        JButton avatar = new JButton(usersUpdated.get(i).name);
                        Dimension buttonSize = new Dimension(228, 60);
                        avatar.setPreferredSize(buttonSize);
                        avatar.setMinimumSize(buttonSize);
                        avatar.setMaximumSize(buttonSize);
                        avatar.setBackground(new Color(156, 156, 156));
                        buttonPanel.add(avatar);
                    }
                }
            }
        });

        //handling logout
        logout_btn.addActionListener(e -> {
            //call function to logout
        });
        //handling sending messages
//        send_btn.addActionListener(e -> {
//            try {
//                sendMessage();
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            } catch (BadLocationException ex) {
//                throw new RuntimeException(ex);
//            }
//            inputChat_jtf.setText("");
//        });
        System.out.println("Chat started");
    }

    boolean sendMessage() throws IOException, BadLocationException {
        HTMLDocument htmlDocument = (HTMLDocument) mainChat.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) mainChat.getEditorKit();

        String messageToSend = inputChat_jtf.getText();
        boolean status = clientModule.sendMessage(messageToSend, clientModule.getUsername(), mainChatName);
        //clientModule.sendMessage(messageToSend);
        String message = "<div style='text-align: right;'>" + messageToSend + "\n" + "</div>";
        if (!messageToSend.isEmpty()) {
            editorKit.insertHTML(htmlDocument, htmlDocument.getLength(), message, 0, 0, null);
        }
        return status;
    }

    // Receive message for another user.
    private class listenForMessage implements Runnable{
        @Override
        public void run() {
            //HTMLDocument htmlDocument = (HTMLDocument) mainChat.getDocument();
            //HTMLEditorKit editorKit = (HTMLEditorKit) mainChat.getEditorKit();
                while (true) {
                    PackageDataStructure data = clientModule.receivePackageDataForChat();
                    System.out.println("Received message from server: ");
                    System.out.println(data);
                    //appendMessage(data.content.get(1), data.content.get(0));
                    if (mainChatName.equals(data.content.get(0))) {
                        appendMessage(data.content.get(1), data.content.get(0));
                        //clientModule.sendPackageDataForChat(new PackageDataStructure("success"));
                    }

                }

        }
    }

    public void appendMessage(String msgFromServer, String sender){
        SwingUtilities.invokeLater(() -> {
            try {
                String msg;
                HTMLDocument htmlDocument = (HTMLDocument) mainChat.getDocument();
                HTMLEditorKit editorKit = (HTMLEditorKit) mainChat.getEditorKit();
                if (Objects.equals(sender, clientModule.getUsername())){
                    msg = "<div style='text-align: right;'>" + msgFromServer + "\n" + "</div>";
                } else {
                    msg = "<div style='text-align: left;'>" + msgFromServer + "\n" + "</div>";
                }
                editorKit.insertHTML(htmlDocument, htmlDocument.getLength(), msg, 0, 0, null);
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    void displayChatName(String chatN){
        chatN_jlb.setText(chatN);
        subChatN_jlb.setText(chatN);
    }
    void displayRightSide(int c){ //c: 0 - person, 1 - group, 2 - group + admin
        if (c == 0){ //if this is a personal chat
            buttons_field.removeAll();
            buttons_field.revalidate();
            buttons_field.repaint();

            bottom_field.removeAll();
            bottom_field.revalidate();
            bottom_field.repaint();

            addFriend_btn = new JButton("Add Friend");
            addFriend_btn.setPreferredSize(new Dimension(195, 50));
            addFriend_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(addFriend_btn);

            unfriend_btn = new JButton("Unfriend");
            unfriend_btn.setPreferredSize(new Dimension(195, 50));
            unfriend_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(unfriend_btn);

            spam_btn = new JButton("Spam");
            spam_btn.setPreferredSize(new Dimension(195, 50));
            spam_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(spam_btn);

            block_btn = new JButton("Block");
            block_btn.setPreferredSize(new Dimension(195, 50));
            block_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(block_btn);

            deleteHst_btn = new JButton("Delete History");
            deleteHst_btn.setPreferredSize(new Dimension(195, 50));
            deleteHst_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(deleteHst_btn);

            newGroup_btn = new JButton("New Group Chat");
            newGroup_btn.setPreferredSize(new Dimension(195, 50));
            newGroup_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(newGroup_btn);

            JLabel label_jlb = new JLabel("Search message in chat history: ");
            label_jlb.setFont(new Font("Arial", Font.PLAIN, 15));
            bottom_field.add(label_jlb);

            searchMsg_jtf = new JTextField(17);
            searchMsg_jtf.setFont(new Font("Arial", Font.PLAIN, 14));
            bottom_field.add(searchMsg_jtf);

            search_btn = new JButton("Search in this chat");
            search_btn.setPreferredSize(new Dimension(185, 30));
            search_btn.setBackground(Color.WHITE);
            bottom_field.add(search_btn);

            searchAll_btn = new JButton("Search in all chats");
            searchAll_btn.setPreferredSize(new Dimension(185, 30));
            searchAll_btn.setBackground(Color.WHITE);
            bottom_field.add(searchAll_btn);
        }
        else{ //group chat
            buttons_field.removeAll();
            buttons_field.revalidate();
            buttons_field.repaint();

            bottom_field.removeAll();
            bottom_field.revalidate();
            bottom_field.repaint();

            newName_jtf = new JTextField(17);
            newName_jtf.setFont(new Font("Arial", Font.PLAIN, 14));
            buttons_field.add(newName_jtf);

            changeN_btn = new JButton("Change chat name");
            changeN_btn.setPreferredSize(new Dimension(195, 50));
            changeN_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(changeN_btn);

            addPerson_btn = new JButton("Add new person");
            addPerson_btn.setPreferredSize(new Dimension(195, 50));
            addPerson_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(addPerson_btn);

            leaveG_btn = new JButton("Leave group");
            leaveG_btn.setPreferredSize(new Dimension(195, 50));
            leaveG_btn.setBackground(new Color(150, 199, 255));
            bottom_field.add(leaveG_btn);

            if (c == 2) {
                addAdmin_btn = new JButton("Add new admin");
                addAdmin_btn.setPreferredSize(new Dimension(195, 50));
                addAdmin_btn.setBackground(new Color(150, 199, 202));
                buttons_field.add(addAdmin_btn);

                deletePerson_btn = new JButton("Delete person");
                deletePerson_btn.setPreferredSize(new Dimension(195, 50));
                deletePerson_btn.setBackground(new Color(150, 199, 202));
                buttons_field.add(deletePerson_btn);
            }
        }
    }


    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        new Chat();
    }
}
