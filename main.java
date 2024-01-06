import java.io.IOException;
import java.sql.Date;  
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale.Category;



public class main{

    public int addDoctor(String firstName, String lastName, String username, String password, String email, String address, int salary, int phone, String specialty) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        try {
            connection = DBConnection.getConnection();
            String insertQuery = "INSERT INTO doctor(first_name, last_name, username, password, email, address, salary, phone, specialty) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, address);
            preparedStatement.setInt(7, salary);
            preparedStatement.setInt(8, phone);
            preparedStatement.setString(9, specialty);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new doctor was inserted.");

                generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int doctorId = generatedKeys.getInt(1);
                    System.out.println("Generated doctor ID: " + doctorId);
                    return doctorId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return -1; // Indicates failure to add a doctor
    }

    public int addNurse(String firstName, String lastName, String username, String password, String email, String address, int salary, int phone) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        try {
            connection = DBConnection.getConnection();
            String insertQuery = "INSERT INTO nurse(first_name, last_name, username, password, email, address, salary, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, address);
            preparedStatement.setInt(7, salary);
            preparedStatement.setInt(8, phone);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new nurse was inserted.");

                generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int nurseId = generatedKeys.getInt(1);
                    System.out.println("Generated nurse ID: " + nurseId);
                    return nurseId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return -1; // Indicates failure to add a nurse
    }

    public int addReceptionist(String firstName, String lastName, String username, String password, String email, String address, int salary, int phone) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        try {
            connection = DBConnection.getConnection();
            String insertQuery = "INSERT INTO receptionist(first_name, last_name, username, password, email, address, salary, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, address);
            preparedStatement.setInt(7, salary);
            preparedStatement.setInt(8, phone);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new receptionist was inserted.");

                generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int receptionistId = generatedKeys.getInt(1);
                    System.out.println("Generated receptionist ID: " + receptionistId);
                    return receptionistId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return -1; // Indicates failure to add a receptionist
    }
    

}

