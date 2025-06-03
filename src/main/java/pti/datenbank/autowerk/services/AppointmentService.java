package pti.datenbank.autowerk.services;

import pti.datenbank.autowerk.dao.AppointmentDao;
import pti.datenbank.autowerk.dao.AppointmentPartDao;
import pti.datenbank.autowerk.dao.DBConnection;
import pti.datenbank.autowerk.dao.Impl.AppointmentDaoImpl;
import pti.datenbank.autowerk.dao.Impl.AppointmentPartDaoImpl;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.Appointment;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AppointmentService extends BaseService {
    private final AppointmentDao appointmentDao = new AppointmentDaoImpl();
    private final AppointmentServiceService apptSvcService;
    private final AppointmentPartDao apptPartDao = new AppointmentPartDaoImpl();

    public AppointmentService(AuthService authService) {
        super(authService);
        this.apptSvcService = new AppointmentServiceService(authService);
    }

    public Appointment findById(int id) throws SQLException {
        checkPermission(Permission.READ);
        return appointmentDao.findById(id);
    }

    public List<Appointment> findAll() throws SQLException {
        checkPermission(Permission.READ);
        return appointmentDao.findAll();
    }

    public List<Appointment> findByCustomerId(int customerId) throws SQLException {
        checkPermission(Permission.READ);
        return appointmentDao.findByCustomerId(customerId);
    }

    public List<Appointment> findByMechanicId(int mechanicId) throws SQLException {
        checkPermission(Permission.READ);
        return appointmentDao.findByMechanicId(mechanicId);
    }

    public void create(Appointment appointment) throws SQLException {
        checkPermission(Permission.CREATE);
        appointmentDao.insert(appointment);
    }

    public void update(Appointment appointment) throws SQLException {
        checkPermission(Permission.UPDATE);
        appointmentDao.update(appointment);
    }


    public void delete(int appointmentId) throws SQLException {
        checkPermission(Permission.DELETE);
        appointmentDao.delete(appointmentId);
    }

    public boolean hasActiveAppointmentsForVehicle(int vehicleId) throws SQLException {
        checkPermission(Permission.READ);
        int cnt = ((AppointmentDaoImpl)appointmentDao).countActiveByVehicle(vehicleId);
        return cnt > 0;
    }

    public void deleteAppointmentWithDependencies(int appointmentId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // начать транзакцию

            try {
                // 1. Удаление зависимостей
                String deleteServices = "DELETE FROM AppointmentServices WHERE AppointmentID = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteServices)) {
                    ps.setInt(1, appointmentId);
                    ps.executeUpdate();
                }

                String deleteParts = "DELETE FROM AppointmentParts WHERE AppointmentID = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteParts)) {
                    ps.setInt(1, appointmentId);
                    ps.executeUpdate();
                }

                // 2. Удаление самой записи
                String deleteAppointment = "DELETE FROM Appointments WHERE AppointmentID = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteAppointment)) {
                    ps.setInt(1, appointmentId);
                    ps.executeUpdate();
                }

                conn.commit(); // подтверждаем все операции

            } catch (SQLException ex) {
                conn.rollback(); // отмена при ошибке
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}