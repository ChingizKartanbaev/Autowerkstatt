package pti.datenbank.autowerk.models;

public class AppointmentService {
    private Appointment appointment;
    private ServiceType serviceType;

    public AppointmentService() {}

    public AppointmentService(Appointment appointment, ServiceType serviceType) {
        this.appointment = appointment;
        this.serviceType = serviceType;
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

}