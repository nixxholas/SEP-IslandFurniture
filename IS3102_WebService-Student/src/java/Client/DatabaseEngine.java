/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author nixholas
 */
public class DatabaseEngine {
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        String connURL = "jdbc:mysql://localhost:3306/islandfurniture-it07?user=root&password=12345";
        return DriverManager.getConnection(connURL);
    }
}
