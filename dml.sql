INSERT INTO User (username, password, role) VALUES ('admin', 'admin_password', 'admin');

-- Insert Doctor
INSERT INTO User (username, password, role) VALUES ('doctor1', 'qwerty', 'Doctor');
INSERT INTO Employee (first_name, last_name, address, phone, email, salary) VALUES ('Mehmet', 'Öztürk', '456 Çınar Sokak', '9876543210', 'mehmet.ozturk@example.com', 120000.00);

-- Insert Nurse
INSERT INTO User (username, password, role) VALUES ('nurse1', 'qwerty', 'Nurse');
INSERT INTO Nurse (nurse_id, first_name, last_name, address, phone, email, salary) VALUES ('nurse1', 'Hemşire', 'Yılmaz', '789 Çam Caddesi', '5559876543', 'hemsire.yilmaz');




DELIMITER //

CREATE PROCEDURE add_employee(
    IN p_first_name VARCHAR(255),
    IN p_last_name VARCHAR(255),
    IN p_username VARCHAR(255),
    IN p_password VARCHAR(255),
    IN p_salary DECIMAL(10, 2),
    IN p_phone VARCHAR(20)
)
BEGIN
    -- Check if the user has admin role
    IF EXISTS (SELECT 1 FROM users WHERE username = p_username AND role = 'admin') THEN
        -- User has admin role, proceed to add employee
        INSERT INTO employee (first_name, last_name, username, password, salary, phone)
        VALUES (p_first_name, p_last_name, p_username, p_password, p_salary, p_phone);
        
        SELECT LAST_INSERT_ID() AS employee_id; -- Return the generated employee ID
    ELSE
        -- User doesn't have admin role, deny access
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Permission denied: Only admin users can add employees';
    END IF;
END //

DELIMITER ;



CALL add_employee('admin', 'admin_password', 'admin', 'John', 'Doe', 50000, 123456789);

-- Insert Admin
INSERT INTO Admin (username) VALUES ('yonetici_kullanici');


-- Insert Rooms
INSERT INTO Room (roomNo, roomType, availability) VALUES (101, 'Standart', true);

-- Insert Bills
INSERT INTO Bill (bill_no, fees, status, amount) VALUES (1, 100, 1, 1000);

-- Insert Appointments
INSERT INTO Appointment (start_time, end_time, fee) VALUES ('2023-01-01 10:00:00', '2023-01-01 11:00:00', 50.00);

-- Insert Medical History
INSERT INTO MedicalHistory (id, date, conditions, surgeries, medication)
VALUES (1, '2023-01-01', 'Yüksek Tansiyon', 'Apandisit Ameliyatı', 'Aspirin');

-- Update Patient Information
UPDATE Patient SET phone = '9876543210' WHERE patient_id = 'H001';

-- Update Doctor Salary
UPDATE Doctor SET salary = 130000.00 WHERE emp_id = 1;

-- Update Nurse Email
UPDATE Nurse SET email = 'yeni.eposta@example.com' WHERE emp_id = 1;

-- Update Room Availability
UPDATE Room SET availability = false WHERE roomNo = 101;

-- Update Bill Status
UPDATE Bill SET status = 2 WHERE bill_no = 1;


-- Doctor Lists Room Availability
SELECT * FROM Room;

-- Doctor Assigns Room to Appointment
UPDATE Appointment SET start_time = '2023-01-10 14:00:00', end_time = '2023-01-10 15:00:00', fee = 50.00 WHERE emp_id = 1;

-- Nurse Views Room Availability
SELECT roomNo, roomType FROM Room WHERE availability = true;

-- Nurse Views Upcoming Assigned Rooms
SELECT * FROM Appointment WHERE emp_id = 2 AND start_time > NOW();

-- Patient Searches for Doctors
SELECT * FROM Doctor WHERE FieldOfExpertise = 'Cardiology';

-- Patient Filters Appointments
SELECT * FROM Appointment WHERE patient_id = 'P001' AND start_time > NOW();

-- Patient Books Appointment (Assuming room availability check is done in application logic)
INSERT INTO Appointment (start_time, end_time, fee) VALUES ('2023-01-10 14:00:00', '2023-01-10 15:00:00', 50.00);

-- Patient Cancels Appointment
DELETE FROM Appointment WHERE patient_id = 'P001' AND start_time > NOW() + INTERVAL 24 HOUR;

-- Admin Retrieves Patient Statistics
SELECT COUNT(DISTINCT patient_id) AS NumberOfPatients, Department
FROM Appointment
GROUP BY Department;

-- Admin Retrieves Rooms Booked to Appointment Ratio
SELECT Department, COUNT(DISTINCT roomNo) / COUNT(AppointmentID) * 100 AS Ratio
FROM Appointment
GROUP BY Department;

-- Admin Retrieves Nurses to Rooms Booked Ratio
SELECT Department, COUNT(DISTINCT emp_id) / COUNT(DISTINCT roomNo) AS Ratio
FROM Appointment
GROUP BY Department;

-- Admin Retrieves Most Booked Room and Doctor
SELECT Department, roomNo, emp_id, COUNT(AppointmentID) AS BookingCount
FROM Appointment
GROUP BY Department, roomNo, emp_id
ORDER BY BookingCount DESC
LIMIT 1;



-- Create a view for available appointments
CREATE VIEW appointments_view AS
SELECT appointment_id, start_time, end_time, fee
FROM Appointment
WHERE doctor_id IS NULL;

-- Grant SELECT permission on the appointments_view to doctors
GRANT SELECT ON appointments_view TO doctors;

-- Create a view for nurse room availability
CREATE VIEW NurseRoomAvailability AS
SELECT roomNo, roomType, availability
FROM Room;

-- Grant SELECT permission on the NurseRoomAvailability view to nurses
GRANT SELECT ON NurseRoomAvailability TO nurses;

-- Create a stored procedure to get available appointments for a specific doctor
DELIMITER //
CREATE PROCEDURE get_available_appointments(IN p_doctor_username VARCHAR(50))
BEGIN
    -- Check if the user is a doctor
    IF (SELECT role FROM users WHERE username = p_doctor_username) = 'doctor' THEN
        -- Return available appointments for the specified doctor
        SELECT * FROM appointments_view;
    ELSE
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Permission denied: Only doctors can access this information';
    END IF;
END //
DELIMITER ;


-- Not so sure 
-- Create a stored procedure to assign a room and nurse to an appointment
DELIMITER //
CREATE PROCEDURE assign_room_and_nurse(
    IN p_appointment_id INT,
    IN p_room_id INT,
    IN p_nurse_id INT
)
BEGIN
    -- Check if the appointment exists
    IF NOT EXISTS (SELECT 1 FROM Appointment WHERE appointment_id = p_appointment_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Appointment does not exist';
    ELSE
        -- Assign room and nurse to the appointment
        UPDATE Appointment
        SET room_id = p_room_id, nurse_id = p_nurse_id
        WHERE appointment_id = p_appointment_id;
    END IF;
END //
DELIMITER ;