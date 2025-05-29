package pti.datenbank.autowerk.dao;

import java.sql.SQLException;
import java.util.List;

public interface GenericDao<T, ID> {
    T findById(ID id) throws SQLException;
    List<T> findAll() throws SQLException;
    void insert(T entity) throws SQLException;
    void update(T entity) throws SQLException;
    void delete(ID id) throws SQLException;
}