package pti.datenbank.autowerk.services.facade;

import pti.datenbank.autowerk.dao.DBConnection;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.Appointment;
import pti.datenbank.autowerk.models.AppointmentService;
import pti.datenbank.autowerk.models.ServiceType;
import pti.datenbank.autowerk.services.AppointmentServiceService;
import pti.datenbank.autowerk.services.AuthService;
import pti.datenbank.autowerk.services.BaseService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AppointmentFacade extends BaseService {

    private final pti.datenbank.autowerk.services.AppointmentService appointmentService;
    private final AppointmentServiceService appointmentServiceService;

    public AppointmentFacade(AuthService authService) {
        super(authService);
        this.appointmentService         = new pti.datenbank.autowerk.services.AppointmentService(authService);
        this.appointmentServiceService  = new AppointmentServiceService(authService);
    }

    public List<Appointment> findByCustomerId(int customerId) throws SQLException {
        checkPermission(Permission.READ);
        List<Appointment> list = appointmentService.findByCustomerId(customerId);
        for (Appointment ap : list) {
            int appId = ap.getAppointmentId();
            List<AppointmentService> services = appointmentServiceService.findByAppointmentId(appId);
            ap.setServices(services);
        }
        return list;
    }

    public Appointment findByIdWithServices(int appointmentId) throws SQLException {
        checkPermission(Permission.READ);
        Appointment ap = appointmentService.findById(appointmentId);
        if (ap == null) {
            return null;
        }
        List<AppointmentService> services = appointmentServiceService.findByAppointmentId(appointmentId);
        ap.setServices(services);
        return ap;
    }

    public List<Appointment> findByMechanicId(int mechanicId) throws SQLException {
        checkPermission(Permission.READ);
        List<Appointment> list = appointmentService.findByMechanicId(mechanicId);
        for (Appointment ap : list) {
            int appId = ap.getAppointmentId();
            List<AppointmentService> services = appointmentServiceService.findByAppointmentId(appId);
            ap.setServices(services);
        }
        return list;
    }

    public void createAppointmentWithServices(Appointment appointment, List<ServiceType> services) throws SQLException {
        checkPermission(Permission.CREATE);
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                appointmentService.create(appointment);

                for (ServiceType srv : services) {
                    appointmentServiceService.create(new AppointmentService(appointment, srv));
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void updateAppointmentWithServices(Appointment appointment,
                                              List<AppointmentService> services) throws SQLException {
        checkPermission(Permission.UPDATE);
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                appointmentService.update(appointment);

                int appId = appointment.getAppointmentId();
                appointmentServiceService.deleteByAppointmentId(appId);

                for (AppointmentService srv : services) {
                    srv.setAppointment(appointment);
                    appointmentServiceService.create(srv);
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void cancelAppointment(int appointmentId) throws SQLException {
        checkPermission(Permission.UPDATE);
        Appointment ap = appointmentService.findById(appointmentId);
        if (ap != null) {
            ap.setStatus("CANCELLED");
            appointmentService.update(ap);
        } else {
            throw new SQLException("Запись с ID=" + appointmentId + " не найдена");
        }
    }

    public void deleteAppointment(int appointmentId) throws SQLException {
        checkPermission(Permission.DELETE);
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                appointmentServiceService.deleteByAppointmentId(appointmentId);
                appointmentService.delete(appointmentId);
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
