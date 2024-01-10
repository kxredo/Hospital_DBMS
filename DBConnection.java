import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection{
    final static String url = "jdbc:mysql://localhost:3306/Hospital";
    final static String user = "root";
    final static String password = "dmbs@2023";
    
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