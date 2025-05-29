package pti.datenbank.autowerk.models;

import java.time.LocalDateTime;
import java.util.List;

public class Appointment {
    private int appointmentId;
    private Customer customer;
    private Mechanic mechanic;
    private Vehicle vehicle;
    private LocalDateTime scheduledAt;
    private String status;

    // n:m with ServiceType
    private List<AppointmentService> services;
    // n:m with Part
    private List<AppointmentPart> parts;

    public Appointment() {}

    public Appointment(int appointmentId, Customer customer, Mechanic mechanic, Vehicle vehicle, LocalDateTime scheduledAt, String status, List<AppointmentService> services, List<AppointmentPart> parts) {
        this.appointmentId = appointmentId;
        this.customer = customer;
        this.mechanic = mechanic;
        this.vehicle = vehicle;
        this.scheduledAt = scheduledAt;
        this.status = status;
        this.services = services;
        this.parts = parts;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AppointmentService> getServices() {
        return services;
    }

    public void setServices(List<AppointmentService> services) {
        this.services = services;
    }

    public List<AppointmentPart> getParts() {
        return parts;
    }

    public void setParts(List<AppointmentPart> parts) {
        this.parts = parts;
    }
}