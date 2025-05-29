package pti.datenbank.autowerk.dao;

import pti.datenbank.autowerk.models.ServiceType;
import java.sql.SQLException;
import java.util.List;

public interface ServiceTypeDao extends GenericDao<ServiceType, Integer> {
    List<ServiceType> findByCreator(int userId) throws SQLException;
}
