package pti.datenbank.autowerk.dao;

import pti.datenbank.autowerk.models.Role;

import java.sql.SQLException;

public interface RoleDao extends GenericDao<Role, Integer> {
    Role findByName(String roleName) throws SQLException;
}
