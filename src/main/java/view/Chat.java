package view;

import Client.ClientModule;
import DataStructure.PackageDataStructure;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Chat extends JFrame {
    ClientModule clientModule;

    private final JPanel chatN_field;
    private final JLabel chatN_jlb;
    private final JLabel subChatN_jlb;
    private final JPanel buttons_field;
    private final JPanel bottom_field;
    private JButton addFriend_btn;
    private JButton acceptFriend_btn;
    private JButton declineFriend_btn;
    private JButton removeRequest_btn;
    private JButton unfriend_btn;
    private JButton block_btn;
    private JButton unblock_btn;
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

    private int mainChatID;
    private int mainChatType;

    // TODO: fix the get Users function
    public ArrayList<User> getUsers() {
        System.out.println("Getting friends list get from server");
        ArrayList<String> friends = clientModule.getFriendList();
        ArrayList<User> userlist = new ArrayList<>();
        if (friends.getFirst().equals("No friends")) {
            System.out.println("Friends status get from server failed, this user may have no friend");
            //return userlist;
        } else {
            System.out.println("Friends list get from server");
            ArrayList<String> friendsStatus = clientModule.getFriendStatus(friends);

            System.out.println("Friends status get from server");
            System.out.println("All friends in4 get from server");
            //int id = 0;
            for (int i = 0; i < friends.size(); i++) {
                System.out.println(friends.get(i) + " " + friendsStatus.get(i));
                userlist.add(new User(0, friends.get(i), friendsStatus.get(i).equals("true") ? "ON" : "OFF"));
            }

            //return userlist;
        }
        ArrayList<String> groupChats = clientModule.getGroupChat();
        ArrayList<Integer> groupIDs = clientModule.getGroupIDs();

        if (!groupChats.isEmpty()) {
            for (int i = 0; i < groupChats.size(); i++) {
                userlist.add(new Group(1, groupChats.get(i), "ON", groupIDs.get(i)));
                System.out.println(groupIDs.get(i));
            }
        } else {
            System.out.println("This user has no group chat");
        }
        return userlist;
    }


    public Chat() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        clientModule = ClientModule.getInstance("localhost", 1234, 5678);
        new Thread(new listenForMessage()).start();
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        setSize(1000, 740);
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
        JLabel title = new JLabel();
        title.setFont(new Font("Arial", Font.BOLD, 20));
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
            buttonPanel.removeAll();
            buttonPanel.revalidate();
            buttonPanel.repaint();
            String keyword = inputUser_jtf.getText();
            ArrayList<String> usersFound = clientModule.findUserByName(keyword);
            ArrayList<User> usersListFound = new ArrayList<>();
            for (String user : usersFound) {
                System.out.println(user);
                usersListFound.add(new User(0, user, "ON"));
            }
            for (User user : usersListFound) {
                JButton avatar = new JButton(user.name);
                Dimension buttonSize = new Dimension(228, 60);
                avatar.setPreferredSize(buttonSize);
                avatar.setMinimumSize(buttonSize);
                avatar.setMaximumSize(buttonSize);
                avatar.setBackground(new Color(156, 156, 156));
                buttonPanel.add(avatar);

                // Change right side
                avatar.addActionListener(usere -> {
                    //System.out.println(avatar.getText());
                    mainChatName = avatar.getText();
                    for (User selectedOne : usersListFound) {
                        if (Objects.equals(selectedOne.name, avatar.getText())) {
                            mainChatType = selectedOne.id;
                            if (mainChatType == 1){
                                System.out.println("Selected a group");
                                Group thisGroup = (Group) selectedOne;
                                mainChatID = thisGroup.getChatID();
                                System.out.println("Main chat ID change to");
                                System.out.println(mainChatID);
                            }
                            else {
                                //mainChatID = clientModule.getChatID(mainChatName);
                            }
                            System.out.println(mainChatID);

                            //System.out.println(selectedOne.id);
                            displayRightSide(selectedOne.id);
                            displayChatName(selectedOne.name);
                        }
                    }
                    //ArrayList<String> history;
                    mainChat.setText("");
                });
            }
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

        AtomicReference<ArrayList<User>> users = new AtomicReference<>(getUsers());
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        if (users.get() == null) {
            System.out.println("No friends");
        } else {

        }
        for (User user : users.get()) {
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
                for (User selectedOne : users.get()) {
                    if (Objects.equals(selectedOne.name, avatar.getText())) {
                        mainChatType = selectedOne.id;
                        if (mainChatType == 1){
                            System.out.println("Selected a group");
                            Group thisGroup = (Group) selectedOne;
                            mainChatID = thisGroup.getChatID();
                            System.out.println("Main chat ID change to");
                            System.out.println(mainChatID);
                        } else {
                            mainChatID = clientModule.getChatID(mainChatName);
                        }
                        System.out.println(mainChatID);

                        System.out.println(selectedOne.id);
                        displayRightSide(selectedOne.id);
                        displayChatName(selectedOne.name);
                    }
                }
                ArrayList<String> history;
                mainChat.setText("");
                if (mainChatType == 0) {
                    history = clientModule.getChatHistory(avatar.getText());
                } else if (mainChatType == 1) {
                    history = clientModule.getGroupHistory(mainChatID);
                } else {
                    history = new ArrayList<>();
                }
                HTMLDocument htmlDocument = (HTMLDocument) mainChat.getDocument();
                HTMLEditorKit editorKit = (HTMLEditorKit) mainChat.getEditorKit();
                for (String msg : history) {
                    String[] split = msg.split(",");
                    if (Objects.equals(split[0], clientModule.getUsername())) {
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
                        String message = "<div style='text-align: left;'>" + split[0] + ": " + split[1] + "\n" + "</div>";
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
                sendMessage(mainChatType);
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
            ArrayList<User> usersUpdated = getUsers();
            users.set(usersUpdated);
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
                    avatar.addActionListener(ae -> {
                        System.out.println(avatar.getText());
                        mainChatName = avatar.getText();
                        for (User selectedOne : usersUpdated) {
                            if (Objects.equals(selectedOne.name, avatar.getText())) {
                                mainChatType = selectedOne.id;
                                if (mainChatType == 1){
                                    System.out.println("Selected a group");
                                    Group thisGroup = (Group) selectedOne;
                                    mainChatID = thisGroup.getChatID();
                                    System.out.println("Main chat ID change to");
                                    System.out.println(mainChatID);
                                } else {
                                    mainChatID = clientModule.getChatID(mainChatName);
                                }
                                System.out.println(mainChatID);

                                System.out.println(selectedOne.id);
                                displayRightSide(selectedOne.id);
                                displayChatName(selectedOne.name);
                            }
                        }
                        ArrayList<String> history;
                        mainChat.setText("");
                        if (mainChatType == 0) {
                            history = clientModule.getChatHistory(avatar.getText());
                        } else if (mainChatType == 1) {
                            history = clientModule.getGroupHistory(mainChatID);
                        } else {
                            history = new ArrayList<>();
                        }
                        HTMLDocument htmlDocument = (HTMLDocument) mainChat.getDocument();
                        HTMLEditorKit editorKit = (HTMLEditorKit) mainChat.getEditorKit();
                        for (String msg : history) {
                            String[] split = msg.split(",");
                            if (Objects.equals(split[0], clientModule.getUsername())) {
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
                                String message = "<div style='text-align: left;'>" + split[0] + ": " + split[1] + "\n" + "</div>";
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
                        avatar.addActionListener(ae2 -> {
                            System.out.println(avatar.getText());
                            mainChatName = avatar.getText();
                            for (User selectedOne : usersUpdated) {
                                if (Objects.equals(selectedOne.name, avatar.getText())) {
                                    mainChatType = selectedOne.id;
                                    if (mainChatType == 1){
                                        System.out.println("Selected a group");
                                        Group thisGroup = (Group) selectedOne;
                                        mainChatID = thisGroup.getChatID();
                                        System.out.println("Main chat ID change to");
                                        System.out.println(mainChatID);
                                    } else {
                                        mainChatID = clientModule.getChatID(mainChatName);
                                    }
                                    System.out.println(mainChatID);
                                    System.out.println(selectedOne.id);
                                    displayRightSide(selectedOne.id);
                                    displayChatName(selectedOne.name);
                                }
                            }
                            ArrayList<String> history;
                            mainChat.setText("");
                            if (mainChatType == 0) {
                                history = clientModule.getChatHistory(avatar.getText());
                            } else if (mainChatType == 1) {
                                history = clientModule.getGroupHistory(mainChatID);
                            } else {
                                history = new ArrayList<>();
                            }
                            HTMLDocument htmlDocument = (HTMLDocument) mainChat.getDocument();
                            HTMLEditorKit editorKit = (HTMLEditorKit) mainChat.getEditorKit();
                            for (String msg : history) {
                                String[] split = msg.split(",");
                                if (Objects.equals(split[0], clientModule.getUsername())) {
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
                                    String message = "<div style='text-align: left;'>" + split[0] + ": " + split[1] + "\n" + "</div>";
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
                        avatar.addActionListener(avatarae -> {
                            System.out.println(avatar.getText());
                            mainChatName = avatar.getText();
                            for (User selectedOne : usersUpdated) {
                                if (Objects.equals(selectedOne.name, avatar.getText())) {
                                    mainChatType = selectedOne.id;
                                    System.out.println(selectedOne.id);
                                    displayRightSide(selectedOne.id);
                                    displayChatName(selectedOne.name);
                                }
                            }
                            ArrayList<String> history;
                            mainChat.setText("");
                            if (mainChatType == 0) {
                                history = clientModule.getChatHistory(avatar.getText());
                            } else if (mainChatType == 1) {
                                history = clientModule.getGroupHistory(mainChatID);
                            } else {
                                history = new ArrayList<>();
                            }
                            HTMLDocument htmlDocument = (HTMLDocument) mainChat.getDocument();
                            HTMLEditorKit editorKit = (HTMLEditorKit) mainChat.getEditorKit();
                            for (String msg : history) {
                                String[] split = msg.split(",");
                                if (Objects.equals(split[0], clientModule.getUsername())) {
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
                                    String message = "<div style='text-align: left;'>" + split[0] + ": " + split[1] + "\n" + "</div>";
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
                        buttonPanel.add(avatar);
                    }
                }
            }

        });

        //handling logout
        logout_btn.addActionListener(e -> {
            //call function to logout
            clientModule.closeConnection();
            dispose();
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
        title.setText(clientModule.getUsername());
    }

    boolean sendMessage(int chatType) throws IOException, BadLocationException {
        HTMLDocument htmlDocument = (HTMLDocument) mainChat.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) mainChat.getEditorKit();
        boolean status;

        String messageToSend = inputChat_jtf.getText();
        status = clientModule.sendMessage(messageToSend, clientModule.getUsername(), mainChatName, chatType, mainChatID);

        if (!status) {
            JOptionPane.showMessageDialog(null, "Message failed to send");
        }
        else {
        //clientModule.sendMessage(messageToSend);
        String message = "<div style='text-align: right;'>" + messageToSend + "\n" + "</div>";
        if (!messageToSend.isEmpty()) {
            editorKit.insertHTML(htmlDocument, htmlDocument.getLength(), message, 0, 0, null);
        }
        }
        return status;
    }

    // Receive message for another user.
    private class listenForMessage implements Runnable {
        @Override
        public void run() {
            //HTMLDocument htmlDocument = (HTMLDocument) mainChat.getDocument();
            //HTMLEditorKit editorKit = (HTMLEditorKit) mainChat.getEditorKit();
            while (true) {
                PackageDataStructure data = clientModule.receivePackageDataForChat();
                System.out.println("Received message from server: ");
                System.out.println(data);
                //appendMessage(data.content.get(1), data.content.get(0));
                if (mainChatType == 0) {
                    //If some message is sent to a person
                    if (mainChatName.equals(data.content.get(0))) {
                        appendMessage(data.content.get(1), data.content.get(0));
                    }
                } else if (mainChatType == 1) {
                    if (mainChatName.equals(data.content.get(2)) && !Objects.equals(clientModule.getUsername(), data.content.get(0)) && Integer.parseInt(data.content.get(3)) == mainChatID) {
                        appendMessage(data.content.get(1), data.content.get(0), Integer.parseInt(data.content.get(3)));
                    }
                }


            }

        }
    }

    public void appendMessage(String msgFromServer, String sender) {
        SwingUtilities.invokeLater(() -> {
            try {
                String msg;
                HTMLDocument htmlDocument = (HTMLDocument) mainChat.getDocument();
                HTMLEditorKit editorKit = (HTMLEditorKit) mainChat.getEditorKit();
                if (Objects.equals(sender, clientModule.getUsername())) {
                    msg = "<div style='text-align: right;'>" + msgFromServer + "\n" + "</div>";
                } else {
                    msg = "<div style='text-align: left;'>" + sender + ": " + msgFromServer + "\n" + "</div>";
                }
                editorKit.insertHTML(htmlDocument, htmlDocument.getLength(), msg, 0, 0, null);
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    // For group chat
    public void appendMessage(String msgFromServer, String sender, int senderID) {
        SwingUtilities.invokeLater(() -> {
            try {
                String msg = "";
                HTMLDocument htmlDocument = (HTMLDocument) mainChat.getDocument();
                HTMLEditorKit editorKit = (HTMLEditorKit) mainChat.getEditorKit();
                if (Objects.equals(sender, clientModule.getUsername())) {
                    msg = "<div style='text-align: right;'>" + msgFromServer + "\n" + "</div>";
                } else if (senderID == mainChatID){
                    msg = "<div style='text-align: left;'>" + sender + ": " + msgFromServer + "\n" + "</div>";
                }
                editorKit.insertHTML(htmlDocument, htmlDocument.getLength(), msg, 0, 0, null);
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    void displayChatName(String chatN) {
        chatN_jlb.setText(chatN);
        subChatN_jlb.setText(chatN);
    }

    void displayRightSide(int c) { //c: 0 - person, 1 - group, 2 - group + admin
        if (c == 0) { //if this is a personal chat
            buttons_field.removeAll();
            buttons_field.revalidate();
            buttons_field.repaint();

            bottom_field.removeAll();
            bottom_field.revalidate();
            bottom_field.repaint();

            String friendShipStatus = clientModule.getFriendShipStatus(mainChatName);

            //Not a friend nor a request was sent
            if(friendShipStatus.equals("3")) {
                addFriend_btn = new JButton("Add Friend");
                addFriend_btn.setPreferredSize(new Dimension(195, 50));
                addFriend_btn.setBackground(new Color(150, 199, 202));
                buttons_field.add(addFriend_btn);
                addFriend_btn.addActionListener(e -> {
                    String result = clientModule.sendFriendRequest(mainChatName);
                    String message = switch (result) {
                        case "success" -> "Successfully send a friend request";
                        case "0" -> "User does not exist";
                        case "1" -> "Friend request already exists, do you wish to remove the request?";
                        case "2" -> "This person has already sent you a friend request, do you wish to accept?";
                        default -> "Something unexpected happened";
                    };
                    if (result.equals("success")) {
                        JOptionPane.showMessageDialog(null, message);
                    } else if (result.equals("1")) {
                        int option = JOptionPane.showConfirmDialog(null, message, "Remove request", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            clientModule.removeFriendRequest(mainChatName);
                        }
                    } else if (result.equals("2")) {
                        int option = JOptionPane.showConfirmDialog(null, message, "Accept friend request", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            clientModule.acceptFriendRequest(mainChatName);
                        } else if (option == JOptionPane.NO_OPTION) {
                            clientModule.declineFriendRequest(mainChatName);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, message);
                    }
                });
            }
            //User already sent a request
            else if (friendShipStatus.equals("1")) {
                removeRequest_btn = new JButton("Remove request");
                removeRequest_btn.setPreferredSize(new Dimension(195, 50));
                removeRequest_btn.setBackground(new Color(150, 199, 202));
                buttons_field.add(removeRequest_btn);
                removeRequest_btn.addActionListener(e -> {
                    String result = clientModule.removeFriendRequest(mainChatName);
                    if(result.equals("succes"))
                        JOptionPane.showMessageDialog(null, "Removed friend request");
                });
            }
            //The other already send a request
            else if (friendShipStatus.equals("2")) {
                acceptFriend_btn = new JButton("Accept request");
                acceptFriend_btn.setPreferredSize(new Dimension(195, 50));
                acceptFriend_btn.setBackground(new Color(150, 199, 202));
                buttons_field.add(acceptFriend_btn);
                acceptFriend_btn.addActionListener(e -> {
                    String result = clientModule.acceptFriendRequest(mainChatName);
                    if (result.equals("success"))
                        JOptionPane.showMessageDialog(null, "You are now friend with this person");
                });
                declineFriend_btn = new JButton("Decline request");
                declineFriend_btn.setPreferredSize(new Dimension(195, 50));
                declineFriend_btn.setBackground(new Color(150, 199, 202));
                buttons_field.add(declineFriend_btn);
                declineFriend_btn.addActionListener(e -> {
                    String result = clientModule.declineFriendRequest(mainChatName);
                    if (result.equals("success"))
                        JOptionPane.showMessageDialog(null, "Decline this person request");
                });
            }
            //Already friends
            else if (friendShipStatus.equals("0")) {
                unfriend_btn = new JButton("Unfriend");
                unfriend_btn.setPreferredSize(new Dimension(195, 50));
                unfriend_btn.setBackground(new Color(150, 199, 202));
                buttons_field.add(unfriend_btn);
                unfriend_btn.addActionListener(e -> {
                    String result = clientModule.removeFriend(mainChatName);
                    if(result.equals("success"))
                        JOptionPane.showMessageDialog(null, "You have removed this friend");
                });
            }

            spam_btn = new JButton("Spam");
            spam_btn.setPreferredSize(new Dimension(195, 50));
            spam_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(spam_btn);
            spam_btn.addActionListener(spam -> {
                boolean result = clientModule.reportSpam(mainChatName);
                if (result) {
                    JOptionPane.showMessageDialog(this, "Report successfully, admin will consider your report");
                }
                else {
                    JOptionPane.showMessageDialog(this, "Failed to send, you may have reported this person already!");
                }
            });

            block_btn = new JButton("Block");
            block_btn.setPreferredSize(new Dimension(195, 50));
            block_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(block_btn);
            block_btn.addActionListener(block -> {
                boolean result = clientModule.blockUser(mainChatName);
                if (!result){
                    JOptionPane.showMessageDialog(this, "Cannot block this user, you may have already blocked him/her ");
                }
                else {
                    JOptionPane.showMessageDialog(this, "Block successfully");
                }
            });

            unblock_btn = new JButton("Unblock");
            unblock_btn.setPreferredSize(new Dimension(195, 50));
            unblock_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(unblock_btn);
            unblock_btn.addActionListener(block -> {
                boolean result = clientModule.unblockUser(mainChatName);
                if (!result){
                    JOptionPane.showMessageDialog(this, "Cannot unblock this user, you may haven't blocked him/her yet");
                }
                else {
                    JOptionPane.showMessageDialog(this, "Unblock successfully");
                }
            });

            deleteHst_btn = new JButton("Delete History");
            deleteHst_btn.setPreferredSize(new Dimension(195, 50));
            deleteHst_btn.setBackground(new Color(150, 199, 202));
            deleteHst_btn.addActionListener(deleteHis ->{
                if (mainChatType == 1){
                    JOptionPane.showMessageDialog(this, "Cannot delete history of a group chat");
                } else {
                    String confirm = JOptionPane.showInputDialog(this, "Are you sure you want to delete history of this chat? (Y/N)");
                    if (confirm == null || confirm.equals("N") || confirm.equals("n")  || confirm.isEmpty()){
                        JOptionPane.showMessageDialog(this, "Cancelled");
                    } else if (confirm.equals("Y") || confirm.equals("y")){
                        System.out.println("Deleting history");
                        boolean result = clientModule.deleteChatHistory(mainChatID);
                        if (result){
                            JOptionPane.showMessageDialog(this, "Successfully deleted");
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to delete");
                        }
                    }
                }
            });
            buttons_field.add(deleteHst_btn);

            newGroup_btn = new JButton("New Group Chat");
            newGroup_btn.setPreferredSize(new Dimension(195, 50));
            newGroup_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(newGroup_btn);
            newGroup_btn.addActionListener(e -> {
                clientModule.createGroupChat(mainChatName);
            });

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
        else { //group chat
            buttons_field.removeAll();
            buttons_field.revalidate();
            buttons_field.repaint();

            bottom_field.removeAll();
            bottom_field.revalidate();
            bottom_field.repaint();

            newName_jtf = new JTextField(17);
            newName_jtf.setFont(new Font("Arial", Font.PLAIN, 14));
            //buttons_field.add(newName_jtf);

            changeN_btn = new JButton("Change chat name");
            changeN_btn.setPreferredSize(new Dimension(195, 50));
            changeN_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(changeN_btn);
            changeN_btn.addActionListener(changegroupname -> {
                if (!clientModule.checkAdmin(mainChatID)){
                    JOptionPane.showMessageDialog(this, "You don't have permission to add person to this group");
                } else {
                    String newname = JOptionPane.showInputDialog(this, "Enter the new name of this group");
                    boolean result = clientModule.changeGroupName(newname, mainChatID);
                    if (result){
                        JOptionPane.showMessageDialog(this, "Successfully changed");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to changed this group's name");
                    }
                }
            });

            addAdmin_btn = new JButton("Add new admin");
            addAdmin_btn.setPreferredSize(new Dimension(195, 50));
            addAdmin_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(addAdmin_btn);
            addAdmin_btn.addActionListener(addadmin -> {
                if (!clientModule.checkAdmin(mainChatID)){
                    JOptionPane.showMessageDialog(this, "You don't have permission to add person to this group");
                } else {
                    String userToAdd = JOptionPane.showInputDialog(this, "Enter the username to be added");

                    boolean result = clientModule.addGroupAdmin(userToAdd, mainChatID);
                    if (result){
                        JOptionPane.showMessageDialog(this, "Successfully added");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add this person to group");
                    }
                }
            });

            addPerson_btn = new JButton("Add new person");
            addPerson_btn.setPreferredSize(new Dimension(195, 50));
            addPerson_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(addPerson_btn);
            addPerson_btn.addActionListener(addmember -> {
                if (!clientModule.checkAdmin(mainChatID)){
                    JOptionPane.showMessageDialog(this, "You don't have permission to add person to this group");
                } else {
                    String userToAdd = JOptionPane.showInputDialog(this, "Enter the username to be added");
                    boolean result = clientModule.addUserToGroup(userToAdd, mainChatID);
                    if (result){
                        JOptionPane.showMessageDialog(this, "Successfully added");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add this person to group");
                    }
                }
            });

            deletePerson_btn = new JButton("Delete person");
            deletePerson_btn.setPreferredSize(new Dimension(195, 50));
            deletePerson_btn.setBackground(new Color(150, 199, 202));
            buttons_field.add(deletePerson_btn);
            deletePerson_btn.addActionListener(deletemember -> {
                if (!clientModule.checkAdmin(mainChatID)){
                    JOptionPane.showMessageDialog(this, "You don't have permission to delete person from this group");
                } else {
                    String userToDelete = JOptionPane.showInputDialog(this, "Enter the username to be deleted");
                    if (clientModule.getUsername().equals(userToDelete)){
                        JOptionPane.showMessageDialog(this, "Cannot delete yourself");
                    } else {
                        boolean result = clientModule.deleteUserFromGroup(userToDelete, mainChatID);
                        if (result){
                            JOptionPane.showMessageDialog(this, "Successfully deleted");
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to delete this person from group");
                        }
                    }
                }
            });

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
