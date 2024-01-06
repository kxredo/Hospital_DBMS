package Entities;

public class Employee {

    public final int emp_Id;
    public final int phone;
    public final String email;
    public final String address; 
    public final String username; 
    public final String password;
    public final int salary; 
    public final String first_name;
    public final String last_name;
    public final String role;

    public Employee(int emp_Id, int phone, String email, String address, String username, String password, int salary, String first_name, String last_name, String role) {
        this.emp_Id = emp_Id;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.username = username;
        this.password = password;
        this.salary = salary;
        this.first_name = first_name;
        this.last_name = last_name;
        this.role = role;
    }

    // Getter methods for the properties
    public int getEmp_Id() {
        return emp_Id;
    }

    public int getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getSalary() {
        return salary;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }
    public String getRole() {
        return role;
    }
}
