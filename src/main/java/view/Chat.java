package view;

import javax.swing.*;
import java.awt.*;

public class Chat extends JFrame {

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


    public Chat() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        setSize(1000,740);
        setTitle("Chat");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);


        //LEFT AREA
        JPanel leftArea = new JPanel(null);
        leftArea.setBounds(0, 0, 250, 700);
        leftArea.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));

        //topPart(in left area - title and searching)
        //frame
        JPanel topPart = new JPanel(null);
        topPart.setBounds(0, 0, 249, 125);
        topPart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(0, 0, 1, 0)));
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
//        JButton findUser_btn = new RoundedButton("Find", 70, 30, 20, true, Color.BLACK);
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
        String[] statusL = {"All", "Online list", "Offline"};
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
        JPanel usersField = new JPanel(new FlowLayout(FlowLayout.CENTER));
        usersField.setBounds(0, 50, 249, 380);
        usersField.setBackground(new Color(217, 217, 217));
//        JScrollPane = new JScrollPane(talkArea,
//                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
//                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);)
        midPart.add(usersField);

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
        JPanel chat_field = new JPanel(null);
        chat_field.setBounds(0, 76, 500, 550);
        chat_field.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        chat_field.setBackground(new Color(227, 235, 240));
        centerArea.add(chat_field);

        //inputMsg (in center area - input and send message)
        //frame
        JPanel input_field = new JPanel(null);
        input_field.setBounds(0, 627, 500, 75);
        input_field.setBackground(new Color(227, 235, 240));
        centerArea.add(input_field);
        //input
        JTextField inputChat_jtf = new JTextField(3);
        inputChat_jtf.setFont(new Font("Arial", Font.PLAIN, 14));
        inputChat_jtf.setBounds(10, 10, 400, 30);
        input_field.add(inputChat_jtf);

        JButton send_btn = new JButton("Send");
//        JButton send_btn = new RoundedButton("Send", 70, 30, 20, true, Color.BLACK);
        send_btn.setBackground(Color.WHITE);
        send_btn.setBounds(420, 10, 70, 30);
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
            displayChatName("TVA - 123");
            //call function display user
        });

        //handling logout
        logout_btn.addActionListener(e -> {
            //call function to logout
        });
        //handling sending messages
        send_btn.addActionListener(e -> {

        });

        displayRightSide(0);
        displayChatName("TVA");

    }

    void displayChatName(String chatN){
        chatN_jlb.setText(chatN);
        subChatN_jlb.setText(chatN);
    }
    void displayRightSide(int c){ //c: 0 - person, 1 - group, 2 - group + admin
        if (c == 0){ //if this is a personal chat
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
