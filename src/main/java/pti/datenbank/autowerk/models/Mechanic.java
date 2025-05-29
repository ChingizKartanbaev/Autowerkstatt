package pti.datenbank.autowerk.models;

import java.util.List;

public class Mechanic {
    private int mechanicId;
    private User user;
    private String fullName;
    private String speciality;

    // 1:N with Appointment
    private List<Appointment> appointments;

    public Mechanic() {}

    public Mechanic(int mechanicId, User user, String fullName, String speciality, List<Appointment> appointments) {
        this.mechanicId = mechanicId;
        this.user = user;
        this.fullName = fullName;
        this.speciality = speciality;
        this.appointments = appointments;
    }

    public int getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(int mechanicId) {
        this.mechanicId = mechanicId;
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

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }
}