
# 📚 **Library Management System**

## 🌟 **Overview**
The **Library Management System** is a Java-based application that provides functionalities to manage books, authors, members, and loans in a library. It uses a SQL Server database to store and manage the data. The application features a user-friendly graphical user interface (GUI) built using Java Swing, allowing users to interact with the database to **Add**, **View**, **Update**, **Delete**, and **Search** records for 📖 books, ✍️ authors, 👤 members, 📅 loans, and 🔗 book-author relationships.

---

## ✨ **Features**
- 📖 **Books Management**: Add, view, edit, delete, and search books in the library.
- ✍️ **Authors Management**: Add, view, edit, delete, and search authors.
- 👤 **Members Management**: Add, view, edit, delete, and search members.
- 📅 **Loans Management**: Track book loans, including borrow and return dates.
- 🔗 **Book-Author Relationships**: Associate books with authors.

---

## 🛠️ **Technologies Used**
- ☕ **Java**: Core programming language for building the application.
- 🖥️ **Java Swing**: For the graphical user interface (GUI).
- 🗄️ **Microsoft SQL Server**: For the backend database.
- 🔌 **JDBC**: Java Database Connectivity for interacting with the SQL Server database.

---

## ✅ **Prerequisites**
Before running the application, ensure you have the following:
1. ☕ **Java Development Kit (JDK)**: Version 8 or later.
2. 🗄️ **SQL Server**: Ensure that SQL Server is installed and running. The application uses the `LibraryDB` database.
3. 🔌 **SQL Server JDBC Driver**: The application requires the **Microsoft SQL Server JDBC Driver** to connect to the database. [🔗 Download here](https://docs.microsoft.com/en-us/sql/connect/jdbc/build-microsoft-jdbc-driver-for-sql-server).

---

## 🏗️ **Setting Up the Database**
1️⃣ **Create a new database** named `LibraryDB` in SQL Server.  
2️⃣ **Create the following tables** in the `LibraryDB` database:  

   - **📚 Books**:
    ```sql
    CREATE TABLE Books (
        BookID INT PRIMARY KEY IDENTITY(1,1),
        Title VARCHAR(255),
        Genre VARCHAR(100),
        PublishedYear INT,
        ISBN VARCHAR(13)
    );

    CREATE TABLE Authors (
        AuthorID INT PRIMARY KEY IDENTITY(1,1),
        Name VARCHAR(255)
    );

    CREATE TABLE Members (
        MemberID INT PRIMARY KEY IDENTITY(1,1),
        Name VARCHAR(255),
        Email VARCHAR(255),
        Phone VARCHAR(20)
    );

    CREATE TABLE Loans (
        LoanID INT PRIMARY KEY IDENTITY(1,1),
        BookID INT FOREIGN KEY REFERENCES Books(BookID),
        MemberID INT FOREIGN KEY REFERENCES Members(MemberID),
        BorrowDate DATE,
        ReturnDate DATE
    );

    CREATE TABLE Book_Authors (
        BookID INT FOREIGN KEY REFERENCES Books(BookID),
        AuthorID INT FOREIGN KEY REFERENCES Authors(AuthorID)
    );
    ```

3️⃣ **Populate the tables with sample data** for testing purposes (optional).

---

## 🚀 **Getting Started**

### 🗂️ **Step 1:** Clone the Repository
```bash
git clone https://github.com/omar-khaled-opal/Library-Management-System.git
cd library-management-system
```

---

### 📥 **Step 2:** Install JDBC Driver
Download the **Microsoft JDBC Driver for SQL Server** (version 12.x) and add the JAR file to your project’s `lib` directory.  
If you don’t have a `lib` directory, create one and place the downloaded JAR file in it.

---

### 🔧 **Step 3:** Update Database Connection
In the `LibraryApp.java` file, update the following constants with your database credentials:
```java
static final String DB_URL = "jdbc:sqlserver://localhost:"yourhost";databaseName="yourdb";
static final String USER = "your_db_username";
static final String PASS = "your_db_password";
```

---

### ▶️ **Step 4:** Compile and Run the Application

#### 💻 On **Windows**:
```bash
cd path	o\your\project
javac -cp .;path	o\lib\mssql-jdbc-12.10.0.jre11.jar *.java
java -cp .;path	o\lib\mssql-jdbc-12.10.0.jre11.jar LibraryApp
```

#### 🍏 On **macOS**:
```bash
cd /path/to/your/project
javac -cp .:/path/to/lib/mssql-jdbc-12.10.0.jre11.jar *.java
java -cp .:/path/to/lib/mssql-jdbc-12.10.0.jre11.jar LibraryApp
```

#### 🐧 On **Linux**:
```bash
cd /path/to/your/project
javac -cp .:/path/to/lib/mssql-jdbc-12.10.0.jre11.jar *.java
java -cp .:/path/to/lib/mssql-jdbc-12.10.0.jre11.jar LibraryApp
```
