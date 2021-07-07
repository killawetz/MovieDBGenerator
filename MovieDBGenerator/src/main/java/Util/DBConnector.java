package Util;

import java.sql.*;

public class DBConnector {

    public static Connection connection;

    public static void getDBConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres?characterEncoding=cp1251",
                    "postgres",
                    "qwerty");

        } catch (SQLException throwables) {
            System.out.println("Connection Failed");
            throwables.printStackTrace();
        }

        if (connection != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
    }
}


