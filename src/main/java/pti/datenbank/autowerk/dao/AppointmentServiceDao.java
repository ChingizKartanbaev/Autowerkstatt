package pti.datenbank.autowerk.dao;

import pti.datenbank.autowerk.models.AppointmentService;

import java.sql.SQLException;
import java.util.List;

public interface AppointmentServiceDao {
    void insert(AppointmentService entity) throws SQLException;
    void  update(AppointmentService appointmentService) throws SQLException;
    void delete(int appointmentId, int serviceTypeId) throws SQLException;
    void deleteByAppointmentId(int appointmentId) throws SQLException;
    List<AppointmentService> findByAppointmentId(int appointmentId) throws SQLException;
    List<AppointmentService> findByServiceTypeId(int serviceTypeId) throws SQLException;
}
