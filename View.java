import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class View extends JFrame {
    private Map<String, String> userDatabase;  // Simulated user database (username, password, role)
    private JTextField usernameField;
    private JPasswordField passwordField;
    JComboBox <String> roleComboBox;

    public View() {
        super("Hospital Management System");
        userDatabase = new HashMap<>();

        // Sample users (You should replace this with a proper user authentication mechanism)
        userDatabase.put("admin", "adminpass,Admin");
        userDatabase.put("doctor", "doctorpass,Doctor");
        userDatabase.put("receptionist", "receptionistpass,Receptionist");

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
        roleComboBox = new JComboBox<>(new String[]{"Doctor", "Nurse", "Patient"});


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

    private void login() {
        String username = usernameField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
    
        // Check if the username exists in the database
        if (userDatabase.containsKey(username)) {
            // Retrieve the stored password and role information
            String storedInfo = userDatabase.get(username);
            String storedPassword = storedInfo.split(",")[0];
            String storedRole = storedInfo.split(",")[1];
    
            // Check if the entered password matches the stored password
            if (password.equals(storedPassword)) {
                showUserInterface(username + "," + storedRole);
                return;  // Exit the method after successful login
            }
        }
    
        // Display an error message for invalid username or password
        JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
    
        // Clear fields after login attempt
        usernameField.setText("");
        passwordField.setText("");
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
            String selectedRole = (String) roleComboBox.getSelectedItem();
            userDatabase.put(username, password + "," + selectedRole);
            showRegistrationForm(username);  // Directly show the registration form
        }
    }

    private void showRegistrationForm(String username) {
        JFrame registrationFrame = new JFrame("User Registration");
        registrationFrame.setSize(400, 400);
        registrationFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));

        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField();

        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField();

        JLabel roleLabel = new JLabel("Role:");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Doctor", "Nurse", "Patient"});

        JLabel genderLabel = new JLabel("Gender:");
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});

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
        panel.add(roleLabel);
        panel.add(roleComboBox);
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
        registrationFrame.setTitle("Registration Form");
        registrationFrame.add(panel);
        registrationFrame.setVisible(true);

        submitButton.addActionListener(new ActionListener() {
            @Override
        public void actionPerformed(ActionEvent e) {
            // Validate mandatory fields before submitting
            String validationMessage = validateRegistrationForm(firstNameField.getText(), lastNameField.getText(),
                    phoneNumberField.getText(), emailField.getText(), addressField.getText());

            if (validationMessage.isEmpty()) {
                // Retrieve selected role from the combo box
                String selectedRole = (String) roleComboBox.getSelectedItem();

                // Save the user details to the database or perform necessary actions
                JOptionPane.showMessageDialog(registrationFrame, "Registration successful\nRole: " + selectedRole, "Success", JOptionPane.INFORMATION_MESSAGE);
                registrationFrame.dispose();  // Close the registration frame
            } else {
                JOptionPane.showMessageDialog(registrationFrame, validationMessage, "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });
}

// Validation method to check if mandatory fields are filled
private String validateRegistrationForm(String firstName, String lastName, String phoneNumber, String email, String address) {
    StringBuilder validationMessage = new StringBuilder();

    if (firstName.isEmpty()) {
        validationMessage.append("First Name is missing.\n");
    }
    if (lastName.isEmpty()) {
        validationMessage.append("Last Name is missing.\n");
    }
    if (phoneNumber.isEmpty()) {
        validationMessage.append("Phone Number is missing.\n");
    }
    if (!isNumeric(phoneNumber)) {
        validationMessage.append("Phone Number must contain only numeric characters.\n");
    }
    if(phoneNumber.length() < 7 || phoneNumber.length() > 15) {
        validationMessage.append("Phone number must be between 7 and 15.");
    }

    if (email.isEmpty()) {
        validationMessage.append("Email is missing.\n");
    }
    if (!isValidEmail(email)) {
        validationMessage.append("Invalid Email address.\n");
    }
    
    if (address.isEmpty()) {
        validationMessage.append("Address is missing.\n");
    }

    return validationMessage.toString();
}
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    // Check if a string contains only numeric characters
    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    private void showUserInterface(String userInfo) {
        String[] userInfoArray = userInfo.split(",");
        String username = userInfoArray[0];
        String role = userInfoArray[1];
    
        JFrame userInterfaceFrame = new JFrame("User Interface");
        userInterfaceFrame.setSize(400, 300);
        userInterfaceFrame.setLocationRelativeTo(null);
    
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
    
        
    
        
    
        userInterfaceFrame.add(panel);
        userInterfaceFrame.setVisible(true);
    
        // Button actions based on the user's role
        switch (role) {
            case "Doctor":
                // Implement doctor-specific actions
                JButton viewAppointmentsDoctorButton = new JButton("View Appointments");
                JButton scheduleAppointmentDoctorButton = new JButton("Schedule Appointment");
                JButton viewPatientsButton = new JButton("View Patients");
                JButton signOutButton = new JButton("Sign Out");
                panel.add(viewAppointmentsDoctorButton);
                panel.add(scheduleAppointmentDoctorButton);
                panel.add(viewPatientsButton);
                panel.add(signOutButton);
                signOutButton.addActionListener(e -> userInterfaceFrame.dispose());  // Close the user interface frame

                break;
            case "Nurse":
                // Implement nurse-specific actions
                
            case "Patient":
                // Implement patient-specific actions
                JButton viewAppointmentsPatientButton = new JButton("View Appointments");
                JButton scheduleAppointmentPatientButton = new JButton("Schedule Appointment");
                JButton Bills = new JButton("Bills");
                signOutButton = new JButton("Sign Out");
                panel.add(viewAppointmentsPatientButton);
                panel.add(scheduleAppointmentPatientButton);
                panel.add(Bills);
                panel.add(signOutButton);
                signOutButton.addActionListener(e -> userInterfaceFrame.dispose());  // Close the user interface frame

                break;
                
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
