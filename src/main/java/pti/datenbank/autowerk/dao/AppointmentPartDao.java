package pti.datenbank.autowerk.dao;

import pti.datenbank.autowerk.models.AppointmentPart;

import java.sql.SQLException;
import java.util.List;

public interface AppointmentPartDao {
    AppointmentPart findOne(int appointmentId, int partId) throws SQLException;
    void insert(AppointmentPart entity) throws SQLException;
    void updateQuantity(int appointmentId, int partId, int quantity) throws SQLException;
    void delete(int appointmentId, int partId) throws SQLException;
    List<AppointmentPart> findByAppointmentId(int appointmentId) throws SQLException;
    List<AppointmentPart> findByPartId(int partId) throws SQLException;
}
