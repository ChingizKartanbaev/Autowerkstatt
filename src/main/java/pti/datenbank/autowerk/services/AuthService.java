package pti.datenbank.autowerk.services;

import pti.datenbank.autowerk.dao.Impl.UserDaoImpl;
import pti.datenbank.autowerk.dao.UserDao;
import pti.datenbank.autowerk.enums.Permission;
import pti.datenbank.autowerk.models.User;

import java.sql.SQLException;

public class AuthService {
    private final UserDao userDao = new UserDaoImpl();
    private User currentUser;

    public boolean login(String username, String password) throws SQLException {
        User user = userDao.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean hasPermission(Permission permission) {
        if (currentUser == null) {
            return false;
        }

        String roleName = currentUser.getRole().getRoleName();
        switch (roleName) {
            case "Admin":
                // Admin may do everything
                return true;

            case "Customer":
            case "Mechanic":
                // Both Customer and Mechanic may read, create and update,
                // but not delete
                return permission == Permission.READ
                        || permission == Permission.CREATE
                        || permission == Permission.UPDATE;

            default:
                // Any other role (or misconfigured role) gets no permissions
                return false;
        }
    }
}
