import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class MembersTab extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtEmail, txtPhone, txtSearch;

    public MembersTab() {
        setLayout(new BorderLayout());

        // Create the table model
        model = new DefaultTableModel();
        model.addColumn("Member ID");
        model.addColumn("Name");
        model.addColumn("Email");
        model.addColumn("Phone");

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel for input fields and buttons
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        // Input fields for adding members
        txtName = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(txtName);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(txtEmail);
        inputPanel.add(new JLabel("Phone:"));
        inputPanel.add(txtPhone);

        // Buttons for Add, Search, and Delete
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnSearch = new JButton("Search");
        JButton btnDelete = new JButton("Delete");
        txtSearch = new JTextField(15);

        buttonPanel.add(new JLabel("Search:"));
        buttonPanel.add(txtSearch);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);

        // Add input panel and button panel to the main panel
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Fetch and display data from the database
        loadMembersData();

        // Add event listeners for buttons
        btnAdd.addActionListener(e -> addMember());
        btnSearch.addActionListener(e -> searchMember());
        btnDelete.addActionListener(e -> deleteMember());
    }

    // Load members data from the database
    private void loadMembersData() {
        String sql = "SELECT * FROM Members";  // Query to fetch all data from the Members table

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Clear the table first
            model.setRowCount(0);

            // Iterate over the result set and populate the table
            while (rs.next()) {
                String memberID = rs.getString("MemberID");
                String name = rs.getString("Name");
                String email = rs.getString("Email");
                String phone = rs.getString("Phone");

                // Add the row to the table
                model.addRow(new Object[]{memberID, name, email, phone});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading member data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add a member to the database
    private void addMember() {
        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO Members (Name, Email, Phone) VALUES (?, ?, ?)";

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMembersData();  // Reload the table data
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding member", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Search members by MemberID, Name, or Email
    private void searchMember() {
        String searchQuery = txtSearch.getText();
        String query = "SELECT * FROM Members WHERE MemberID LIKE ? OR Name LIKE ? OR Email LIKE ?";

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchQuery + "%");
            stmt.setString(2, "%" + searchQuery + "%");
            stmt.setString(3, "%" + searchQuery + "%");

            ResultSet rs = stmt.executeQuery();

            // Clear the table first
            model.setRowCount(0);

            // Iterate over the result set and populate the table
            while (rs.next()) {
                String memberID = rs.getString("MemberID");
                String name = rs.getString("Name");
                String email = rs.getString("Email");
                String phone = rs.getString("Phone");

                // Add the row to the table
                model.addRow(new Object[]{memberID, name, email, phone});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching members", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete a member from the database
    private void deleteMember() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No member selected for deletion", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String memberID = (String) model.getValueAt(selectedRow, 0);  // Get the MemberID from the selected row

        String query = "DELETE FROM Members WHERE MemberID = ?";

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, memberID);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Member deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMembersData();  // Reload the table data
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting member", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
