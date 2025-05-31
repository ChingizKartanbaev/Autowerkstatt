package pti.datenbank.autowerk.models;

import java.util.List;

public class Customer {
    private int customerId;
    private User user;
    private String fullName;
    private String phone;
    private String address;

    // 1:N with Vehicle
    private List<Vehicle> vehicles;
    // 1:N with Appointment
    private List<Appointment> appointments;

    public Customer() {}

    public Customer(int customerId, User user, String fullName, String phone, String address, List<Vehicle> vehicles, List<Appointment> appointments) {
        this.customerId = customerId;
        this.user = user;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.vehicles = vehicles;
        this.appointments = appointments;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @Override
    public String toString() {
        return fullName;
    }
}