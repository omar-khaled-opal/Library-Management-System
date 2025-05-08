import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class BookAuthorsTab extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtBookID, txtAuthorID, txtSearch;

    public BookAuthorsTab() {
        setLayout(new BorderLayout());

        // Create the table model
        model = new DefaultTableModel();
        model.addColumn("Book ID");
        model.addColumn("Author ID");

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel for input fields and buttons
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        // Input fields for adding Book-Author relationship
        txtBookID = new JTextField();
        txtAuthorID = new JTextField();

        inputPanel.add(new JLabel("Book ID:"));
        inputPanel.add(txtBookID);
        inputPanel.add(new JLabel("Author ID:"));
        inputPanel.add(txtAuthorID);

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
        loadBookAuthorsData();

        // Add event listeners for buttons
        btnAdd.addActionListener(e -> addBookAuthor());
        btnSearch.addActionListener(e -> searchBookAuthor());
        btnDelete.addActionListener(e -> deleteBookAuthor());
    }

    // Load Book-Author data from the database
    private void loadBookAuthorsData() {
        String sql = "SELECT * FROM Book_Authors";  // Query to fetch all data from the Book_Authors table

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Clear the table first
            model.setRowCount(0);

            // Iterate over the result set and populate the table
            while (rs.next()) {
                String bookID = rs.getString("BookID");
                String authorID = rs.getString("AuthorID");

                // Add the row to the table
                model.addRow(new Object[]{bookID, authorID});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading book-author data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add a Book-Author relationship to the database
    private void addBookAuthor() {
        String bookID = txtBookID.getText();
        String authorID = txtAuthorID.getText();

        if (bookID.isEmpty() || authorID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both Book ID and Author ID fields must be filled out!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO Book_Authors (BookID, AuthorID) VALUES (?, ?)";

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, bookID);
            stmt.setString(2, authorID);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Book-Author relationship added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBookAuthorsData();  // Reload the table data
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding Book-Author relationship", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Search Book-Author relationships by BookID or AuthorID
    private void searchBookAuthor() {
        String searchQuery = txtSearch.getText();
        String query = "SELECT * FROM Book_Authors WHERE BookID LIKE ? OR AuthorID LIKE ?";

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchQuery + "%");
            stmt.setString(2, "%" + searchQuery + "%");

            ResultSet rs = stmt.executeQuery();

            // Clear the table first
            model.setRowCount(0);

            // Iterate over the result set and populate the table
            while (rs.next()) {
                String bookID = rs.getString("BookID");
                String authorID = rs.getString("AuthorID");

                // Add the row to the table
                model.addRow(new Object[]{bookID, authorID});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching Book-Author relationships", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete a Book-Author relationship from the database
    private void deleteBookAuthor() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No Book-Author relationship selected for deletion", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String bookID = (String) model.getValueAt(selectedRow, 0);  // Get the BookID from the selected row
        String authorID = (String) model.getValueAt(selectedRow, 1);  // Get the AuthorID from the selected row

        String query = "DELETE FROM Book_Authors WHERE BookID = ? AND AuthorID = ?";

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, bookID);
            stmt.setString(2, authorID);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Book-Author relationship deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBookAuthorsData();  // Reload the table data
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting Book-Author relationship", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
