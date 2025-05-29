package pti.datenbank.autowerk.dao;

import pti.datenbank.autowerk.models.Part;

import java.sql.SQLException;
import java.util.List;

public interface PartDao extends GenericDao<Part, Integer> {
    List<Part> findByCreator(int userId) throws SQLException;
}
