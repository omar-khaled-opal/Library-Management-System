import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class LoansTab extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtBookID, txtMemberID, txtBorrowDate, txtReturnDate, txtSearch;

    public LoansTab() {
        setLayout(new BorderLayout());

        // Create the table model
        model = new DefaultTableModel();
        model.addColumn("Loan ID");
        model.addColumn("Book ID");
        model.addColumn("Member ID");
        model.addColumn("Borrow Date");
        model.addColumn("Return Date");

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel for input fields and buttons
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        // Input fields for adding loans
        txtBookID = new JTextField();
        txtMemberID = new JTextField();
        txtBorrowDate = new JTextField();
        txtReturnDate = new JTextField();

        inputPanel.add(new JLabel("Book ID:"));
        inputPanel.add(txtBookID);
        inputPanel.add(new JLabel("Member ID:"));
        inputPanel.add(txtMemberID);
        inputPanel.add(new JLabel("Borrow Date:"));
        inputPanel.add(txtBorrowDate);
        inputPanel.add(new JLabel("Return Date:"));
        inputPanel.add(txtReturnDate);

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
        loadLoansData();

        // Add event listeners for buttons
        btnAdd.addActionListener(e -> addLoan());
        btnSearch.addActionListener(e -> searchLoan());
        btnDelete.addActionListener(e -> deleteLoan());
    }

    // Load loans data from the database
    private void loadLoansData() {
        String sql = "SELECT * FROM Loans";  // Query to fetch all data from the Loans table

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Clear the table first
            model.setRowCount(0);

            // Iterate over the result set and populate the table
            while (rs.next()) {
                String loanID = rs.getString("LoanID");
                String bookID = rs.getString("BookID");
                String memberID = rs.getString("MemberID");
                String borrowDate = rs.getString("BorrowDate");
                String returnDate = rs.getString("ReturnDate");

                // Add the row to the table
                model.addRow(new Object[]{loanID, bookID, memberID, borrowDate, returnDate});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading loan data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add a loan to the database
    private void addLoan() {
        String bookID = txtBookID.getText();
        String memberID = txtMemberID.getText();
        String borrowDate = txtBorrowDate.getText();
        String returnDate = txtReturnDate.getText();

        if (bookID.isEmpty() || memberID.isEmpty() || borrowDate.isEmpty() || returnDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO Loans (BookID, MemberID, BorrowDate, ReturnDate) VALUES (?, ?, ?, ?)";

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, bookID);
            stmt.setString(2, memberID);
            stmt.setString(3, borrowDate);
            stmt.setString(4, returnDate);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Loan added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadLoansData();  // Reload the table data
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding loan", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Search loans by LoanID, BookID, or MemberID
    private void searchLoan() {
        String searchQuery = txtSearch.getText();
        String query = "SELECT * FROM Loans WHERE LoanID LIKE ? OR BookID LIKE ? OR MemberID LIKE ?";

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
                String loanID = rs.getString("LoanID");
                String bookID = rs.getString("BookID");
                String memberID = rs.getString("MemberID");
                String borrowDate = rs.getString("BorrowDate");
                String returnDate = rs.getString("ReturnDate");

                // Add the row to the table
                model.addRow(new Object[]{loanID, bookID, memberID, borrowDate, returnDate});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching loans", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete a loan from the database
    private void deleteLoan() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No loan selected for deletion", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String loanID = (String) model.getValueAt(selectedRow, 0);  // Get the LoanID from the selected row

        String query = "DELETE FROM Loans WHERE LoanID = ?";

        try (Connection conn = LibraryApp.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, loanID);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Loan deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadLoansData();  // Reload the table data
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting loan", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
