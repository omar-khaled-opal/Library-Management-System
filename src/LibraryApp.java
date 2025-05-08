import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class LibraryApp {
    static final String DB_URL = "jdbc:sqlserver://localhost:"yourhost";databaseName="yourdb";
    static final String USER = "your_db_username";
    static final String PASS = "your_db_password";
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Library Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);

            // Create Tabs
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Books", new BooksTab());
            tabbedPane.addTab("Authors", new AuthorsTab());
            tabbedPane.addTab("Members", new MembersTab());
            tabbedPane.addTab("Loans", new LoansTab());
            tabbedPane.addTab("Book-Authors", new BookAuthorsTab());

            // Add Tabs to Frame
            frame.add(tabbedPane, BorderLayout.CENTER);

            // Make frame visible
            frame.setVisible(true);
        });
    }

    // Database Connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}
