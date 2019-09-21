package db;

import java.sql.*;

public class DBConnection {

    private static DBConnection dbConnection;
    private Connection connection;

    private DBConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library?createDatabaseIfNotExist=true&allowMultiQueries=true", "root", "root");
            PreparedStatement pstm = connection.prepareStatement("SHOW TABLES");
            ResultSet resultSet = pstm.executeQuery();
            if (!resultSet.next()) {
                String sql = "CREATE TABLE book ( bookid VARCHAR(25) PRIMARY KEY,\n" +
                        "\t\t    title  VARCHAR(25),\n" +
                        "\t\t    author VARCHAR(25),\n" +
                        "\t            price DOUBLE(10,2),\n" +
                        "\t\t    status VARCHAR(25));\n" +
                        "\n" +
                        "\n" +
                        "CREATE TABLE library_member ( memberid VARCHAR(20) PRIMARY KEY,\n" +
                        "\t\t      membername VARCHAR(20),\n" +
                        "\t\t      memberaddress VARCHAR(20));\n" +
                        "\t\t       \n" +
                        "\n" +
                        "CREATE TABLE issue (issueid VARCHAR(20) PRIMARY KEY,\n" +
                        "\t\t    date DATE,\n" +
                        "\t\t    book_id VARCHAR(25),\n" +
                        "\t\t    member_id VARCHAR(20),\n" +
                        "       CONSTRAINT FOREIGN KEY(book_id) REFERENCES book(bookid)\n" +
                        "       ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                        "       CONSTRAINT FOREIGN KEY(member_id) REFERENCES  library_member(memberid)\n" +
                        "       ON DELETE CASCADE ON UPDATE CASCADE);\n" +
                        "\t\t    \n" +
                        "\n" +
                        "\n" +
                        "CREATE TABLE book_return (\n" +
                        " \tissue_id VARCHAR(20) PRIMARY KEY REFERENCES issue(issueid)\n" +
                        "\t\t\t ON DELETE CASCADE ON UPDATE CASCADE, \n" +
                        "\treturnfine DOUBLE(10,2),\n" +
                        "\treturn_date Date \n" +
                        "\n" +
                        ");";
                pstm = connection.prepareStatement(sql);
                pstm.execute();
                System.out.println("--Auto generation process completed.--");
            } else {
                System.out.println("--Database and all tables are already exist.--");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DBConnection getInstance() {
        return (dbConnection == null) ? (dbConnection = new DBConnection()) : dbConnection;
    }

    public Connection getConnection() {
        return connection;
    }



}
