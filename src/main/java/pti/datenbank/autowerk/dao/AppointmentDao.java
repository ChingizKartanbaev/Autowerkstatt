package pti.datenbank.autowerk.dao;

import pti.datenbank.autowerk.models.Appointment;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentDao extends GenericDao<Appointment, Integer> {
    List<Appointment> findByCustomerId(int customerId) throws SQLException;
    List<Appointment> findByMechanicId(int mechanicId) throws SQLException;
    int countActiveByVehicle(int vehicleId) throws SQLException;

    boolean isMechanicAvailable(int mechanicId, LocalDateTime time) throws SQLException;
}