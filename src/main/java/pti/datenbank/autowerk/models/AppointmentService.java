package pti.datenbank.autowerk.models;

public class AppointmentService {
    private Appointment appointment;
    private ServiceType serviceType;
    private int quantity;

    public AppointmentService() {}

    public AppointmentService(Appointment appointment, ServiceType serviceType, int quantity) {
        this.appointment = appointment;
        this.serviceType = serviceType;
        this.quantity = quantity;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}