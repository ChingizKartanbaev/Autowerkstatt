package pti.datenbank.autowerk.services;

import pti.datenbank.autowerk.dao.Impl.UserDaoImpl;
import pti.datenbank.autowerk.dao.UserDao;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.User;

import java.sql.SQLException;
import java.util.List;

public class UserService extends BaseService {
    private final UserDao dao = new UserDaoImpl();

    public UserService(AuthService authService) {
        super(authService);
    }

    public User findById(int id) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findById(id);
    }

    public User findByUsername(String username) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findByUsername(username);
    }

    public List<User> findAll() throws SQLException {
        checkPermission(Permission.READ);
        return dao.findAll();
    }

    public void create(User user) throws SQLException {
        checkPermission(Permission.CREATE);
        dao.insert(user);
    }

    public void update(User user) throws SQLException {
        checkPermission(Permission.UPDATE);
        dao.update(user);
    }

    public void delete(int id) throws SQLException {
        checkPermission(Permission.DELETE);
        dao.delete(id);
    }
}