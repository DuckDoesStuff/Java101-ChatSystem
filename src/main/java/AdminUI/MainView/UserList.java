/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package AdminUI.MainView;

import AdminUI.User.AddUser;
import AdminUI.User.EditUser;
import AdminUI.User.UserDetail;
import Database.DB;
import User.UserModel;
import User.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Admin
 */
public class UserList extends javax.swing.JFrame {
    UserService userService;
    ArrayList<UserModel> userList = new ArrayList<>();
    /**
     * Creates new form UserList
     */
    public UserList(Connection connection) {
        initComponents();

        userService = new UserService(connection);
        userList = userService.getAllUser();
        userList.sort((o1, o2) -> {
            if (o1.getUserID() > o2.getUserID()) {
                return 1;
            } else if (o1.getUserID() < o2.getUserID()) {
                return -1;
            } else {
                return 0;
            }
        });
        loadUserTable();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        userTable = new javax.swing.JTable();
        searchField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        searchButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        sortSelector = new javax.swing.JComboBox<>();
        detailButton = new javax.swing.JButton();
        backToMainMenu = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jScrollPane1.setHorizontalScrollBar(null);

        userTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null, null}
                },
                new String [] {
                        "ID", "First name", "Last name", "Username", "Email", "Address", "Date of birth", "First joined", "Gender"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                    true, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        userTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        userTable.setShowGrid(true);
        userTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(userTable);

        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel1.setText("Enter username");

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        editButton.setText("Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        sortSelector.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sort by ID (Asc)", "Sort by ID (Desc)", "Sort by Username (Asc)", "Sort by Username (Desc)", "Sort by First joined (Asc)", "Sort by First joined (Desc)" }));
        sortSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortSelectorActionPerformed(evt);
            }
        });

        detailButton.setText("Detail");
        detailButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailButtonActionPerformed(evt);
            }
        });

        backToMainMenu.setText("Back To Main Menu");
        backToMainMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backToMainMenuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 558, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(sortSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 960, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(19, 19, 19)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(removeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(detailButton, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(backToMainMenu))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1)
                                        .addComponent(searchButton)
                                        .addComponent(sortSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(34, 34, 34)
                                                .addComponent(removeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(34, 34, 34)
                                                .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(34, 34, 34)
                                                .addComponent(detailButton, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 539, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
                                .addComponent(backToMainMenu))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        // TODO add your handling code here:
        String username = searchField.getText();
        userList = userService.filterUser(username, true);
        loadUserTable();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchFieldActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        // TODO add your handling code here:
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a user to edit");
            return;
        }
        new EditUser((int)userTable.getValueAt(selectedRow, 0), userService).setVisible(true);
    }//GEN-LAST:event_editButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // TODO add your handling code here:
        new AddUser(userService).setVisible(true);
    }//GEN-LAST:event_addButtonActionPerformed

    private void sortSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortSelectorActionPerformed
        // TODO add your handling code here:
        String selected = sortSelector.getSelectedItem().toString();
        switch (selected) {
            case "Sort by ID (Asc)":
                userList.sort((o1, o2) -> {
                    if (o1.getUserID() > o2.getUserID()) {
                        return 1;
                    } else if (o1.getUserID() < o2.getUserID()) {
                        return -1;
                    } else {
                        return 0;
                    }
                });
                break;
            case "Sort by ID (Desc)":
                userList.sort((o1, o2) -> {
                    if (o1.getUserID() > o2.getUserID()) {
                        return -1;
                    } else if (o1.getUserID() < o2.getUserID()) {
                        return 1;
                    } else {
                        return 0;
                    }
                });
                break;
            case "Sort by Username (Asc)":
                userList.sort((o1, o2) -> {return o1.getUsername().compareTo(o2.getUsername());});
                break;
            case "Sort by Username (Desc)":
                userList.sort((o1, o2) -> {return o2.getUsername().compareTo(o1.getUsername());});
                break;
            case "Sort by First name (Asc)":
                userList = userService.sortUserByFirstName();
                break;
            case "Sort by First name (Desc)":
                userList = userService.sortUserByFirstName();
                Collections.reverse(userList);
                break;
            case "Sort by First joined (Asc)":
                userList = userService.sortUserByFirstJoined();
                break;
            case "Sort by First joined (Desc)":
                userList = userService.sortUserByFirstJoined();
                Collections.reverse(userList);
                break;
        }
        loadUserTable();
    }//GEN-LAST:event_sortSelectorActionPerformed

    private void detailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailButtonActionPerformed
        // TODO add your handling code here:
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a user to view detail");
            return;
        }
        new UserDetail((int) userTable.getValueAt(selectedRow, 0), userService).setVisible(true);
    }//GEN-LAST:event_detailButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // TODO add your handling code here:
        int selected = userTable.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(null, "Please select a user to remove");
            return;
        }

        int userID = (int) userTable.getValueAt(selected, 0);
        userService.deleteByAdmin(userID);
        JOptionPane.showMessageDialog(null, "User: " + userID + " has been removed");
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        model.removeRow(selected);
    }//GEN-LAST:event_removeButtonActionPerformed

    private void backToMainMenuActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        dispose();
        new MainMenu().setVisible(true);
    }

    private void loadUserTable() {
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        model.setRowCount(0);
        for (UserModel user : userList) {
            model.addRow(new Object[]{user.getUserID(), user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getAddress(), user.getDateOfBirth(), user.getFirst_joined(), user.isGender()});
        }
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
            java.util.logging.Logger.getLogger(UserList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserList.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DB db = new DB();
                new UserList(db.getConnection()).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backToMainMenu;
    private javax.swing.JButton addButton;
    private javax.swing.JButton detailButton;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable userTable;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JComboBox<String> sortSelector;
    // End of variables declaration//GEN-END:variables
}
