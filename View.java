import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

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

    private void login() {
        String username = usernameField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
            showUserInterface(username);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
        }

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
            userDatabase.put(username, password);
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
    
        registrationFrame.add(panel);
        registrationFrame.setVisible(true);
    
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve selected role from the combo box
                selectedRole = (String) roleComboBox.getSelectedItem();
    
                // Save the user details to the database or perform necessary actions
                JOptionPane.showMessageDialog(registrationFrame, "Registration successful\nRole: " + selectedRole, "Success", JOptionPane.INFORMATION_MESSAGE);
                registrationFrame.dispose();  // Close the registration frame
            }
        });
    }

    private void patientInterface(String username) {
        JFrame patientFrame = new JFrame("Patient Interface");
        patientFrame.setSize(400, 300);
        patientFrame.setLocationRelativeTo(null);
    
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
    
        JButton viewAppointmentsButton = new JButton("View Appointments");
        JButton scheduleAppointmentButton = new JButton("Schedule Appointment");
        JButton viewBillsButton = new JButton("View Bills");
        JButton signOutButton = new JButton("Sign Out");
    
        panel.add(viewAppointmentsButton);
        panel.add(scheduleAppointmentButton);
        panel.add(viewBillsButton);
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
    
        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add logic to handle "Sign Out" button click
                JOptionPane.showMessageDialog(patientFrame, "Signed Out");
                patientFrame.dispose();  // Close the patient frame
            }
        });
    }
    
    private void scheduleAppointmentFunction() {
        JFrame appointmentFrame = new JFrame("Schedule Appointment");
        appointmentFrame.setSize(400, 200);
        appointmentFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

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
                    viewAppointmentsFunction(); // Refresh the view after cancellation
                }
            });
        }

        // Add the panel to the frame
        appointmentsFrame.add(new JScrollPane(panel));
        appointmentsFrame.setVisible(true);
    }

    private void viewBillsFunction() {
        // Create a frame for viewing bills
        JFrame billsFrame = new JFrame("View Bills");
        billsFrame.setSize(600, 400);
        billsFrame.setLocationRelativeTo(null);

        // Create a panel for displaying bill information
        JPanel panel = new JPanel(new GridLayout(billData.size() + 1, 3, 10, 10));

        // Add headers for bill details
        panel.add(new JLabel("Date"));
        panel.add(new JLabel("Description"));
        panel.add(new JLabel("Amount"));

        // Iterate over bill data and display information
        for (Map.Entry<String, String> entry : billData.entrySet()) {
            String billInfo = entry.getValue();
            String[] billDetails = billInfo.split(";");

            for (String detail : billDetails) {
                panel.add(new JLabel(detail));
            }
        }

        // Add the panel to the frame
        billsFrame.add(new JScrollPane(panel));
        billsFrame.setVisible(true);
    }

    private void doctorInterface(String username) {
        JFrame doctorFrame = new JFrame("Doctor Interface");
        doctorFrame.setSize(400, 300);
        doctorFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton viewPatientsButton = new JButton("View Patients");
        JButton viewAppointmentsButton = new JButton("View Appointments");
        JButton scheduleAppointmentButton = new JButton("Schedule Appointment");
        JButton signOutButton = new JButton("Sign Out");

        panel.add(viewPatientsButton);
        panel.add(viewAppointmentsButton);
        panel.add(scheduleAppointmentButton);
        panel.add(signOutButton);

        doctorFrame.add(panel);
        doctorFrame.setVisible(true);

        viewPatientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewPatientsFunction();
            }
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
    
    private void viewPatientsFunction() {
        // Create a frame for viewing patients
        JFrame patientsFrame = new JFrame("View Patients");
        patientsFrame.setSize(600, 400);
        patientsFrame.setLocationRelativeTo(null);
    
        // Assuming you have a map to store patient data similar to appointmentData and billData
        Map<String, String> patientData = new HashMap<>();
        // Populate patient data (replace this with your actual patient data)
        patientData.put("Patient1", "John Doe;25;Male;123 Main St;555-1234");
        patientData.put("Patient2", "Jane Doe;30;Female;456 Oak St;555-5678");
    
        // Create a panel for displaying patient information
        JPanel panel = new JPanel(new GridLayout(patientData.size() + 1, 5, 10, 10));
    
        // Add headers for patient details
        panel.add(new JLabel("Name"));
        panel.add(new JLabel("Age"));
        panel.add(new JLabel("Gender"));
        panel.add(new JLabel("Address"));
        panel.add(new JLabel("Phone Number"));
    
        // Iterate over patient data and display information
        for (Map.Entry<String, String> entry : patientData.entrySet()) {
            String patientInfo = entry.getValue();
            String[] patientDetails = patientInfo.split(";");
    
            for (String detail : patientDetails) {
                panel.add(new JLabel(detail));
            }
        }
    
        // Add the panel to the frame
        patientsFrame.add(new JScrollPane(panel));
        patientsFrame.setVisible(true);
    }

    private void scheduleAppointmentFunctionForDoctor() {
        JFrame appointmentFrame = new JFrame("Schedule Appointment");
        appointmentFrame.setSize(400, 200);
        appointmentFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel dateLabel = new JLabel("Date: (dd/mm/yy)");
        JTextField dateField = new JTextField();

        JLabel timeLabel = new JLabel("Time: (hr:min)");
        JTextField timeField = new JTextField();

        JLabel patientLabel = new JLabel("Patient:");
        JTextField patientField = new JTextField();

        JLabel symptomsLabel = new JLabel("Symptoms:");
        JTextField symptomsField = new JTextField();

        JButton submitButton = new JButton("Submit");

        panel.add(dateLabel);
        panel.add(dateField);
        panel.add(timeLabel);
        panel.add(timeField);
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
                String date = dateField.getText();
                String time = timeField.getText();
                String patient = patientField.getText();
                String symptoms = symptomsField.getText();

                // Add logic to save the appointment details to the database or perform necessary actions
                JOptionPane.showMessageDialog(appointmentFrame, "Appointment scheduled:\nDate/Time: " + date + " " + time + "\nPatient: " + patient + "\nSymptoms: " + symptoms, "Success", JOptionPane.INFORMATION_MESSAGE);
                appointmentFrame.dispose(); // Close the appointment frame
            }
        });
    }

    private void viewAppointmentsFunctionForDoctor() {
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
    

    

    private void showUserInterface(String userType) {
        if (selectedRole.equals("Patient")) {
            patientInterface(userType);
        }
        if (selectedRole.equals("Doctor")) {
            doctorInterface(userType);
        }
        if(selectedRole.equals("Nurse")) {
            nurseInterface(userType);
        }
        else {
            // Implement interfaces for other user types if needed
            JOptionPane.showMessageDialog(this, "Welcome, " + userType + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
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