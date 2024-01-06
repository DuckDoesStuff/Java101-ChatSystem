package view;

import Client.ClientModule;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.Objects;

public class SignUpUI extends JFrame {
    private final JTextField userN_jtf;
    private final JButton signUp_jbtn;
    private final JButton login_jbtn;
    private final JTextField address_jtf;
    private final JDateChooser dateChooser;
    private final JRadioButton maleRadioButton;
    private final JRadioButton femaleRadioButton;
    private final JTextField email_jtf;
    private final JPasswordField pw_jtf;
    private final JTextField firstN_jtf;
    private final JTextField lastN_jtf;
    ClientModule clientModule;

    public SignUpUI(){
        clientModule = ClientModule.getInstance("localhost", 1234, 5678);
        setSize(1000,700);
        setTitle("Sign Up");
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(new Color(227, 235, 240));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                clientModule.closeConnection();
            }
        });
        
        JLabel label = new JLabel("CREATE AN ACCOUNT");
        label.setFont(new Font("Arial", Font.BOLD, 27));
        Dimension size = label.getPreferredSize();
        label.setBounds(350, 60, size.width, size.height);

        //form
        JPanel formContainer = new JPanel();
        formContainer.setLayout(null);
        formContainer.setBounds(230, 120, 700, 700);
        formContainer.setBackground(new Color(227, 235, 240));

        //username
        JLabel userN_jlb = new JLabel("User name");
        userN_jlb.setFont(new Font("Arial", Font.PLAIN, 18));
        userN_jlb.setBounds(40, 10, 100, userN_jlb.getPreferredSize().height);

        userN_jtf = new JTextField(18);
        userN_jtf.setFont(new Font("Arial", Font.PLAIN, 17));
        userN_jtf.setBounds(220, 0, 270, 35);

        //firstname
        JLabel firstN_jlb = new JLabel("First Name");
        firstN_jlb.setFont(new Font("Arial", Font.PLAIN, 18));
        firstN_jlb.setBounds(40, 55, 100, firstN_jlb.getPreferredSize().height);

        firstN_jtf = new JTextField(15);
        firstN_jtf.setFont(new Font("Arial", Font.PLAIN, 17));
        firstN_jtf.setBounds(220, 45, 270, 35);

        //lastname
        JLabel lastN_jlb = new JLabel("Last Name");
        lastN_jlb.setFont(new Font("Arial", Font.PLAIN, 18));
        lastN_jlb.setBounds(40, 100, 100, lastN_jlb.getPreferredSize().height);

        lastN_jtf = new JTextField(15);
        lastN_jtf.setFont(new Font("Arial", Font.PLAIN, 17));
        lastN_jtf.setBounds(220, 90, 270, 35);

        //address
        JLabel address_jlb = new JLabel("Address");
        address_jlb.setFont(new Font("Arial", Font.PLAIN, 18));
        address_jlb.setBounds(40, 145, 100, address_jlb.getPreferredSize().height);

        address_jtf = new JTextField(15);
        address_jtf.setFont(new Font("Arial", Font.PLAIN, 17));
        address_jtf.setBounds(220, 135, 270, 35);

        //date of birth
        JLabel dateChooser_jlb = new JLabel("Date of birth");
        dateChooser_jlb.setFont(new Font("Arial", Font.PLAIN, 18));
        dateChooser_jlb.setBounds(40, 190, 100, dateChooser_jlb.getPreferredSize().height);

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setBounds(220, 180, 270, 35);

        //gender
        JLabel gender_jlb = new JLabel("Gender");
        gender_jlb.setFont(new Font("Arial", Font.PLAIN, 18));
        gender_jlb.setBounds(40, 235, 100, gender_jlb.getPreferredSize().height);

        maleRadioButton = new JRadioButton("Male");
        maleRadioButton.setFont(new Font("Arial", Font.PLAIN, 17));
        maleRadioButton.setBounds(220, 225, 90, 40);
        femaleRadioButton = new JRadioButton("Female");
        femaleRadioButton.setFont(new Font("Arial", Font.PLAIN, 17));
        femaleRadioButton.setBounds(310, 225, 90, 40);

        // Group button
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadioButton);
        genderGroup.add(femaleRadioButton);

        //email
        JLabel email_jlb = new JLabel("Email");
        email_jlb.setFont(new Font("Arial", Font.PLAIN, 18));
        email_jlb.setBounds(40, 280, email_jlb.getPreferredSize().width, email_jlb.getPreferredSize().height);

        email_jtf = new JTextField(15);
        email_jtf.setFont(new Font("Arial", Font.PLAIN, 17));
        email_jtf.setBounds(220, 270, 270, 35);

        //password
        JLabel pw_jlb = new JLabel("Password");
        pw_jlb.setFont(new Font("Arial", Font.PLAIN, 18));
        pw_jlb.setBounds(40, 325, 100, pw_jlb.getPreferredSize().height);

        pw_jtf = new JPasswordField(15);
        pw_jtf.setFont(new Font("Arial", Font.PLAIN, 17));
        pw_jtf.setBounds(220, 315, 270, 35);

        //signUp button
        signUp_jbtn = new RoundedButton("SIGN UP", 270, 45, 40, false, null);
        signUp_jbtn.setBounds(220, 370, 270, 45);
        signUp_jbtn.setBackground(new Color(140, 158, 221));
        signUp_jbtn.setForeground(Color.BLACK);
        signUp_jbtn.setBorder(null);

        //login button
        JLabel login_jlb = new JLabel("Have already an account?");
        login_jlb.setFont(new Font("Arial", Font.ITALIC, 17));
        login_jlb.setForeground(new Color(70, 67, 67));
        login_jlb.setBounds(50, 440, 200, login_jlb.getPreferredSize().height);

        login_jbtn = new RoundedButton("LOGIN", 130, 45, 40, true, new Color(239, 95, 86));
        login_jbtn.setBounds(300, 430, 130, 45);
        login_jbtn.setBackground(new Color(255, 244, 244));
        login_jbtn.setForeground(new Color(239, 95, 86));
        login_jbtn.setBorder(null);

        formContainer.add(userN_jlb );
        formContainer.add(userN_jtf );
        formContainer.add(firstN_jlb);
        formContainer.add(firstN_jtf);
        formContainer.add(lastN_jlb);
        formContainer.add(lastN_jtf);
        formContainer.add(address_jlb);
        formContainer.add(address_jtf);
        formContainer.add(dateChooser_jlb);
        formContainer.add(dateChooser);
        formContainer.add(gender_jlb);
        formContainer.add(maleRadioButton);
        formContainer.add(femaleRadioButton);
        formContainer.add(email_jlb);
        formContainer.add(email_jtf);
        formContainer.add(pw_jlb);
        formContainer.add(pw_jtf);
        formContainer.add(signUp_jbtn);
        formContainer.add(login_jlb);
        formContainer.add(login_jbtn);

        add(label);
        add(formContainer);

        signUp_jbtn.addActionListener(e -> {
            try {
                registerUser();
            } catch (UnsupportedLookAndFeelException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (InstantiationException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        });
        login_jbtn.addActionListener(e -> {
            login();
        });
        setVisible(true);
    }

    void registerUser() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String userN = userN_jtf.getText();
        String firstN = firstN_jtf.getText();
        String lastN = lastN_jtf.getText();
        String address = address_jtf.getText();
        Date dob = dateChooser.getDate();
        boolean gender = maleRadioButton.isSelected();
        String email = email_jtf.getText();
        String pw = pw_jtf.getText();

        if (!userN.trim().isEmpty() && !pw.trim().isEmpty() && !email.trim().isEmpty() && dob != null &&
                !firstN.trim().isEmpty() && !lastN.trim().isEmpty() && !address.trim().isEmpty()) {
            String result = clientModule.registerUser(userN, email, pw, firstN, lastN, address, dob, gender);
            if (result.equals("success")) {
                JOptionPane.showMessageDialog(null, "Register successfully");
                dispose();
                new Chat();
            } else {
                JOptionPane.showMessageDialog(null, result);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please fill all the fields");
        }
    }

    void login(){
        dispose();
        new SignInUI();
    }
    
    public static void main(String[] args) {
        new SignUpUI();
    }
}
