import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class BooksTab extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTitle, txtGenre, txtYear, txtISBN, txtSearch;

    public BooksTab() {
        setLayout(new BorderLayout());

        // Table model for displaying data
        model = new DefaultTableModel(new Object[]{"Book ID", "Title", "Genre", "Published Year", "ISBN"}, 0);
        table = new JTable(model);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for input fields and buttons
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        // Input fields for adding books
        txtTitle = new JTextField();
        txtGenre = new JTextField();
        txtYear = new JTextField();
        txtISBN = new JTextField();

        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(txtTitle);
        inputPanel.add(new JLabel("Genre:"));
        inputPanel.add(txtGenre);
        inputPanel.add(new JLabel("Year:"));
        inputPanel.add(txtYear);
        inputPanel.add(new JLabel("ISBN:"));
        inputPanel.add(txtISBN);

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
        loadBooksData();

        // Add event listeners for buttons
        btnAdd.addActionListener(e -> addBook());
        btnSearch.addActionListener(e -> searchBook());
        btnDelete.addActionListener(e -> deleteBook());
    }

    // Load books data from the database
    private void loadBooksData() {
        String query = "SELECT * FROM Books";  // Query to fetch all data from the Books table

        try (Connection conn = LibraryApp.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Clear the table first
            model.setRowCount(0);

            // Iterate over the result set and populate the table
            while (rs.next()) {
                int bookId = rs.getInt("BookID");
                String title = rs.getString("Title");
                String genre = rs.getString("Genre");
                int publishedYear = rs.getInt("PublishedYear");
                String isbn = rs.getString("ISBN");

                // Add the row to the table
                model.addRow(new Object[]{bookId, title, genre, publishedYear, isbn});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading books data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add a book to the database
    private void addBook() {
        String title = txtTitle.getText();
        String genre = txtGenre.getText();
        String year = txtYear.getText();
        String isbn = txtISBN.getText();

        if (title.isEmpty() || genre.isEmpty() || year.isEmpty() || isbn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO Books (Title, Genre, PublishedYear, ISBN) VALUES (?, ?, ?, ?)";

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            stmt.setString(2, genre);
            stmt.setInt(3, Integer.parseInt(year));
            stmt.setString(4, isbn);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBooksData();  // Reload the table data
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding book", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Search books by Title or ISBN
    private void searchBook() {
        String searchQuery = txtSearch.getText();
        String query = "SELECT * FROM Books WHERE Title LIKE ? OR ISBN LIKE ?";

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchQuery + "%");
            stmt.setString(2, "%" + searchQuery + "%");

            ResultSet rs = stmt.executeQuery();

            // Clear the table first
            model.setRowCount(0);

            // Iterate over the result set and populate the table
            while (rs.next()) {
                int bookId = rs.getInt("BookID");
                String title = rs.getString("Title");
                String genre = rs.getString("Genre");
                int publishedYear = rs.getInt("PublishedYear");
                String isbn = rs.getString("ISBN");

                // Add the row to the table
                model.addRow(new Object[]{bookId, title, genre, publishedYear, isbn});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching books", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete a book from the database
    private void deleteBook() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No book selected for deletion", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookId = (int) model.getValueAt(selectedRow, 0);  // Get the Book ID from the selected row

        String query = "DELETE FROM Books WHERE BookID = ?";

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bookId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Book deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBooksData();  // Reload the table data
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting book", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
