import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class AuthorsTab extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtSearch;

    public AuthorsTab() {
        setLayout(new BorderLayout());

        // Create the table model
        model = new DefaultTableModel();
        model.addColumn("Author ID");
        model.addColumn("Name");

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel for input fields and buttons
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));

        // Input fields for adding authors
        txtName = new JTextField();

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(txtName);

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
        loadAuthorsData();

        // Add event listeners for buttons
        btnAdd.addActionListener(e -> addAuthor());
        btnSearch.addActionListener(e -> searchAuthor());
        btnDelete.addActionListener(e -> deleteAuthor());
    }

    // Load authors data from the database
    private void loadAuthorsData() {
        String sql = "SELECT * FROM Authors";  // Query to fetch all data from the Authors table

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Clear the table first
            model.setRowCount(0);

            // Iterate over the result set and populate the table
            while (rs.next()) {
                String authorID = rs.getString("AuthorID");
                String name = rs.getString("Name");

                // Add the row to the table
                model.addRow(new Object[]{authorID, name});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading author data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add an author to the database
    private void addAuthor() {
        String name = txtName.getText();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name field must be filled out!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO Authors (Name) VALUES (?)";

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Author added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAuthorsData();  // Reload the table data
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding author", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Search authors by AuthorID or Name
    private void searchAuthor() {
        String searchQuery = txtSearch.getText();
        String query = "SELECT * FROM Authors WHERE AuthorID LIKE ? OR Name LIKE ?";

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchQuery + "%");
            stmt.setString(2, "%" + searchQuery + "%");

            ResultSet rs = stmt.executeQuery();

            // Clear the table first
            model.setRowCount(0);

            // Iterate over the result set and populate the table
            while (rs.next()) {
                String authorID = rs.getString("AuthorID");
                String name = rs.getString("Name");

                // Add the row to the table
                model.addRow(new Object[]{authorID, name});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching authors", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete an author from the database
    private void deleteAuthor() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No author selected for deletion", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String authorID = (String) model.getValueAt(selectedRow, 0);  // Get the AuthorID from the selected row

        String query = "DELETE FROM Authors WHERE AuthorID = ?";

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, authorID);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Author deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAuthorsData();  // Reload the table data
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting author", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
