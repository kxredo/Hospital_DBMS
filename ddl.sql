CREATE DATABASE Hospital;

CREATE TABLE User (
    user_id INT PRIMARY KEY UNIQUE AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE Employee (
    employee_id INT UNIQUE PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    address VARCHAR(60) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    salary DOUBLE(10, 2) NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES User(user_id)
);

CREATE TABLE Patient (
    patient_id INT NOT NULL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    nationality VARCHAR(50) NOT NULL,
    gender VARCHAR(6) NOT NULL,
    address VARCHAR(60) NOT NULL,
    dob VARCHAR(20) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES User(user_id)
);

CREATE TABLE Doctor (
    doctor_id INT PRIMARY KEY,
    specialty VARCHAR(50) NOT NULL,
    FOREIGN KEY (doctor_id) REFERENCES Employee(employee_id)
);

-- CREATE TABLE Nurse (
--     nurse_id INT PRIMARY KEY,
--     FOREIGN KEY (nurse_id) REFERENCES Employee(employee_id)
-- );

-- CREATE TABLE Receptionist (
--     recep_id INT PRIMARY KEY,
--     FOREIGN KEY (recep_id) REFERENCES Employee(employee_id)
-- );


CREATE TABLE File (
        fileNo int NOT NULL
);

CREATE TABLE Diagnosis (
    diagnosis_id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id INT,
    appointment_id INT,
    diagnosis_text TEXT,
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (appointment_id) REFERENCES Appointment(appointment_id)
);


CREATE TABLE Bill (
    billNo INT PRIMARY KEY,
    patientId INT,
    status VARCHAR(20),
    fees DECIMAL(10, 2),
    FOREIGN KEY (patientId) REFERENCES Patient(patientId)
);



CREATE TABLE Appointment (
    app_id int PRIMARY KEY,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    fee DECIMAL(5,2) NOT NULL,
    roomNo int,  -- New field to store the assigned room number
    FOREIGN KEY (roomNo) REFERENCES Room(roomNo),
    FOREIGN KEY (emp_id) REFERENCES Doctor(emp_id)
);


CREATE TABLE Room (
    roomNo int PRIMARY KEY,
    roomType varchar(50) NOT NULL,
    availability boolean DEFAULT true
);

CREATE TABLE MedicalHistory (
    id int PRIMARY KEY,
    date DATE NOT NULL,
    conditions VARCHAR(100) NOT NULL,
    surgeries VARCHAR(100) NOT NULL,
    medication VARCHAR(100) NOT NULL
);


CREATE TABLE Schedules (
    doctor_id INT,
    appointment_date INT,  -- Assuming the date and time are stored in a DATETIME column
    room_id INT,
    patient_id INT,
    appointment_id INT PRIMARY KEY,  -- Assuming appointment_id is the primary key for the Appointment table
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id),
    FOREIGN KEY (appointment_id) REFERENCES Appointment(appointment_id),
    FOREIGN KEY (room_id) REFERENCES Room(roomNo),  -- Corrected the reference to roomNo
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
);


CREATE TABLE Pays (
    receipt_id INT AUTO_INCREMENT, 
    bill_no INT, 
    patient_id INT,
    PRIMARY KEY (receipt_id),
    FOREIGN KEY (bill_no) REFERENCES Bill(bill_no),
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id)
);

CREATE TABLE Unavailability (
    unavailability_id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id VARCHAR(50),
    day_of_week INT,
    start_time TIME,
    end_time TIME,
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id)
);


CREATE TABLE SpecialtyPrices (
    specialty VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);


/*CREATE TABLE EmergencyContactPerson (
    EmergencyContactPerson_ID INT PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100)
);
*/

/*
CREATE TABLE HealthInsurance(
        HealthInsurance_ID int PRIMARY KEY,
    company varchar(70),
    address1 varchar(70),
    address2 varchar(70),
    city varchar(70),
    zipcode varchar(70),
    country varchar(70),
    phone varchar(70),
    email varchar(70)
);
*/

-- CREATE TABLE AttendedBy {
--     nurse_id INT,
--     doctor_id INT,
--     patient_id INT,
--     appointment_id INT,

--     FOREIGN KEY (nurse_id) REFERENCES Nurse(emp_id),
--     FOREIGN KEY (doctor_id) REFERENCES Doctor(emp_id),
--     FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
--     FOREIGN KEY (appointment_id) REFERENCES Appointment(appointment_id)
 
-- };

--
-- CREATE TABLE LocatedIn (
--     appointment_id INT,
--     roomNo INT,
--     FOREIGN KEY (appointment_id) REFERENCES Appointment(appointment_id),
--     FOREIGN KEY (roomNo) REFERENCES Room(roomNo)
-- );