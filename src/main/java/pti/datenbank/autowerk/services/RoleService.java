package pti.datenbank.autowerk.services;

import pti.datenbank.autowerk.dao.Impl.RoleDaoImpl;
import pti.datenbank.autowerk.dao.RoleDao;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.Role;

import java.sql.SQLException;
import java.util.List;

public class RoleService extends BaseService {
    private final RoleDao dao = new RoleDaoImpl();

    public RoleService(AuthService authService) {
        super(authService);
    }


    public Role findById(int id) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findById(id);
    }

    public Role findByName(String name) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findByName(name);
    }


    public List<Role> findAll() throws SQLException {
        checkPermission(Permission.READ);
        return dao.findAll();
    }

    public void create(Role role) throws SQLException {
        checkPermission(Permission.CREATE);
        dao.insert(role);
    }

    public void update(Role role) throws SQLException {
        checkPermission(Permission.UPDATE);
        dao.update(role);
    }

    public void delete(int id) throws SQLException {
        checkPermission(Permission.DELETE);
        dao.delete(id);
    }
}





