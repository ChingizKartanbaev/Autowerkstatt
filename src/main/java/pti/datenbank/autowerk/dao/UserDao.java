package pti.datenbank.autowerk.dao;


import pti.datenbank.autowerk.models.User;
import java.sql.SQLException;

public interface UserDao extends GenericDao<User, Integer> {
    User findByUsername(String username) throws SQLException;
}