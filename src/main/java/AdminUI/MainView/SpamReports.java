package AdminUI.MainView;

import User.Spam;
import User.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;

public class SpamReports extends javax.swing.JFrame {

    /**
     * Creates new form SpamReports
     */
    public SpamReports(Connection conn) {
        userService = new UserService(conn);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        title = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableOfSpamTicket = new javax.swing.JTable();
        sortModeCombobox = new javax.swing.JComboBox<>();
        banBtn = new javax.swing.JButton();
        bannedUsernameInput = new javax.swing.JTextField();
        sortByTitle = new javax.swing.JLabel();
        banUserTitle = new javax.swing.JLabel();
        backToMainMenuBtn = new javax.swing.JButton();
        nameToSearch = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();


        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(740, 480));

        title.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        title.setText("Spam Reports");

        tableOfSpamTicket.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "Reporter", "Spammer", "Creation time"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ArrayList<Spam> allSpam = userService.getAllSpam("nameasc");
        for (Spam spam : allSpam) {
            addRow(spam.getUserName(), spam.getSpammerName(), spam.getTimeSpam());
        }

        jScrollPane1.setViewportView(tableOfSpamTicket);
        if (tableOfSpamTicket.getColumnModel().getColumnCount() > 0) {
            tableOfSpamTicket.getColumnModel().getColumn(0).setResizable(false);
            tableOfSpamTicket.getColumnModel().getColumn(1).setResizable(false);
            tableOfSpamTicket.getColumnModel().getColumn(2).setResizable(false);
        }

        sortModeCombobox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Username of Reporter: A-Z", "Username of Reporter: Z-A", "Creation Time: oldest to latest", "Creation Time: latest to oldest" }));
        sortModeCombobox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortModeComboboxActionPerformed(evt);
            }
        });

        banBtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        banBtn.setForeground(new java.awt.Color(255, 102, 102));
        banBtn.setText("Ban user");
        banBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                banBtnActionPerformed(evt);
            }
        });

        bannedUsernameInput.setText("Username to be banned...");

        sortByTitle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        sortByTitle.setText("Sort by");

        banUserTitle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        banUserTitle.setText("Ban user");

        backToMainMenuBtn.setText("Back To Main Menu");

        nameToSearch.setText("Search by name...");


        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(nameToSearch))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 551, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(bannedUsernameInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(banBtn)
                                        .addComponent(sortByTitle)
                                        .addComponent(banUserTitle)
                                        .addComponent(backToMainMenuBtn)
                                        .addComponent(sortModeCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(searchButton))
                                .addContainerGap(14, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(nameToSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(searchButton))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(37, 37, 37)
                                                .addComponent(sortByTitle)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(sortModeCombobox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(73, 73, 73)
                                                .addComponent(banUserTitle)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(bannedUsernameInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(banBtn)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(backToMainMenuBtn)))
                                .addContainerGap(8, Short.MAX_VALUE))
        );

        pack();
    }
//"Username of Reporter: A-Z", "Username of Reporter: Z-A", "Creation Time: oldest to latest", "Creation Time: latest to oldest"
    private void sortModeComboboxActionPerformed(ActionEvent e) {
        clearTable();
        String sortBy="nameasc";

        String sortMode = sortModeCombobox.getSelectedItem().toString();
        System.out.println(sortMode);
        if (sortMode.equals("Username of Reporter: A-Z")){
            sortBy = "nameasc";
        } else if (sortMode.equals("Username of Reporter: Z-A")){
            sortBy = "namedes";
        } else if (sortMode.equals("Creation Time: oldest to latest")){
            sortBy = "timeasc";
        } else {
            sortBy = "timedes";
        }
        System.out.println(sortBy);
        ArrayList<Spam> allSpam = userService.getAllSpam(sortBy);
        for (Spam spam : allSpam) {
            addRow(spam.getUserName(), spam.getSpammerName(), spam.getTimeSpam());
        }
    }


    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        clearTable();
        String name = nameToSearch.getText();
        ArrayList<Spam> allSpam = userService.filterSpam(name);
        for (Spam spam : allSpam) {
            addRow(spam.getUserName(), spam.getSpammerName(), spam.getTimeSpam());
        }
    }

    private void banBtnActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        String username = bannedUsernameInput.getText();
        boolean result = userService.banUser(username);
        if (result){
            JOptionPane.showMessageDialog(null, "User " + username + " has been banned");
        }
        else{
            JOptionPane.showMessageDialog(null, "Ban " + username + " failed");
        }
    }

    private void clearTable(){
        DefaultTableModel model = (DefaultTableModel) tableOfSpamTicket.getModel();
        model.setRowCount(0);
    }

    private void addRow(String reporter, String spammer, Timestamp creationTime){
        DefaultTableModel model = (DefaultTableModel) tableOfSpamTicket.getModel();
        model.addRow(new Object[]{reporter, spammer, creationTime});
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SpamReports.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SpamReports.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SpamReports.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SpamReports.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */

    }

    // Variables declaration - do not modify
    private UserService userService;
    private javax.swing.JButton backToMainMenuBtn;
    private javax.swing.JButton banBtn;
    private javax.swing.JLabel banUserTitle;
    private javax.swing.JTextField bannedUsernameInput;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nameToSearch;
    private javax.swing.JButton searchButton;
    private javax.swing.JLabel sortByTitle;
    private javax.swing.JComboBox<String> sortModeCombobox;
    private javax.swing.JTable tableOfSpamTicket;
    private javax.swing.JLabel title;
    // End of variables declaration
}

