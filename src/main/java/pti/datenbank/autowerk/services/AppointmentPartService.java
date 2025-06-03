package pti.datenbank.autowerk.services;

import pti.datenbank.autowerk.dao.AppointmentPartDao;
import pti.datenbank.autowerk.dao.Impl.AppointmentPartDaoImpl;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.AppointmentPart;

import java.sql.SQLException;
import java.util.List;

public class AppointmentPartService extends BaseService {

    private final AppointmentPartDao appointmentPartDao;

    public AppointmentPartService(AuthService authService) {
        super(authService);
        this.appointmentPartDao = new AppointmentPartDaoImpl();
    }

    public void create(AppointmentPart ap) throws SQLException {
        checkPermission(Permission.CREATE);
        appointmentPartDao.insert(ap);
    }

    public void updateQuantity(int appointmentId, int partId, int quantity) throws SQLException {
        checkPermission(Permission.UPDATE);
        appointmentPartDao.updateQuantity(appointmentId, partId, quantity);
    }

    public void delete(int appointmentId, int partId) throws SQLException {
        checkPermission(Permission.DELETE);
        appointmentPartDao.delete(appointmentId, partId);
    }

    public List<AppointmentPart> findByAppointmentId(int appointmentId) throws SQLException {
        checkPermission(Permission.READ);
        return appointmentPartDao.findByAppointmentId(appointmentId);
    }

    public void deleteByAppointmentId(int appointmentId) throws SQLException {
        checkPermission(Permission.DELETE);
        List<AppointmentPart> list = appointmentPartDao.findByAppointmentId(appointmentId);
        for (AppointmentPart ap : list) {
            int apptId = ap.getAppointment().getAppointmentId();
            int partId = ap.getPart().getPartId();
            appointmentPartDao.delete(apptId, partId);
        }
    }

    public AppointmentPart findOne(int appointmentId, int partId) throws SQLException {
        return appointmentPartDao.findOne(appointmentId, partId);
    }
}
