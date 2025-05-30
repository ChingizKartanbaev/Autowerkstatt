package pti.datenbank.autowerk.services;

import pti.datenbank.autowerk.dao.AppointmentDao;
import pti.datenbank.autowerk.dao.Impl.AppointmentDaoImpl;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.Appointment;

import java.sql.SQLException;
import java.util.List;

public class AppointmentService extends BaseService {
    private final AppointmentDao dao = new AppointmentDaoImpl();

    public AppointmentService(AuthService authService) {
        super(authService);
    }

    public Appointment findById(int id) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findById(id);
    }

    public List<Appointment> findAll() throws SQLException {
        checkPermission(Permission.READ);
        return dao.findAll();
    }

    public List<Appointment> findByCustomerId(int customerId) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findByCustomerId(customerId);
    }

    public List<Appointment> findByMechanicId(int mechanicId) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findByMechanicId(mechanicId);
    }

    public void create(Appointment appt) throws SQLException {
        checkPermission(Permission.CREATE);
        dao.insert(appt);
    }

    public void update(Appointment appt) throws SQLException {
        checkPermission(Permission.UPDATE);
        dao.update(appt);
    }

    public void delete(int id) throws SQLException {
        checkPermission(Permission.DELETE);
        dao.delete(id);
    }
}
