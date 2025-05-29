package pti.datenbank.autowerk.models;

public class AppointmentPart {
    private Appointment appointment;
    private Part part;
    private int quantity;

    public AppointmentPart() {}

    public AppointmentPart(Appointment appointment, Part part, int quantity) {
        this.appointment = appointment;
        this.part = part;
        this.quantity = quantity;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}