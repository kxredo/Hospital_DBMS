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

import com.mysql.cj.x.protobuf.MysqlxCrud.Order;


public class main {
    final static String url = "jdbc:mysql://localhost:3306/cs202";
    final static String user = "root";
    final static String password = "dbms@2023"; //TODO: change deez nuts
   public static void main(String[] args) {
        

        executeScript("DDL.sql");
        executeScript("DML.sql");
    
   }
   //---------------------------------------------General Methods---------------------------------------------------

    public static Connection getConnection() throws SQLException {
    Connection connection = DriverManager.getConnection(url, user, password);
    if (connection == null) {
        throw new SQLException("Database connection failed.");
    }
    return connection;
    }

    private int getGeneratedKey(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        return -1;
    }

   // -------------------------------------------------USERS---------------------------------------------------------
   public int addSeller (String name, String address) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String insertQuery = "INSERT INTO seller(name, type, address) VALUES(?,'seller',?)";

            preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new record was inserted.");

                getGeneratedKey(preparedStatement);
            }
        }
         catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
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

        return -1; 
}

   public int addCustomer(String name, String address){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String insertQuery = ("INSERT INTO customer (name, type, address) VALUES(?,'customer',?)");
            preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);

            int rowsInserted = preparedStatement.executeUpdate(); 
            if (rowsInserted > 0) {
                System.out.println("A new record was inserted."); 

                getGeneratedKey(preparedStatement);
            }
        } catch(SQLException e){
            e.printStackTrace();
        } finally {
            // Close resources in a finally block to ensure they are closed
            try {
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
    return -1;
   }
   

   public ArrayList<User> listAllUsers() {
    ArrayList<User> users = new ArrayList<>();
    try {
        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM user");

        while (rs.next()) {
            int userId = rs.getInt("user_id");
            String userName = rs.getString("user_name");
            String userType = rs.getString("user_type");
            String userAddress = rs.getString("user_address");

            User user = new User(userId, userName, userType, userAddress);
            users.add(user);
        }

        rs.close();
        stmt.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
     return users;

   }

   public boolean addRemovePaymentMethod(int userId, String type, String cardNumber) throws SQLException {
    Connection connection = null;
    try {
        // Get the database connection
        connection = getConnection();
        
        // Check if the user is a customer
        boolean isCustomer = isCustomer(userId);
        if (!isCustomer) {
            throw new SQLException("User is not a customer.");
        }

        // If adding a card, make sure the card does not already exist
        if (type.equalsIgnoreCase("add")) {
            boolean cardExists = doesCardExist(userId, cardNumber);
            if (cardExists) {
                throw new SQLException("Card is already added.");
            }
        }

        // If removing a card, make sure the card exists
        if (type.equalsIgnoreCase("remove")) {
            boolean cardExists = doesCardExist(userId, cardNumber);
            if (!cardExists) {
                throw new SQLException("Card to be removed not found.");
            }
        }

        // Perform the add or remove card operation
        String query;
        if (type.equalsIgnoreCase("add")) {
            query = "INSERT INTO PaymentMethod (customerID, methodType, cardNumber) VALUES (?, ?, ?)";
        } else {
            query = "DELETE FROM PaymentMethod WHERE customerID = ? AND cardNumber = ?";
        }

        // Create a PreparedStatement
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, preparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, type);
            preparedStatement.setString(3, cardNumber);

            // Execute the query
            int affectedRows = preparedStatement.executeUpdate();

            // Return true if the operation was successful, false otherwise
            return affectedRows > 0;
        }
    } catch (SQLException e) {
        throw e;
    }
    finally {
        // Close the connection in the finally block
        if (connection != null) {
            connection.close();
        }}
}

    // Helper method to check if the user is a customer
    private boolean isCustomer(int userId) throws SQLException {
        try {
            Connection connection = getConnection();

            String query = "SELECT * FROM User WHERE user_id = ? AND user_type = 'customer'";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);

                ResultSet resultSet = preparedStatement.executeQuery();

                return resultSet.next(); // Returns true if there is a result in the ResultSet, false otherwise
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    // Helper method to check if a specific card for the user already exists
    private boolean doesCardExist(int userId, String cardNumber) throws SQLException {
        try {
            Connection connection = getConnection();

            String query = "SELECT * FROM PaymentMethod WHERE customerID = ? AND cardNumber = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setString(2, cardNumber);

                ResultSet resultSet = preparedStatement.executeQuery();

                return resultSet.next(); // Returns true if there is a result in the ResultSet, false otherwise
            }
        } catch (SQLException e) {
            throw e;
        }
    }


    public ArrayList<String> getPaymentMethodsOfUser(int userId){

        ArrayList<String> paymentMethodsOfUser = new ArrayList<>();
        try {
            Connection connection = getConnection();
            String query =                        ("SELECT pm.methodType " +
                                                   "FROM paymentMethod pm, customer c " +
                                                   "WHERE c.customerID = pm.customerID AND c.customerID = ?"
                                                );

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String methodType = resultSet.getString("methodType");
                        paymentMethodsOfUser.add(methodType);
                    }
                }
            }


            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paymentMethodsOfUser;

    }

//------------------------------------------------PRODUCTS & ORDERS---------------------------------------------------
    
    public int addRemoveCategory(String name, String type) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String query;

        try {
            connection = getConnection();
            Category existingCategory = getCategoryByName(name);

                if (type.equals("add")) {
                    // If adding, and the category already exists, return -1
                    if (existingCategory !=null) {
                        System.out.println("Category already exists.");
                        return -1;
                    }
                    else {
                        query = "INSERT INTO Category (categoryName) VALUES (?)";

                    }
                } else if (type.equals("remove")) {
                    //if removing but the category dne -> return -1 
                    if (existingCategory == null) {
                        System.out.println("Category not found.");
                        return -1;
                    }
                    else {
                        query = "DELETE FROM Category WHERE categoryName = ?";
                        
                    }
                }

                preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, name);
                int rowsAffected = preparedStatement.executeUpdate();

                // If adding, retrieve the generated category ID
                if (type.equals("add") && rowsAffected > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int categoryId = generatedKeys.getInt(1);
                            return categoryId;
                        }
                    }
                }

                // If removing, return the existing category
                if (type.equals("remove") && existingCategory != null) {
                    return categoryId = existingCategory.getCategoryId();
    
                }
            }
         catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
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

        return -1; // Return null for any unexpected errors
    }

    // Helper method to get category by name
    private Category getCategoryByName(String categoryName) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
                String query = "SELECT * FROM Category WHERE categoryName = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, categoryName);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int categoryId = resultSet.getInt("categoryID");
                    return new Category(categoryId, categoryName);
            
                }else {
                    return null; // Category not found
                }
            }
         finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
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

            return null;
        }
    }

    public ArrayList<Category> getCategories(){
        ArrayList<Category> categories = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                String query = "SELECT * FROM Category";
                preparedStatement = connection.prepareStatement(query);
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int categoryId = resultSet.getInt("categoryID");
                    String categoryName = resultSet.getString("categoryName");
                    Category category = new Category(categoryId, categoryName);
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
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

        return categories;

    }

    public int addProduct(String name, String description, int categoryID ){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try{
            connection = DriverManager.getConnection(url, user, password);
            if(connection!=null){
                String insertQuery = "INSERT INTO Product(name, description, categoryID) VALUES(?,?,?)";

                preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, description);
                preparedStatement.setInt(3, categoryID);

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("A new record was inserted.");
                    return getGeneratedKey(preparedStatement);
                }
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            try {
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
        return -1;
    }

    public boolean removeProduct(int productId){
        PreparedStatement preparedStatement = null;
        try{
            Connection connection = getConnection();

            String listingDelete = "DELETE FROM Listing WHERE productID IN (SELECT productID FROM Product WHERE productID = ?)";
            try (PreparedStatement listingStatement = connection.prepareStatement(listingDelete)) {
                listingStatement.setInt(1, productId);
                listingStatement.executeUpdate();
            }
    
            // Then, delete the product
            String productDelete = "DELETE FROM Product WHERE productID = ?";
            try (PreparedStatement productStatement = connection.prepareStatement(productDelete)) {
                productStatement.setInt(1, productId);
                int affectedRows = productStatement.executeUpdate();
                return affectedRows > 0;
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return false;

    }

    public ArrayList<Product> listProducts() {
        ArrayList<Product> products = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Product");
             ResultSet resultSet = preparedStatement.executeQuery()) {
    
            while (resultSet.next()) {
                int productId = resultSet.getInt("productID");
                String productName = resultSet.getString("name");
                Product product = new Product(productId, productName);
                products.add(product);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    

    public int createListing(int productID, int sellerID, int price, int stock){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        if(listingExists(productID, sellerID)){
            throw new SQLException("Listing Already Exists :>");
        }

        try{
            connection = getConnection();
                String insertQuery = "INSERT INTO Listing(productID, sellerID, price, stock) Values(?,?,?,?)";
                preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, productID);
                preparedStatement.setInt(2, sellerID);
                preparedStatement.setInt(3, price);
                preparedStatement.setInt(4, stock);

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("A new record was inserted.");
                    getGeneratedKey(preparedStatement);
                }
            }
        
        catch (SQLException e){
            e.printStackTrace();
        }
        finally{
             try {
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
        return -1;
    }

    private boolean listingExists(int productID, int sellerID){

        try {
            Connection connection = getConnection();

            String query = "SELECT * FROM Listing WHERE productID = ? AND sellerID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, productID);
                preparedStatement.setInt(2, sellerID);

                ResultSet resultSet = preparedStatement.executeQuery();

                return resultSet.next(); // Returns true if there is a result in the ResultSet, false otherwise
            }
        } catch (SQLException e) {
            throw e;
        }
        return false;

    }

    public ArrayList<Listing> getListingsOfSeller(int sellerID) {
        ArrayList<Listing> listings = new ArrayList<>();
    
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM Listing WHERE sellerID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, sellerID);
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int listingId = resultSet.getInt("listingId");
                        int productId = resultSet.getInt("productId");
                        int price = resultSet.getInt("price");
                        int stock = resultSet.getInt("stock");
    
                        // Assuming you have a Listing class, create an instance and add it to the list
                        Listing listing = new Listing(listingId, sellerID, productId, price, stock);
                        listings.add(listing);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return listings;
    }

    public Listing getListing(int listingId) {
    
        try {
            Connection connection = getConnection();
            String query = "SELECT * FROM Listing WHERE listingId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, listingId);
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int sellerId = resultSet.getInt("sellerId");
                        int productId = resultSet.getInt("productId");
                        int price = resultSet.getInt("price");
                        int stock = resultSet.getInt("stock");
    
                        // Create a new Listing object
                        Listing listing = new Listing(listingId, sellerId, productId, price, stock);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return null;
    }
    

    public boolean buyListing(int listingId, int userId) {
        try {
            Connection connection = getConnection();
            Listing listing = getListing(listingId);
            if (listing == null || listing.getStock() == 0) {
                return false; // Return false if the listing dne or 0 stocks
            }
    
            // do purchase
            String updateQuery = "UPDATE Listing SET stock = stock - 1 WHERE listingId = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, listingId);
    
                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) {
                    return true;
                }
                
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    
        return false; 
    }


    public ArrayList<Order> getOrdersOfUser(int userId) {
    ArrayList<Order> orders = new ArrayList<>();

    try (Connection connection = getConnection()) {
        String query = "SELECT * FROM Order WHERE userId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int orderId = resultSet.getInt("orderId");
                
                    Order order = getOrderDetails(orderId);

                    if (order != null) {
                        orders.add(order);
                    }
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return orders;
}




    public Order getOrderDetails(int orderId) throws SQLException {
        Order order = null;

        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM Order WHERE orderId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, orderId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int listingId = resultSet.getInt("listingId");
                        int userId = resultSet.getInt("userId");
                        Date date = resultSet.getDate("date");

                        order = new Order(orderId, listingId, userId, date);
                    }
                }
            }
        } catch (SQLException e) {
            throw e; 
        }

        return order;
    }

//-------------------------------------------------Statistics ----------------------------------------------------
  



    
    
}    
