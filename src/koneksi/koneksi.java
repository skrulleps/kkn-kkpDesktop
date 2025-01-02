/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package koneksi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
/**
 *
 * @author fadhl
 */
public class koneksi {
    public static String PathReport=System.getProperty("user.dir") + "/src/report";
    private static Connection connection = null;
    public static Connection getConnection () {
        if (connection != null)
            return connection;
        else {
            String dbUrl = "jdbc:mysql://localhost:3306/db_kkn?user=root&password=";
            try {
               Class.forName("com.mysql.cj.jdbc.Driver");
               connection = DriverManager.getConnection(dbUrl);
            } catch (ClassNotFoundException | SQLException e){}
            return connection;
        }
    }
}
