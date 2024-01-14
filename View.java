import javax.swing.*;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;



public class View extends JFrame {
    private Map<String, String> userDatabase;  // Simulated user database (username, password)
    private Map<String, String> appointmentData;
    private Map<String, String> billData = new HashMap<>();  // New map for storing bill information
    private JTextField usernameField;
    private JPasswordField passwordField;
    private String selectedRole;

    public View() {
        super("Hospital Management System");
        userDatabase = new HashMap<>();
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
            // Establish database connection
            Connection connection = DBConnection.getConnection();
    
            if (connection != null) {
                // Execute a query to retrieve the user role based on the username
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
    
        // Check if the user is a patient
        if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
            selectedRole = "Patient";
            userInterface("Patient");
        } else {
            // If not a patient, try to verify login for doctors/nurses
            if (verifyLogin(username, password)) {
                // Retrieve the user role
                String userRole = getUserRole(username);
                System.out.println("User role: " + userRole);
    
                if (userRole != null) {
                    selectedRole = userRole;
                    userInterface(userRole);
                } else {
                    JOptionPane.showMessageDialog(this, "User role not found", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        // Clear fields after login attempt
        usernameField.setText("");
        passwordField.setText("");
    }
    
    
    private boolean verifyLogin(String username, String password) {
        try {
            // Check if the user exists in the userDatabase map
            if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
                return true;
            }
    
            // Establish database connection
            Connection connection = DBConnection.getConnection();
    
            if (connection != null) {
                // Execute a query to check if the username and password match
                String query = "SELECT * FROM User WHERE username = ? AND password = ?";
    
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
    
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
    
            if (userDatabase.containsKey(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.", "Duplicate Username", JOptionPane.ERROR_MESSAGE);
            }
    
        } while (userDatabase.containsKey(username));
    
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
            userDatabase.put(username, password);
            registrationForm(username);  // Directly show the registration form
        }
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
    
    private void userInterface(String userType) {
        if (selectedRole.equals("Patient")) {
            patientInterface(userType);
        }
        else if (selectedRole.equals("Doctor")) {
            doctorInterface(userType);
        }
        else if(selectedRole.equals("Nurse")) {
            nurseInterface(userType);
        }
        else {
            //JOptionPane.showMessageDialog(this, "Error." ,"Login Unsuccessful.", JOptionPane.ERROR_MESSAGE);
        }
    }

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
    panel.add(new JLabel()); // Empty label as a placeholder
    panel.add(new JLabel()); // Empty label as a placeholder
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
    // Create a frame for displaying search results
    JFrame resultsFrame = new JFrame("Search Results");
    resultsFrame.setSize(400, 200);
    resultsFrame.setLocationRelativeTo(null);

    // Create a panel for displaying search results
    JPanel panel = new JPanel(new GridLayout(doctorList.size() + 1, 3, 10, 10));

    // Add headers for doctor details
    panel.add(new JLabel("Doctor ID"));
    panel.add(new JLabel("Name"));
    panel.add(new JLabel("Specialty"));
    

    // Iterate over the doctor list and display information
    for (String[] doctorDetails : doctorList) {
        for (String detail : doctorDetails) {
            panel.add(new JLabel(detail));
        }
    }

    // Add the panel to the frame
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
        panel.add(new JLabel()); // Empty label as a placeholder
        panel.add(submitButton);

        appointmentFrame.add(panel);
        appointmentFrame.setVisible(true);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve user inputs
                String date = dateField.getText();
                String concerns = concernsField.getText();
                String symptoms = symptomsField.getText();

                String appointmentInfo = date + ";" + "Scheduled" + ";" + timeField.getText() + ";" + concerns + ";" + symptoms;
            appointmentData.put(date, appointmentInfo);

            // Show a success message
            JOptionPane.showMessageDialog(appointmentFrame, "Appointment scheduled:\nDate/Time: " + date + "\nConcerns: " + concerns + "\nSymptoms: " + symptoms, "Success", JOptionPane.INFORMATION_MESSAGE);
            appointmentFrame.dispose(); // Close the appointment frame
        }
    });
}

private void viewAppointmentsFunction() {
    // Create a frame for viewing appointments
    JFrame appointmentsFrame = new JFrame("View Appointments");
    appointmentsFrame.setSize(600, 400);
    appointmentsFrame.setLocationRelativeTo(null);

    // Create a panel for displaying appointment information
    JPanel panel = new JPanel(new GridLayout(appointmentData.size() + 1, 7, 10, 10));

    // Add headers for appointment details
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

    // Add ActionListener for Apply Filter button
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

        // Add buttons for actions (e.g., "See Diagnosis" and "Cancel/Delete")
        JButton seeDiagnosisButton = new JButton("See Diagnosis");
        JButton cancelAppointmentButton = new JButton("Cancel/Delete");

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
                appointmentsFrame.dispose();
                viewAppointmentsFunction(); // Refresh the view after cancellation
            }
        });
    }

    // Add the panel to the frame
    appointmentsFrame.add(new JScrollPane(panel), BorderLayout.CENTER);
    appointmentsFrame.setVisible(true);
}

private void applyFilter(int days, String specialty) {
    // Implement filtering logic based on days and specialty
    // For example, you can filter the displayed appointments accordingly
    // Update the appointmentData map based on the filter criteria
    // Then, call viewAppointmentsFunction() to refresh the view with the filtered appointments
    // You need to implement this part based on your specific requirements
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


    private void doctorInterface(String username) {
        JFrame doctorFrame = new JFrame("Doctor Interface");
        doctorFrame.setSize(400, 300);
        doctorFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

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
        panel.add(new JLabel()); // Empty label as a placeholder
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

                // Add logic to save the appointment details to the database or perform necessary actions
                JOptionPane.showMessageDialog(appointmentFrame, "Appointment scheduled:\nDate/Time: " + date + " " + startTime + " -> " + endTime + "\nPatient: " + patient + "\nSymptoms: " + symptoms, "Success", JOptionPane.INFORMATION_MESSAGE);
                appointmentFrame.dispose(); // Close the appointment frame
            }
        });
    }
    private int getRoomNoByName(String roomName) {
        // Implement this method to retrieve and return the room number based on the room name
        // You can fetch this information from the Room table in the database
        // Example: return 101;
        return 1;
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
    

    private String getRoomForAppointment(String appointmentDate) {
        // Implement this method to retrieve and return the assigned room for a specific appointment date
        // You can fetch this information from the database based on the provided date
        // Example: return "X-Ray room";
        return "Room 101";
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

    private void nurseInterface(String username) {
        JFrame nurseFrame = new JFrame("Nurse Interface");
        nurseFrame.setSize(400, 300);
        nurseFrame.setLocationRelativeTo(null);
    
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
    
        JButton viewPatientsButton = new JButton("View Patients");
        JButton viewAppointmentsButton = new JButton("View Appointments");
        JButton managePatientsButton = new JButton("Manage Patients");
        JButton signOutButton = new JButton("Sign Out");
    
        panel.add(viewPatientsButton);
        panel.add(viewAppointmentsButton);
        panel.add(managePatientsButton);
        panel.add(signOutButton);
    
        nurseFrame.add(panel);
        nurseFrame.setVisible(true);
    
        viewPatientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add logic for "View Patients" button
                viewPatientsFunctionForNurse();
            }
        });
    
        viewAppointmentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add logic for "View Appointments" button
                viewAppointmentsFunctionForNurse();
            }
        });
    
        managePatientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add logic for "Manage Patients" button
                managePatientFunction();
            }
        });
    
        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add logic to handle "Sign Out" button click
                JOptionPane.showMessageDialog(nurseFrame, "Signed Out");
                nurseFrame.dispose();  // Close the nurse frame
            }
        });
    }
    
    // Implement the corresponding functions for View Patients, View Appointments, and Manage Patients
    private void viewPatientsFunctionForNurse() {
    // Fetch and display patient information using JDBC (assuming you have a database connection)

    // For demonstration purposes, let's assume you have a database table named "patients"
    // with columns: patient_id, first_name, last_name, gender, age, etc.

    // Replace the following JDBC code with your actual database interaction logic
    try {
        // Establish a database connection (replace with your connection details)
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "username", "password");

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

            panel.add(viewMedicalHistoryButton);

            // Add ActionListener for the viewMedicalHistoryButton (you need to implement this)
            viewMedicalHistoryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Implement logic for "View Medical History" button
                    JOptionPane.showMessageDialog(viewPatientsFrame, "View Medical History functionality to be implemented.");
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

    
private void viewAppointmentsFunctionForNurse() {
    // Fetch and display appointment information using JDBC (assuming you have a database connection)

    // For demonstration purposes, let's assume you have a database table named "appointments"
    // with columns: appointment_id, patient_id, date, time, concerns, symptoms, status, etc.

    // Replace the following JDBC code with your actual database interaction logic
    try {
        // Establish a database connection (replace with your connection details)
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "username", "password");

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

    
private void managePatientFunction() {
    // For demonstration purposes, let's assume you have a database table named "patients"
    // with columns: patient_id, first_name, last_name, vital_signs, medication, etc.

    // Replace the following JDBC code with your actual database interaction logic
    try {
        // Establish a database connection (replace with your connection details)
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "username", "password");

        // Create a statement
        Statement statement = connection.createStatement();

        // Execute a query to retrieve patient information
        ResultSet resultSet = statement.executeQuery("SELECT * FROM patients");

        // Create a frame for managing patients
        JFrame managePatientsFrame = new JFrame("Manage Patients");
        managePatientsFrame.setSize(800, 400);
        managePatientsFrame.setLocationRelativeTo(null);

        // Create a panel for displaying patient information
        JPanel panel = new JPanel(new GridLayout(1, 5, 10, 10));

        // Add headers for patient details
        panel.add(new JLabel("Patient ID"));
        panel.add(new JLabel("First Name"));
        panel.add(new JLabel("Last Name"));
        panel.add(new JLabel("Vital Signs"));
        panel.add(new JLabel("Medication"));

        // Iterate over the result set and display patient information
        while (resultSet.next()) {
            // Retrieve patient details from the result set
            int patientId = resultSet.getInt("patient_id");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String vitalSigns = resultSet.getString("vital_signs");
            String medication = resultSet.getString("medication");

            // Display patient details
            panel.add(new JLabel(String.valueOf(patientId)));
            panel.add(new JLabel(firstName));
            panel.add(new JLabel(lastName));
            panel.add(new JLabel(vitalSigns));
            panel.add(new JLabel(medication));
        }

        // Add the panel to the frame
        managePatientsFrame.add(new JScrollPane(panel));
        managePatientsFrame.setVisible(true);

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