package view;

import Database.DB;
import User.UserController;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class SignInUI extends JFrame {
    private JTextField userN_jtf;
    private JButton signIn_jbtn;
    private JButton signUp_jbtn;
    private  JButton pass_jbtn;
    private JTextField pw_jtf;

    public SignInUI(){
        setSize(1000,740);
        setTitle("Sign In");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(new Color(227, 235, 240));

        JLabel label = new JLabel("Welcome to my chat system!");
        label.setFont(new Font("Arial", Font.BOLD, 27));
        Dimension size = label.getPreferredSize();
        label.setBounds(300, 120, size.width, size.height);

        JLabel notify = new JLabel("Please sign in to your account");
        notify.setFont(new Font("Arial", Font.ITALIC, 17));
        Dimension size1 = notify.getPreferredSize();
        notify.setBounds(380, 180, 300, size1.height);

        //form
        JPanel formContainer = new JPanel();
        formContainer.setLayout(null);
        formContainer.setBounds(220, 280, 700, 400);
        formContainer.setBackground(new Color(227, 235, 240));

        //username
        JLabel userN_jlb = new JLabel("User name");
        userN_jlb.setFont(new Font("Arial", Font.PLAIN, 18));
        userN_jlb.setBounds(40, 10, 100, userN_jlb.getPreferredSize().height);

        userN_jtf = new JTextField(18);
        userN_jtf.setFont(new Font("Arial", Font.PLAIN, 17));
        userN_jtf.setBounds(220, 0, 270, 35);

        //password
        JLabel pw_jlb = new JLabel("Password");
        pw_jlb.setFont(new Font("Arial", Font.PLAIN, 18));
        pw_jlb.setBounds(40, 60, 100, pw_jlb.getPreferredSize().height);

        pw_jtf = new JTextField(15);
        pw_jtf.setFont(new Font("Arial", Font.PLAIN, 17));
        pw_jtf.setBounds(220, 55, 270, 35);

        //signIn button
        signIn_jbtn = new RoundedButton("SIGN IN", 270, 45, 40, false, null);
        signIn_jbtn.setBounds(220, 130, 270, 45);
        signIn_jbtn.setBackground(new Color(140, 158, 221));
        signIn_jbtn.setForeground(Color.BLACK);
        signIn_jbtn.setBorder(null);

        //forgot password button
        pass_jbtn = new RoundedButton("Forgot password", 155, 45, 40, false, null);
        pass_jbtn.setBounds(280, 190, 150, 45);
        pass_jbtn.setBackground(new Color(227, 235, 240));
        pass_jbtn.setForeground(Color.BLACK);
        pass_jbtn.setFont(new Font("Arial", Font.ITALIC, 17));
        pass_jbtn.setBorder(null);

        //signUp button
        JLabel signUp_jlb = new JLabel("Don't have an account");
        signUp_jlb.setFont(new Font("Arial", Font.ITALIC, 17));
        signUp_jlb.setForeground(new Color(70, 67, 67));
        signUp_jlb.setBounds(50, 260, 200, signUp_jlb.getPreferredSize().height);

        signUp_jbtn = new RoundedButton("CREATE NEW", 130, 45, 40, true, new Color(239, 95, 86));
        signUp_jbtn.setBounds(300, 250, 130, 45);
        signUp_jbtn.setBackground(new Color(255, 244, 244));
        signUp_jbtn.setForeground(new Color(239, 95, 86));
        signUp_jbtn.setBorder(null);

        formContainer.add(userN_jlb );
        formContainer.add(userN_jtf );
        formContainer.add(pw_jlb);
        formContainer.add(pw_jtf);
        formContainer.add(signIn_jbtn);
        formContainer.add(pass_jbtn);
        formContainer.add(signUp_jlb);
        formContainer.add(signUp_jbtn);

        add(label);
        add(notify);
        add(formContainer);

        signIn_jbtn.addActionListener(e -> {
            registerUser();
        });
        signUp_jbtn.addActionListener(e -> {
            signUp();
        });
        pass_jbtn.addActionListener(e -> {
            forgot();
        });
        setVisible(true);
    }

    void registerUser(){
        String userN = userN_jtf.getText();
        String pw = pw_jtf.getText();
        if (!Objects.equals(userN, "") && !Objects.equals(pw, "")){
            DB db = new DB();
            UserController userC = new UserController(db.getConnection());
//            userC.registerUser(userN, pw);
        }
    }

    void signUp(){
        dispose();
        new SignUpUI();
    }

    void forgot() {
        dispose();
        new ChangPWUI();
    }

    public static void main(String[] args) {
        new SignInUI();
    }
}
