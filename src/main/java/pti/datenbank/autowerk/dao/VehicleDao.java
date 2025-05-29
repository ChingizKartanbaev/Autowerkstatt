package pti.datenbank.autowerk.dao;

import pti.datenbank.autowerk.models.Vehicle;

import java.sql.SQLException;
import java.util.List;

public interface VehicleDao extends GenericDao<Vehicle, Integer> {
    List<Vehicle> findByCustomerId(int customerId) throws SQLException;
}