package pti.datenbank.autowerk.dao;

import pti.datenbank.autowerk.models.Appointment;

import java.sql.SQLException;
import java.util.List;

public interface AppointmentDao extends GenericDao<Appointment, Integer> {
    List<Appointment> findByCustomerId(int customerId) throws SQLException;
    List<Appointment> findByMechanicId(int mechanicId) throws SQLException;
}