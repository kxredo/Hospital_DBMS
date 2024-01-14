import javax.swing.*;
import java.util.Date;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;



public class View extends JFrame {
    //private Map<String, String> userDatabase; 
    private Map<String, String> appointmentData;
    private Map<String, String> billData = new HashMap<>();  
    private JTextField usernameField;
    private JPasswordField passwordField;
    private String selectedRole;


    private static String hashPassword(String password) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(
                password.getBytes(StandardCharsets.UTF_8));

        // Convert the byte array to a hexadecimal string
        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
        // Handle the exception (e.g., log it, print a message, or throw a runtime exception)
        e.printStackTrace(); // Example: printing the stack trace
        // You might also throw a runtime exception or return a default value
        throw new RuntimeException("Hashing algorithm not available", e);
    }
}


    public View() {
        super("Hospital Management System");
        //userDatabase = new HashMap<>();
        appointmentData = new HashMap<>();
        
      

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        createUI();
        
    }

    private void createUI() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
    }
    private String getUserRole(String username) {
        try {
            Connection connection = DBConnection.getConnection();
    
            if (connection != null) {
                String query = "SELECT role FROM User WHERE username = ?";
    
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);
    
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        // If a row is returned, retrieve the role
                        if (resultSet.next()) {
                            return resultSet.getString("role");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return null;
    }

private void login() {
    String username = usernameField.getText();
    char[] passwordChars = passwordField.getPassword();
    String password = new String(passwordChars);

        if (verifyLogin(username, password)) {
            // User authentication successful
            String userRole = getUserRole(username);
            System.out.println("User role: " + userRole);

            selectedRole = userRole;
            userInterface(userRole, username);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
        }
   

    // Clear fields after login 
    usernameField.setText("");
    passwordField.setText("");
}




private boolean verifyLogin(String username, String password) {
    try {
        // Establish database connection
        Connection connection = DBConnection.getConnection();

        if (connection != null) {
            // Execute a query to check if the username and hashed password match
            String query = "SELECT * FROM User WHERE username = ? AND password = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, hashPassword(password)); // Hash the entered password

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // If a row is returned, the login is successful
                    return resultSet.next();
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}

    
private void register() {
    String username;
    do {
        username = JOptionPane.showInputDialog(this, "Enter a new username:");

        if (username == null) {
            // User clicked Cancel, exit registration
            return;
        }

        // Check if the username already exists in the database
        if (userExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.", "Duplicate Username", JOptionPane.ERROR_MESSAGE);
        }

    } while (userExists(username));

    // Validate and set a password with a valid length
    String password;
    do {
        password = JOptionPane.showInputDialog(this, "Enter a password (8-127 characters):");

        if (password == null) {
            // User clicked Cancel, exit registration
            return;
        }

        if (password.length() < 8 || password.length() > 127) {
            JOptionPane.showMessageDialog(this, "Password must be between 8 and 127 characters.", "Invalid Password", JOptionPane.ERROR_MESSAGE);
        }

    } while (password.length() < 8 || password.length() > 127);

    // Continue with the registration process
    if (username != null && password != null) {
        try {
            // Establish database connection
            Connection connection = DBConnection.getConnection();

            if (connection != null) {
                // Prepare the SQL query to insert a new user
                String insertQuery = "INSERT INTO User (username, password) VALUES (?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, hashPassword(password));

                    // Execute the query to insert the new user
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        registrationForm(username);  // Directly show the registration form
    }
}

private boolean userExists(String username) {
  
    return  userExistsInDatabase(username);
}

private boolean userExistsInDatabase(String username) {
    try {
        // Establish database connection
        Connection connection = DBConnection.getConnection();

        if (connection != null) {
            // Execute a query to check if the username already exists in the database
            String query = "SELECT * FROM User WHERE username = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // If a row is returned, the username already exists
                    return resultSet.next();
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace(); // Handle exceptions according to your application's requirements
    }

    return false;
}



    private void registrationForm(String username) {
        JFrame registrationFrame = new JFrame("User Registration");
        registrationFrame.setSize(400, 400);
        registrationFrame.setLocationRelativeTo(null);
    
        JPanel panel = new JPanel(new GridLayout(11, 1, 10, 10));
    
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField();
    
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField();
    
        JLabel genderLabel = new JLabel("Gender:");
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});

        JLabel dayLabel = new JLabel("Day of birth:");
        JComboBox<String> dayComboBox = new JComboBox<>(new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"});

        JLabel monthLabel = new JLabel("Month of birth:");
        JComboBox<String> monthComboBox = new JComboBox<>(new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"});

        
        JLabel yearLabel = new JLabel("Year of birth:");
        JComboBox<String> yearComboBox = new JComboBox<>();
        for (int year = 1900; year <= 2024; year++) {
            yearComboBox.addItem(String.valueOf(year));
}

        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        JTextField phoneNumberField = new JTextField();
    
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
    
        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField();
    
        JButton submitButton = new JButton("Submit");
    
        panel.add(firstNameLabel);
        panel.add(firstNameField);
        panel.add(lastNameLabel);
        panel.add(lastNameField);
        panel.add(dayLabel);
        panel.add(dayComboBox);
        panel.add(monthLabel);
        panel.add(monthComboBox);
        panel.add(yearLabel);
        panel.add(yearComboBox);
        panel.add(genderLabel);
        panel.add(genderComboBox);
        panel.add(phoneNumberLabel);
        panel.add(phoneNumberField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(addressLabel);
        panel.add(addressField);
        panel.add(new JLabel());  // Empty label as a placeholder
        panel.add(submitButton);
    
        registrationFrame.add(panel);
        registrationFrame.setVisible(true);
    
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                // Retrieve values from text fields and combo boxes
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String dayOfBirth = (String) dayComboBox.getSelectedItem();
                String monthOfBirth = (String) monthComboBox.getSelectedItem();
                String yearOfBirth = (String) yearComboBox.getSelectedItem();
                String gender = (String) genderComboBox.getSelectedItem();
                String phoneNumber = phoneNumberField.getText();
                String email = emailField.getText();
                String address = addressField.getText();
        
                // Check if any of the fields are empty
                if (firstName.isEmpty()) {
                    showError("First name is empty.");
                    return;
                }
        
                if (lastName.isEmpty()) {
                    showError("Last name is empty.");
                    return;
                }
        
                if (dayOfBirth.isEmpty() || monthOfBirth.isEmpty() || yearOfBirth.isEmpty()) {
                    showError("Date of birth is incomplete.");
                    return;
                }
        
                if (gender.isEmpty()) {
                    showError("Gender is not selected.");
                    return;
                }
        
                if (phoneNumber.isEmpty()) {
                    showError("Phone number is empty.");
                    return;
                }
        
                // Check if phone number contains only numbers
                if (!phoneNumber.matches("\\d+")) {
                    showError("Phone number should contain numbers only.");
                    return;
                }
        
                if (email.isEmpty()) {
                    showError("Email is empty.");
                    return;
                }
                if (!email.contains("@")) {
                    showError("Email is invalid.");
                    return;
                }
        
                if (address.isEmpty()) {
                    showError("Address is empty.");
                    return;
                }
        
                // Save the user details to the database or perform necessary actions
                JOptionPane.showMessageDialog(registrationFrame, "Registration successful ", " ", JOptionPane.INFORMATION_MESSAGE);
                registrationFrame.dispose();  // Close the registration frame
            }
        
            private void showError(String message) {
                JOptionPane.showMessageDialog(registrationFrame, message, "Incomplete Information", JOptionPane.ERROR_MESSAGE);
            }
        });
        
    }
    
    private void userInterface(String role, String username) {
        if (role.equals("Patient")) {
            patientInterface(username);
        } else if (role.equals("Doctor")) {
            doctorInterface(username);
        } else if (role.equals("Nurse")) {
            nurseInterface(username);
        } else {
            JOptionPane.showMessageDialog(this, "Error.", "Login Unsuccessful.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
// -------------------------------PATIENT INTERFACE ---------------------------------
    private void patientInterface(String username) {
        JFrame patientFrame = new JFrame("Patient Interface");
        patientFrame.setSize(400, 300);
        patientFrame.setLocationRelativeTo(null);
    
        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));
    
        JButton scheduleAppointmentButton = new JButton("Schedule Appointment");
        JButton viewAppointmentsButton = new JButton("View Appointments");
        JButton viewBillsButton = new JButton("View Bills");
        JButton searchDoctorsButton = new JButton("Search Doctors");
        JButton signOutButton = new JButton("Sign Out");
    
        panel.add(scheduleAppointmentButton);
        panel.add(viewAppointmentsButton);
        panel.add(viewBillsButton);
        panel.add(searchDoctorsButton);
        panel.add(signOutButton);
    
        patientFrame.add(panel);
        patientFrame.setVisible(true);
    
        viewAppointmentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAppointmentsFunction();
            }
        });
    
        scheduleAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scheduleAppointmentFunction();
            }
        });
    
        viewBillsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewBillsFunction();
            }
        });
    
        searchDoctorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchDoctorsFunction();
            }
        });
    
    
        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add logic to handle "Sign Out" button click
                JOptionPane.showMessageDialog(patientFrame, "Signed Out");
                patientFrame.dispose();  // Close the patient frame
            }
        });
    }

    private void searchDoctorsFunction() {
    // Create a frame for searching doctors
    JFrame searchDoctorsFrame = new JFrame("Search Doctors");
    searchDoctorsFrame.setSize(400, 200);
    searchDoctorsFrame.setLocationRelativeTo(null);

    // Create a panel for input fields and buttons
    JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

    JLabel expertiseLabel = new JLabel("Field of Expertise:");
    JTextField expertiseField = new JTextField();

    JButton searchButton = new JButton("Search");
    JButton cancelButton = new JButton("Cancel");

    panel.add(expertiseLabel);
    panel.add(expertiseField);
    panel.add(new JLabel()); 
    panel.add(new JLabel()); 
    panel.add(searchButton);
    panel.add(cancelButton);

    searchDoctorsFrame.add(panel);
    searchDoctorsFrame.setVisible(true);

    searchButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Retrieve user input
            String fieldOfExpertise = expertiseField.getText();

            // Validate input
            if (fieldOfExpertise.isEmpty()) {
                showError("Field of Expertise is empty.");
                return;
            }

            // Perform the search based on the input
            List<String[]> doctorList = searchDoctors(fieldOfExpertise);

            // Display the search results
            displayDoctorSearchResults(doctorList);

            // Close the search frame
            searchDoctorsFrame.dispose();
        }
    });

    cancelButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Close the search frame without performing the search
            searchDoctorsFrame.dispose();
        }
    });
}
private void showError(String message) {
    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
}

private List<String[]> searchDoctors(String fieldOfExpertise) {
    List<String[]> doctorList = new ArrayList<>();

    try {
        // Establish database connection
        Connection connection = DBConnection.getConnection();

        if (connection != null) {
            // Execute the search query with a JOIN operation
            String query = "SELECT Doctor.doctor_id, Doctor.specialty, Employee.name " +
                           "FROM Doctor " +
                           "JOIN Employee ON Doctor.doctor_id = Employee.emp_id " +
                           "WHERE Doctor.specialty = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, fieldOfExpertise);
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // Iterate over the result set and add doctor information to the list
                    while (resultSet.next()) {
                        String doctorId = resultSet.getString("doctor_id");
                        String name = resultSet.getString("name");
                        String specialty = resultSet.getString("specialty");
                        
    
                        doctorList.add(new String[]{doctorId, name, specialty});
                    }
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return doctorList;
}

private void displayDoctorSearchResults(List<String[]> doctorList) {
    JFrame resultsFrame = new JFrame("Search Results");
    resultsFrame.setSize(400, 200);
    resultsFrame.setLocationRelativeTo(null);

    JPanel panel = new JPanel(new GridLayout(doctorList.size() + 1, 3, 10, 10));

    panel.add(new JLabel("Doctor ID"));
    panel.add(new JLabel("Name"));
    panel.add(new JLabel("Specialty"));
    

    // Iterate over the doctor list and display information
    for (String[] doctorDetails : doctorList) {
        for (String detail : doctorDetails) {
            panel.add(new JLabel(detail));
        }
    }

    resultsFrame.add(new JScrollPane(panel));
    resultsFrame.setVisible(true);
}

private void scheduleAppointmentFunction() {
    JFrame appointmentFrame = new JFrame("Schedule Appointment");
    appointmentFrame.setSize(400, 200);
    appointmentFrame.setLocationRelativeTo(null);

    JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));

    JLabel dateLabel = new JLabel("Date: (dd/mm/yy)");
    JTextField dateField = new JTextField();

    JLabel timeLabel = new JLabel("Time: (hr:min)");
    JTextField timeField = new JTextField();

    JLabel concernsLabel = new JLabel("Concerns:");
    JTextField concernsField = new JTextField();

    JLabel symptomsLabel = new JLabel("Symptoms:");
    JTextField symptomsField = new JTextField();

    JButton submitButton = new JButton("Submit");

    panel.add(dateLabel);
    panel.add(dateField);
    panel.add(timeLabel);
    panel.add(timeField);
    panel.add(concernsLabel);
    panel.add(concernsField);
    panel.add(symptomsLabel);
    panel.add(symptomsField);
    panel.add(new JLabel()); 
    panel.add(submitButton);

    appointmentFrame.add(panel);
    appointmentFrame.setVisible(true);

    submitButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Retrieve user inputs
            String date = dateField.getText();
            String time = timeField.getText();
            String concerns = concernsField.getText();
            String symptoms = symptomsField.getText();

            try {
                // Establish database connection
                Connection connection = DBConnection.getConnection();

                if (connection != null) {
                    // Prepare the SQL query to insert a new appointment
                    String insertQuery = "INSERT INTO Appointments (date, time, concerns, symptoms) VALUES (?, ?, ?, ?)";

                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                        preparedStatement.setString(1, date);
                        preparedStatement.setString(2, time);
                        preparedStatement.setString(3, concerns);
                        preparedStatement.setString(4, symptoms);

                        // Execute the query to insert the new appointment
                        preparedStatement.executeUpdate();
                    }
                }

                JOptionPane.showMessageDialog(appointmentFrame, "Appointment scheduled:\nDate/Time: " + date + " " + time + "\nConcerns: " + concerns + "\nSymptoms: " + symptoms, "Success", JOptionPane.INFORMATION_MESSAGE);
                appointmentFrame.dispose(); // Close the appointment frame
            } catch (SQLException ex) {
                ex.printStackTrace(); // Handle exceptions according to your application's requirements
                JOptionPane.showMessageDialog(appointmentFrame, "Error scheduling appointment", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });
}

private void viewAppointmentsFunction() {
    JFrame appointmentsFrame = new JFrame("View Appointments");
    appointmentsFrame.setSize(600, 400);
    appointmentsFrame.setLocationRelativeTo(null);

    // Create a panel for displaying appointment information
    JPanel panel = new JPanel(new GridLayout(appointmentData.size() + 1, 7, 10, 10));

    panel.add(new JLabel("Date"));
    panel.add(new JLabel("Start Time"));
    panel.add(new JLabel("End Time"));
    panel.add(new JLabel("Concerns"));
    panel.add(new JLabel("Symptoms"));
    panel.add(new JLabel("Status"));
    panel.add(new JLabel("Actions"));

    // Add a filter panel with filter options
    JPanel filterPanel = new JPanel();
    JLabel filterLabel = new JLabel("Filter Appointments:");
    JTextField daysFilterField = new JTextField("0"); // Default filter: 0 days (show all)
    JComboBox specialtyFilterComboBox = new JComboBox<>(new String[]{"Cardiology", "Neurology", "Urology", "Orthopedics", "Dermatology", "Opthalmology", "Gastroenterology", "Obstetrics & Gynecology", "Psychiatry", "Pediatrics", "Endocrinology"}); // Default filter: Cardiology
    JButton applyFilterButton = new JButton("Apply Filter");

    filterPanel.add(filterLabel);
    filterPanel.add(new JLabel("Days:"));
    filterPanel.add(daysFilterField);
    filterPanel.add(new JLabel("Specialty:"));
    filterPanel.add(specialtyFilterComboBox);
    filterPanel.add(applyFilterButton);

    applyFilterButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            applyFilter(Integer.parseInt(daysFilterField.getText()), (String) specialtyFilterComboBox.getSelectedItem());

        }
    });

    // Add filter panel to the appointmentsFrame
    appointmentsFrame.add(filterPanel, BorderLayout.NORTH);

    // Iterate over appointment data and display information
    for (Map.Entry<String, String> entry : appointmentData.entrySet()) {
        String appointmentInfo = entry.getValue();
        String[] appointmentDetails = appointmentInfo.split(";");

        for (String detail : appointmentDetails) {
            panel.add(new JLabel(detail));
        }

        JButton seeDiagnosisButton = new JButton("See Diagnosis");
        JButton cancelAppointmentButton = new JButton("Cancel/Delete");

        panel.add(seeDiagnosisButton);
        panel.add(cancelAppointmentButton);

        seeDiagnosisButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve the appointment ID and patient name from appointmentDetails
                int appointmentId = Integer.parseInt(appointmentDetails[4]);
                String patientName = appointmentDetails[3];
    
                // Implement logic to fetch and display the diagnosis for the given appointment
                String diagnosis = getDiagnosis(appointmentId, patientName);
    
                // Display the diagnosis in a dialog
                JOptionPane.showMessageDialog(appointmentsFrame, "Diagnosis for " + patientName + ":\n" + diagnosis);
            }
        });

        cancelAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement logic for "Cancel/Delete" button
                String selectedDate = appointmentDetails[0];
                appointmentData.remove(selectedDate);
                appointmentsFrame.dispose();
                viewAppointmentsFunction(); // Refresh the view after cancellation
            }
        });
    }

    appointmentsFrame.add(new JScrollPane(panel), BorderLayout.CENTER);
    appointmentsFrame.setVisible(true);
}

// Assuming you have a class named Appointment with appropriate fields
private List<Appointment> getAppointmentsFromDatabase() {
    List<Appointment> appointments = new ArrayList<>();

    try {
        // Establish database connection
        Connection connection = DBConnection.getConnection();

        if (connection != null) {
            // Prepare the SQL query to retrieve appointments
            String selectQuery = "SELECT * FROM Appointments";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // Iterate over the result set and populate the list of appointments
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        // int date = resultSet.getString("date");
                        String start_time = resultSet.getString("start_time");
                        String end_time = resultSet.getString("end_time");
                        String concerns = resultSet.getString("concerns");
                        String symptoms = resultSet.getString("symptoms");
                        String status = resultSet.getString("status");
                        String patientName = resultSet.getString("patient_name");

                        // Create an Appointment object and add it to the list
                        Appointment appointment = new Appointment(id, start_time, end_time, concerns, symptoms, status, patientName);
                        appointments.add(appointment);
                    }
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace(); // Handle exceptions according to your application's requirements
    }

    return appointments;
}


private String getDiagnosis(int appointmentId, String patientName) {
    try (Connection connection = DBConnection.getConnection()) {
        if (connection != null) {
            // Execute a query to get the diagnosis for the specified appointment and patient
            String query = "SELECT diagnosis_text FROM Diagnosis WHERE appointment_id = ? AND patient_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, appointmentId);
                preparedStatement.setString(2, patientName);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Return the diagnosis if found
                        return resultSet.getString("diagnosis");
                    }
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Return an empty string if the diagnosis is not found or an error occurs
    return "";
}


private void applyFilter(int days, String specialty) {
    System.out.println("Filter Applied - Days: " + days + ", Specialty: " + specialty);
}

private void viewBillsFunction() {
    // Create a frame for viewing bills
    JFrame billsFrame = new JFrame("View Bills");
    billsFrame.setSize(600, 400);
    billsFrame.setLocationRelativeTo(null);

    // Create a panel for displaying bill information
    JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));

    JLabel dateLabel = new JLabel("Date:");
    JLabel feesLabel = new JLabel("Fees:");
    JLabel statusLabel = new JLabel("Status:");
    JLabel billNumLabel = new JLabel("Bill number:");

    panel.add(dateLabel);
    panel.add(feesLabel);
    panel.add(statusLabel);
    panel.add(billNumLabel);

    try (Connection connection = DBConnection.getConnection()) {
        if (connection != null) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Bill");
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    // Extract data from the result set
                    int billNumber = resultSet.getInt("bill_no");
                    int fees = resultSet.getInt("fees");
                    boolean status = resultSet.getBoolean("status");
                    Date date = resultSet.getDate("date");

                    // Display bill details in the panel
                    panel.add(new JLabel(date.toString()));
                    panel.add(new JLabel(Integer.toString(fees)));
                    panel.add(new JLabel(getBillStatus(status)));
                    panel.add(new JLabel(Integer.toString(billNumber)));
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Add the panel to the frame
    billsFrame.add(new JScrollPane(panel));
    billsFrame.setVisible(true);
}

// Example method to get bill status based on status flag
private String getBillStatus(boolean status) {
    return status ? "Paid" : "Unpaid";
}

// -------------------------------DOCTOR INTERFACE -------------------------------

    private void doctorInterface(String username) {
        JFrame doctorFrame = new JFrame("Doctor Interface");
        doctorFrame.setSize(400, 300);
        doctorFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton viewPatientsButton = new JButton("View Patients");
        JButton roomsAvailabilityButton = new JButton("Rooms availability");
        JButton viewAppointmentsButton = new JButton("View Appointments");
        JButton scheduleAppointmentButton = new JButton("Schedule Appointment");
        JButton signOutButton = new JButton("Sign Out");

        panel.add(roomsAvailabilityButton);
        panel.add(viewAppointmentsButton);
        panel.add(scheduleAppointmentButton);
        panel.add(signOutButton);

        doctorFrame.add(panel);
        doctorFrame.setVisible(true);

        viewPatientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewPatientsFunction(username);
            }
        });

        roomsAvailabilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                roomsAvailabilityFunction();            }
        });

        viewAppointmentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAppointmentsFunctionForDoctor();            }
        });

        scheduleAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scheduleAppointmentFunctionForDoctor();
            }
        });

        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add logic to handle "Sign Out" button click
                JOptionPane.showMessageDialog(doctorFrame, "Signed Out");
                doctorFrame.dispose();  // Close the doctor frame
            }
        });
    }

    private int getDoctorIdByUsername(String doctorUsername) throws SQLException {
        int doctorId = -1;
    
        try (Connection connection = DBConnection.getConnection()) {
            if (connection != null) {
                String query = "SELECT doctor_id FROM Doctor WHERE username = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, doctorUsername);
    
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            doctorId = resultSet.getInt("doctor_id");
                        }
                    }
                }
            }
        }
    
        return doctorId;
    }
    

    private void viewPatientsFunction(String doctorUsername) {
        // Create a frame for viewing patients and appointments
        JFrame patientsFrame = new JFrame("View Patients");
        patientsFrame.setSize(800, 600);
        patientsFrame.setLocationRelativeTo(null);
    
        // Create a panel for displaying patient and appointment information
        JPanel panel = new JPanel(new GridLayout(1, 10, 10, 10));
    
        // Add headers for patient and appointment details
        panel.add(new JLabel("Patient ID"));
        panel.add(new JLabel("Patient Name"));
        panel.add(new JLabel("Age"));
        panel.add(new JLabel("Gender"));
        panel.add(new JLabel("Appointment ID"));
        panel.add(new JLabel("Start Time"));
        panel.add(new JLabel("End Time"));
        panel.add(new JLabel("Concerns"));
        panel.add(new JLabel("Diagnose"));
    
        try (Connection connection = DBConnection.getConnection()) {
            if (connection != null) {
                // Retrieve doctor's ID based on the username
                int doctorId = getDoctorIdByUsername(doctorUsername);
    
                // Execute the query to get patient and appointment information
                String query = "SELECT P.patient_id, P.name, P.age, P.gender, A.appointment_id, A.start_time, A.end_time, A.concerns " +
                               "FROM Patient P " +
                               "JOIN Appointment A ON P.patient_id = A.patient_id " +
                               "WHERE A.doctor_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, doctorId);
    
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            int patientId = resultSet.getInt("patient_id");
                            String patientName = resultSet.getString("name");
                            int patientAge = resultSet.getInt("age");
                            String patientGender = resultSet.getString("gender");
                            int appointmentId = resultSet.getInt("appointment_id");
                            Timestamp startTime = resultSet.getTimestamp("start_time");
                            Timestamp endTime = resultSet.getTimestamp("end_time");
                            String concerns = resultSet.getString("concerns");
    
                            // Add patient and appointment information to the panel
                            panel.add(new JLabel(Integer.toString(patientId)));
                            panel.add(new JLabel(patientName));
                            panel.add(new JLabel(Integer.toString(patientAge)));
                            panel.add(new JLabel(patientGender));
                            panel.add(new JLabel(Integer.toString(appointmentId)));
                            panel.add(new JLabel(startTime.toString()));
                            panel.add(new JLabel(endTime.toString()));
                            panel.add(new JLabel(concerns));
    
                            // Add a button for diagnosing the appointment
                            JButton diagnoseButton = new JButton("Diagnose");
                            panel.add(diagnoseButton);
    
                            // Add ActionListener for the Diagnose button
                            diagnoseButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    // Display a dialog to input the diagnosis
                                    String diagnosisText = JOptionPane.showInputDialog(patientsFrame, "Enter Diagnosis:");
    
                                    // Save the diagnosis to the database
                                    saveDiagnosis(doctorId, appointmentId, diagnosisText);
                                }
                            });
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        patientsFrame.add(new JScrollPane(panel));
        patientsFrame.setVisible(true);
    }
    
    private void saveDiagnosis(int doctorId, int appointmentId, String diagnosisText) {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection != null) {
                // Execute the query to insert the diagnosis information
                String query = "INSERT INTO Diagnosis (doctor_id, appointment_id, diagnosis_text) VALUES (?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, doctorId);
                    preparedStatement.setInt(2, appointmentId);
                    preparedStatement.setString(3, diagnosisText);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    


    private void roomsAvailabilityFunction() {
        // Create a frame for displaying room availability
        JFrame roomsFrame = new JFrame("Rooms Availability");
        roomsFrame.setSize(600, 400);
        roomsFrame.setLocationRelativeTo(null);
    
        // Create a panel for displaying room availability information
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
    
        JLabel roomNoLabel = new JLabel("Room Number");
        JLabel roomTypeLabel = new JLabel("Room Type");
        JLabel availabilityLabel = new JLabel("Availability");
    
        panel.add(roomNoLabel);
        panel.add(roomTypeLabel);
        panel.add(availabilityLabel);
    
        try (Connection connection = DBConnection.getConnection()) {
            if (connection != null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Room");
                     ResultSet resultSet = preparedStatement.executeQuery()) {
    
                    while (resultSet.next()) {
                        // Extract data from the result set
                        int roomNo = resultSet.getInt("roomNo");
                        String roomType = resultSet.getString("roomType");
                        boolean availability = resultSet.getBoolean("availability");
    
                        // Display room availability details in the panel
                        panel.add(new JLabel(Integer.toString(roomNo)));
                        panel.add(new JLabel(roomType));
                        panel.add(new JLabel(availability ? "Available" : "Occupied"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        // Add the panel to the frame
        roomsFrame.add(new JScrollPane(panel));
        roomsFrame.setVisible(true);
    }
    
    

    private void scheduleAppointmentFunctionForDoctor() {
        JFrame appointmentFrame = new JFrame("Schedule Appointment");
        appointmentFrame.setSize(400, 200);
        appointmentFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel roomLabel = new JLabel("Select Room:");
        JComboBox<String> roomComboBox = new JComboBox<>(getAvailableRooms());

        JLabel dateLabel = new JLabel("Date: (dd/mm/yy)");
        JTextField dateField = new JTextField();

        JLabel startTimeLabel = new JLabel("Start time: (hr:min)");
        JTextField startTimeField = new JTextField();

        JLabel endTimeLabel = new JLabel("End time: (hr:min)");
        JTextField endTimeField = new JTextField();

        JLabel patientLabel = new JLabel("Patient:");
        JTextField patientField = new JTextField();

        JLabel symptomsLabel = new JLabel("Symptoms:");
        JTextField symptomsField = new JTextField();

        JButton submitButton = new JButton("Submit");

        panel.add(roomLabel);
        panel.add(roomComboBox);
        panel.add(dateLabel);
        panel.add(dateField);
        panel.add(startTimeLabel);
        panel.add(startTimeField);
        panel.add(endTimeLabel);
        panel.add(endTimeField);
        panel.add(patientLabel);
        panel.add(patientField);
        panel.add(symptomsLabel);
        panel.add(symptomsField);
        panel.add(new JLabel());
        panel.add(submitButton);

        appointmentFrame.add(panel);
        appointmentFrame.setVisible(true);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve user inputs
                String selectedRoom = (String) roomComboBox.getSelectedItem();
                String date = dateField.getText();
                String startTime = startTimeField.getText();
                String endTime = endTimeField.getText();
                String patient = patientField.getText();
                String symptoms = symptomsField.getText();

                assignRoomToAppointment(date, startTime, endTime, patient, symptoms, selectedRoom);

                JOptionPane.showMessageDialog(appointmentFrame, "Appointment scheduled:\nDate/Time: " + date + " " + startTime + " -> " + endTime + "\nPatient: " + patient + "\nSymptoms: " + symptoms, "Success", JOptionPane.INFORMATION_MESSAGE);
                appointmentFrame.dispose(); // Close the appointment frame
    }
});
    }
    private String[] getAvailableRooms() {
        List<String> availableRooms = new ArrayList<>();
    
        try (Connection connection = DBConnection.getConnection()) {
            if (connection != null) {
                // Execute the query to select available rooms
                String query = "SELECT * FROM Room WHERE availability = true";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                     ResultSet resultSet = preparedStatement.executeQuery()) {
    
                    while (resultSet.next()) {
                        // Extract data from the result set
                        String roomType = resultSet.getString("roomType");
                        availableRooms.add(roomType);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        // Convert the list to an array
        return availableRooms.toArray(new String[0]);
    }
    
    private int getRoomNoByName(String roomName) {
        int roomNo = 0;
    
        try (Connection connection = DBConnection.getConnection()) {
            if (connection != null) {
                // Execute the query to get the room number based on the room name
                String query = "SELECT roomNo FROM Room WHERE roomType = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, roomName);
    
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            roomNo = resultSet.getInt("roomNo");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    
        return roomNo;
    }

    private String getRoomForAppointment(String appointmentDate) {
        String assignedRoom = "";
    
        try (Connection connection = DBConnection.getConnection()) {
            if (connection != null) {
                // Execute the query to get the assigned room based on the appointment date
                String query = "SELECT R.roomType FROM Room R " +
                               "JOIN Appointment A ON R.roomNo = A.roomNo " +
                               "WHERE A.start_time <= ? AND A.end_time >= ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, appointmentDate);
                    preparedStatement.setString(2, appointmentDate);
    
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            assignedRoom = resultSet.getString("roomType");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    
        return assignedRoom;
    }
    

    private void assignRoomToAppointment(String date, String startTime, String endTime, String patient, String symptoms, String room) {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection != null) {
                // Retrieve room number based on the selected room
                int roomNo = getRoomNoByName(room);
    
                // Execute the query to insert appointment details with assigned room
                String query = "INSERT INTO Appointment (start_time, end_time, fee, roomNo) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, date + " " + startTime);
                    preparedStatement.setString(2, date + " " + endTime);
                    preparedStatement.setDouble(3, 50.00); // Replace with the actual fee
                    preparedStatement.setInt(4, roomNo);
    
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private void viewAppointmentsFunctionForDoctor() {
        // Create a frame for viewing appointments
        JFrame appointmentsFrame = new JFrame("View Appointments");
        appointmentsFrame.setSize(600, 400);
        appointmentsFrame.setLocationRelativeTo(null);

        // Create a panel for displaying appointment information
        JPanel panel = new JPanel(new GridLayout(appointmentData.size() + 1, 7, 10, 10));

        // Add headers for appointment details
        panel.add(new JLabel("Room"));
        panel.add(new JLabel("Date"));
        panel.add(new JLabel("Start Time"));
        panel.add(new JLabel("End Time"));
        panel.add(new JLabel("Patient"));
        panel.add(new JLabel("Symptoms"));
        panel.add(new JLabel("Status"));
        panel.add(new JLabel("Actions"));

        // Iterate over appointment data and display information
        for (Map.Entry<String, String> entry : appointmentData.entrySet()) {
            String appointmentInfo = entry.getValue();
            String[] appointmentDetails = appointmentInfo.split(";");

            for (String detail : appointmentDetails) {
                panel.add(new JLabel(detail));
            }

            // Add buttons for actions (e.g., "See Diagnosis" and "Cancel/Delete")
            JButton seeDiagnosisButton = new JButton("See Diagnosis");
            JButton cancelAppointmentButton = new JButton("Cancel/Delete");

            panel.add(new JLabel(getRoomForAppointment(appointmentDetails[0]))); // Assuming appointmentDetails[0] is the appointment date
            panel.add(seeDiagnosisButton);
            panel.add(cancelAppointmentButton);

            // Add ActionListener for See Diagnosis button (you need to implement this)
            seeDiagnosisButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implement logic for "See Diagnosis" button
                    JOptionPane.showMessageDialog(appointmentsFrame, "See Diagnosis functionality to be implemented.");
                }
            });

            // Add ActionListener for Cancel/Delete button (you need to implement this)
            cancelAppointmentButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implement logic for "Cancel/Delete" button
                    String selectedDate = appointmentDetails[0];
                    appointmentData.remove(selectedDate);
                    viewAppointmentsFunctionForDoctor(); // Refresh the view after cancellation
                }
            });
        }
        

        // Add the panel to the frame
        appointmentsFrame.add(new JScrollPane(panel));
        appointmentsFrame.setVisible(true);
    }
// -------------------------------NURSE INTERFACE ----------------------------
    private void nurseInterface(String username) {
        JFrame nurseFrame = new JFrame("Nurse Interface");
        nurseFrame.setSize(400, 300);
        nurseFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton viewPatientsButton = new JButton("View Patients");
        JButton viewRoomAvailabilityButton = new JButton("View available rooms");
        JButton viewAppointmentsButton = new JButton("View Appointments");
        JButton signOutButton = new JButton("Sign Out");
    
        panel.add(viewPatientsButton);
        panel.add(viewRoomAvailabilityButton);
        panel.add(viewAppointmentsButton);
        panel.add(signOutButton);
    
        nurseFrame.add(panel);
        nurseFrame.setVisible(true);
    
        viewPatientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewPatientsFunctionForNurse();
            }
        });


        viewRoomAvailabilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //viewRoomAvailabilityFunction();
            }
        });
    
        viewAppointmentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add logic for "View Appointments" button
                viewAppointmentsFunctionForNurse();
            }
        });
    
        
        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(nurseFrame, "Signed Out");
                nurseFrame.dispose();  // Close the nurse frame
            }
        });
    }
    
    private void viewPatientsFunctionForNurse() {
    
    try {
        // Establish a database connection (replace with your connection details)
        Connection connection = DBConnection.getConnection();

        // Create a statement
        Statement statement = connection.createStatement();

        // Execute a query to retrieve patient information
        ResultSet resultSet = statement.executeQuery("SELECT * FROM patients");

        // Create a frame for viewing patients
        JFrame viewPatientsFrame = new JFrame("View Patients");
        viewPatientsFrame.setSize(800, 400);
        viewPatientsFrame.setLocationRelativeTo(null);

        // Create a panel for displaying patient information
        JPanel panel = new JPanel(new GridLayout(1, 6, 10, 10));

        // Add headers for patient details
        panel.add(new JLabel("Patient ID"));
        panel.add(new JLabel("First Name"));
        panel.add(new JLabel("Last Name"));
        panel.add(new JLabel("Gender"));
        panel.add(new JLabel("Age"));
        panel.add(new JLabel("Actions"));

        // Iterate over the result set and display patient information
        while (resultSet.next()) {
            // Retrieve patient details from the result set
            int patientId = resultSet.getInt("patient_id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String gender = resultSet.getString("gender");
            int age = resultSet.getInt("age");

            // Display patient details
            panel.add(new JLabel(String.valueOf(patientId)));
            panel.add(new JLabel(firstName));
            panel.add(new JLabel(lastName));
            panel.add(new JLabel(gender));
            panel.add(new JLabel(String.valueOf(age)));

            // Add a button for actions (e.g., "View Medical History" or "Edit")
            JButton viewMedicalHistoryButton = new JButton("View Medical History");

            viewMedicalHistoryButton.setActionCommand(String.valueOf(patientId));

            panel.add(viewMedicalHistoryButton);

            viewMedicalHistoryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Retrieve the patient ID from the action command
                    int patientId = Integer.parseInt(((JButton) e.getSource()).getActionCommand());

                    // Call the method to view the medical history for the selected patient
                    viewMedicalHistory(patientId);
                }
            });
        }

        // Add the panel to the frame
        viewPatientsFrame.add(new JScrollPane(panel));
        viewPatientsFrame.setVisible(true);

        // Close the database resources
        resultSet.close();
        statement.close();
        connection.close();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error connecting to the database", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void viewMedicalHistory(int patientId) {
    try {
        // Establish a database connection (replace with your connection details)
        Connection connection = DBConnection.getConnection();

        // Create a statement
        Statement statement = connection.createStatement();

        // Execute a query to retrieve the medical history for the specified patient
        String query = "SELECT * FROM MedicalHistory WHERE patient_id = " + patientId;
        ResultSet rs = statement.executeQuery(query);

        // Create a frame for displaying the medical history
        JFrame historyFrame = new JFrame("Medical History - Patient ID: " + patientId);
        historyFrame.setSize(600, 300);
        historyFrame.setLocationRelativeTo(null);

        // Create a panel for displaying the medical history
        JPanel historyPanel = new JPanel(new GridLayout(1, 4, 10, 10));

        // Add headers for medical history details
        historyPanel.add(new JLabel("Visit Date"));
        historyPanel.add(new JLabel("Conditions"));
        historyPanel.add(new JLabel("Surgeries"));
        historyPanel.add(new JLabel("Medication"));

        // Iterate over the result set and display medical history details
        while (rs.next()) {
            // Retrieve medical history details from the result set
            Date visitDate = rs.getDate("date");
            String conditions = rs.getString("conditions");
            String surgeries = rs.getString("surgeries");
            String medication = rs.getString("medication");

            // Display medical history details
            historyPanel.add(new JLabel(visitDate.toString()));
            historyPanel.add(new JLabel(conditions));
            historyPanel.add(new JLabel(surgeries));
            historyPanel.add(new JLabel(medication));
        }

        // Add the panel to the frame
        historyFrame.add(new JScrollPane(historyPanel));
        historyFrame.setVisible(true);

        // Close the database resources
        rs.close();
        statement.close();
        connection.close();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error retrieving medical history", "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    
private void viewAppointmentsFunctionForNurse() {
    try {
        // Establish a database connection (replace with your connection details)
        Connection connection = DBConnection.getConnection();

        // Create a statement
        Statement statement = connection.createStatement();

        // Execute a query to retrieve appointment information
        ResultSet resultSet = statement.executeQuery("SELECT * FROM appointments");

        // Create a frame for viewing appointments
        JFrame viewAppointmentsFrame = new JFrame("View Appointments");
        viewAppointmentsFrame.setSize(800, 400);
        viewAppointmentsFrame.setLocationRelativeTo(null);

        // Create a panel for displaying appointment information
        JPanel panel = new JPanel(new GridLayout(1, 7, 10, 10));

        // Add headers for appointment details
        panel.add(new JLabel("Appointment ID"));
        panel.add(new JLabel("Patient ID"));
        panel.add(new JLabel("Date"));
        panel.add(new JLabel("Time"));
        panel.add(new JLabel("Concerns"));
        panel.add(new JLabel("Symptoms"));
        panel.add(new JLabel("Status"));

        // Iterate over the result set and display appointment information
        while (resultSet.next()) {
            // Retrieve appointment details from the result set
            int appointmentId = resultSet.getInt("appointment_id");
            int patientId = resultSet.getInt("patient_id");
            String date = resultSet.getString("date");
            String time = resultSet.getString("time");
            String concerns = resultSet.getString("concerns");
            String symptoms = resultSet.getString("symptoms");
            String status = resultSet.getString("status");

            // Display appointment details
            panel.add(new JLabel(String.valueOf(appointmentId)));
            panel.add(new JLabel(String.valueOf(patientId)));
            panel.add(new JLabel(date));
            panel.add(new JLabel(time));
            panel.add(new JLabel(concerns));
            panel.add(new JLabel(symptoms));
            panel.add(new JLabel(status));
        }

        // Add the panel to the frame
        viewAppointmentsFrame.add(new JScrollPane(panel));
        viewAppointmentsFrame.setVisible(true);

        // Close the database resources
        resultSet.close();
        statement.close();
        connection.close();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error connecting to the database", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new View().setVisible(true);
            }
        });
    }
}