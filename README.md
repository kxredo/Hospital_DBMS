# ER Diagram:

<img width="888" alt="Screenshot 2024-08-12 at 4 56 08 PM" src="https://github.com/user-attachments/assets/ce2f43c1-61ac-4423-83dd-566c8f456d5f">

For the Doctor

navailability -> the doctors can specify the times where they are not available 
get_available_appointments(doctor id) -> every doctor sees their own appointment 


Patient should view schedule






extra stuff 
//     private void login() {
//         String username = usernameField.getText();
//         char[] passwordChars = passwordField.getPassword();
//         String password = new String(passwordChars);
    
//         // Check if the user is a patient
//         if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
//             selectedRole = "Patient";
//             userInterface("Patient", username);
//         } else {
//             // If not a patient, try to verify login for doctors/nurses
//             if (verifyLogin(username, password)) {
//                 // Retrieve the user role
//                 String userRole = getUserRole(username);
//                 System.out.println("User role: " + userRole);
    
//                 if (userRole != null) {
//                     selectedRole = userRole;
//                     userInterface(userRole, username);
//                 } else {
//                     JOptionPane.showMessageDialog(this, "User role not found", "Error", JOptionPane.ERROR_MESSAGE);
//                 }
//             } else {
//                 JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
//             }
//         }
    
//         // Clear fields after login attempt
//         usernameField.setText("");
//         passwordField.setText("");
    
// }





    // private boolean verifyLogin(String username, String password) {
    //     try {
    //         // Check if the user exists in the userDatabase map
    //         if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
    //             return true;
    //         }
    
    //         // Establish database connection
    //         Connection connection = DBConnection.getConnection();
    
    //         if (connection != null) {
    //             // Execute a query to check if the username and password match
    //             String query = "SELECT * FROM User WHERE username = ? AND password = ?";
    
    //             try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    //                 preparedStatement.setString(1, username);
    //                 preparedStatement.setString(2, password);
    
    //                 try (ResultSet resultSet = preparedStatement.executeQuery()) {
    //                     // If a row is returned, the login is successful
    //                     return resultSet.next();
    //                 }
    //             }
    //         }
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    
    //     return false;
    // }
    


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
