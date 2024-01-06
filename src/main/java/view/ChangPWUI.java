package view;

import Database.DB;
import User.UserController;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ChangPWUI extends JFrame {
    private JButton signIn_jbtn;
    private JButton signUp_jbtn;
    private JButton back_jbtn;
    private JPasswordField pw_jtf;
    private JTextField userN_jtf;

    public ChangPWUI(){
        setSize(1000,740);
        setTitle("Forgot password");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(new Color(227, 235, 240));

        JLabel label = new JLabel("Forgot password");
        label.setFont(new Font("Arial", Font.BOLD, 27));
        Dimension size = label.getPreferredSize();
        label.setBounds(400, 120, size.width, size.height);

        JLabel notify1 = new JLabel("New password has been sent to email abc@def.gmail.com");
        notify1.setFont(new Font("Arial", Font.ITALIC, 17));
        Dimension size1 = notify1.getPreferredSize();
        notify1.setBounds(300, 170, 500, size1.height);

        JLabel notify2 = new JLabel("Enter this password to continue");
        notify2.setFont(new Font("Arial", Font.ITALIC, 17));
        Dimension size2 = notify2.getPreferredSize();
        notify2.setBounds(400, 210, 500, size2.height);

        //form
        JPanel formContainer = new JPanel();
        formContainer.setLayout(null);
        formContainer.setBounds(250, 280, 700, 400);
        formContainer.setBackground(new Color(227, 235, 240));

        //username
        JLabel userN_jlb = new JLabel("User name");
        userN_jlb.setFont(new Font("Arial", Font.PLAIN, 18));
        userN_jlb.setBounds(40, 10, 100, userN_jlb.getPreferredSize().height);

        userN_jtf = new JTextField(18);
        userN_jtf.setFont(new Font("Arial", Font.PLAIN, 17));
        userN_jtf.setBounds(220, 0, 270, 35);

        //new password
        JLabel pw_jlb = new JLabel("New password");
        pw_jlb.setFont(new Font("Arial", Font.PLAIN, 18));
        pw_jlb.setBounds(40, 75, 150, pw_jlb.getPreferredSize().height);

        pw_jtf = new JPasswordField(15);
        pw_jtf.setFont(new Font("Arial", Font.PLAIN, 17));
        pw_jtf.setBounds(220, 65, 270, 35);

        //signIn button
        signIn_jbtn = new RoundedButton("SIGN IN", 120, 45, 40, false, null);
        signIn_jbtn.setBounds(220, 125, 120, 45);
        signIn_jbtn.setBackground(new Color(140, 158, 221));
        signIn_jbtn.setForeground(Color.BLACK);
        signIn_jbtn.setBorder(null);

        //back button
        back_jbtn = new RoundedButton("BACK", 120, 45, 40, false, null);
        back_jbtn.setBounds(375, 125, 120, 45);
        back_jbtn.setBackground(new Color(140, 158, 221));
        back_jbtn.setForeground(Color.BLACK);
        back_jbtn.setBorder(null);

        //signUp button
        JLabel signUp_jlb = new JLabel("Don't have an account");
        signUp_jlb.setFont(new Font("Arial", Font.ITALIC, 17));
        signUp_jlb.setForeground(new Color(70, 67, 67));
        signUp_jlb.setBounds(50, 240, 200, signUp_jlb.getPreferredSize().height);

        signUp_jbtn = new RoundedButton("CREATE NEW", 130, 45, 40, true, new Color(239, 95, 86));
        signUp_jbtn.setBounds(300, 230, 130, 45);
        signUp_jbtn.setBackground(new Color(255, 244, 244));
        signUp_jbtn.setForeground(new Color(239, 95, 86));
        signUp_jbtn.setBorder(null);

        formContainer.add(userN_jlb );
        formContainer.add(userN_jtf );
        formContainer.add(pw_jlb);
        formContainer.add(pw_jtf);
        formContainer.add(signIn_jbtn);
        formContainer.add(back_jbtn);
        formContainer.add(signUp_jlb);
        formContainer.add(signUp_jbtn);

        add(label);
        add(notify1);
        add(notify2);
        add(formContainer);

        signIn_jbtn.addActionListener(e -> {
            registerUser();
        });
        signUp_jbtn.addActionListener(e -> {
            signUp();
        });
        back_jbtn.addActionListener(e -> {
            signIn();
        });
        setVisible(true);
    }

    void registerUser(){
        String pw = pw_jtf.getText();
        if (!Objects.equals(pw, "")){
            DB db = new DB();
            UserController userC = new UserController(db.getConnection());
//            userC.registerUser(userN, pw);
        }
    }

    void signUp(){
        dispose();
        new SignUpUI();
    }

    void signIn() {
        dispose();
        new SignInUI();
    }

    public static void main(String[] args) {
        new ChangPWUI();
    }
}
