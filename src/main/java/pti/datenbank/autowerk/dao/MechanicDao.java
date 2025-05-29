package pti.datenbank.autowerk.dao;

import pti.datenbank.autowerk.models.Mechanic;

import java.sql.SQLException;

public interface MechanicDao extends GenericDao<Mechanic, Integer> {
    Mechanic findByUserId(int userId) throws SQLException;
}
