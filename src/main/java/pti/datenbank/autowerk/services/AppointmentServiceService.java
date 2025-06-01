package pti.datenbank.autowerk.services;

import pti.datenbank.autowerk.dao.AppointmentServiceDao;
import pti.datenbank.autowerk.dao.Impl.AppointmentServiceDaoImpl;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.AppointmentService;

import java.sql.SQLException;
import java.util.List;

public class AppointmentServiceService extends BaseService {
    private final AppointmentServiceDao dao = new AppointmentServiceDaoImpl();

    public AppointmentServiceService(AuthService authService) {
        super(authService);
    }

    public void create(AppointmentService as) throws SQLException {
        checkPermission(Permission.CREATE);
        dao.insert(as);
    }

    public void update(AppointmentService srv) throws SQLException {
        checkPermission(Permission.UPDATE);
        dao.update(srv);
    }

    public void delete(int appointmentId, int serviceTypeId) throws SQLException {
        checkPermission(Permission.DELETE);
        dao.delete(appointmentId, serviceTypeId);
    }

    public void deleteByAppointmentId(int appointmentId) throws SQLException {
        dao.deleteByAppointmentId(appointmentId);
    }


    public List<AppointmentService> findByAppointmentId(int appointmentId) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findByAppointmentId(appointmentId);
    }
}
