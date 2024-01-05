import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection{
    final static String url = "jdbc:mysql://localhost:3306/mydb";
    final static String user = "username";
    final static String password = "password";
    
    public static Connection getConnection() {
    
        try {
            Connection con = DriverManager.getConnection(url, user, password);
            if(con != null){
                System.out.println("Connected to the database");
                return con; 
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
        
    }


    // public static Connection getConnection() throws SQLException {
    // Connection connection = DriverManager.getConnection(url, user, password);
    // if (connection == null) {
    //     throw new SQLException("Database connection failed.");
    // }
    // return connection;
    // }
}