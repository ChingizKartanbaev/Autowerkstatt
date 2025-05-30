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

    /** Получить пользователя по ID */
    public User findById(int id) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findById(id);
    }

    /** Получить пользователя по имени */
    public User findByUsername(String username) throws SQLException {
        checkPermission(Permission.READ);
        return dao.findByUsername(username);
    }

    /** Получить всех пользователей */
    public List<User> findAll() throws SQLException {
        checkPermission(Permission.READ);
        return dao.findAll();
    }

    /** Создать нового пользователя */
    public void create(User user) throws SQLException {
        checkPermission(Permission.CREATE);
        dao.insert(user);
    }

    /** Обновить существующего пользователя */
    public void update(User user) throws SQLException {
        checkPermission(Permission.UPDATE);
        dao.update(user);
    }

    /** Удалить пользователя по ID */
    public void delete(int id) throws SQLException {
        checkPermission(Permission.DELETE);
        dao.delete(id);
    }
}






