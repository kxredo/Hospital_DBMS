--New Stuff
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL
);


-------
CREATE TABLE Admin (
    username varchar(50) NOT NULL,
    password varchar(50) NOT NULL
);

CREATE TABLE Patient (
    patient_id char(4) NOT NULL PRIMARY KEY,
    username varchar(50) NOT NULL,
    password varchar(30) NOT NULL,
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    nationality varchar(50) NOT NULL,
    gender VARCHAR(6) NOT NULL,
    address varchar(60) NOT NULL,
    dob varchar(20) NOT NULL,
    phone varchar(50) NOT NULL,
    email varchar(100) NOT NULL
);



CREATE TABLE Doctor (
    emp_id INT AUTO_INCREMENT PRIMARY KEY,
    username varchar(50) NOT NULL,
    password varchar(30) NOT NULL,
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    address varchar(60) NOT NULL,
    phone varchar(50) NOT NULL,
    email varchar(100) NOT NULL,
    salary double (10,2) NOT NULL
);

CREATE TABLE Nurse (
    emp_id INT AUTO_INCREMENT PRIMARY KEY,
    username varchar(50) NOT NULL,
    password varchar(30) NOT NULL,
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    address varchar(60) NOT NULL,
    phone varchar(50) NOT NULL,
    email varchar(100) NOT NULL,
    salary double (10,2) NOT NULL
);

CREATE TABLE File (
        fileNo int NOT NULL
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

CREATE TABLE Bill(
        bill_no int PRIMARY KEY,
    fees int NOT NULL,
    status int NOT NULL,
    amount int NOT NULL
);



CREATE TABLE Appointment (
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    fee double NOT NULL
);

CREATE TABLE Room (
    roomNo int PRIMARY KEY,
    roomType varchar(50) NOT NULL,
    availability boolean NOT NULL
);

CREATE TABLE MedicalHistory (
    id int PRIMARY KEY,
    date DATE NOT NULL,
    conditions VARCHAR(100) NOT NULL,
    surgeries VARCHAR(100) NOT NULL,
    medication VARCHAR(100) NOT NULL
);